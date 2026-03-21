package com.android.appclock.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.appclock.data.mapper.UsageMonitoringMapper
import com.android.appclock.di.UsageMonitoringWorkerEntryPoint
import com.android.appclock.domain.model.UsageMonitoringAlertStateEntity
import com.android.appclock.notifications.UsageLimitNotificationHelper
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class UsageLimitAlertWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            UsageMonitoringWorkerEntryPoint::class.java
        )
        val repository = entryPoint.usageMonitoringRepository()
        val usageStatsReader = entryPoint.appUsageStatsReader()
        val alertStateDao = entryPoint.usageMonitoringAlertStateDao()

        UsageLimitNotificationHelper.ensureChannel(applicationContext)

        if (!usageStatsReader.hasUsageAccessPermission()) {
            return Result.success()
        }

        val activeRules = repository.getAllRules().first().filter { it.isActive }
        if (activeRules.isEmpty()) {
            return Result.success()
        }

        val usageByPackage = usageStatsReader.getTodayUsageMillis(
            activeRules.map { it.packageName }.toSet()
        )

        val dateKey = LocalDate.now().toString()
        alertStateDao.deleteStatesBefore(dateKey)

        activeRules.forEach { rule ->
            val limitMillis = rule.dailyLimitMinutes * 60_000L
            if (limitMillis <= 0L) return@forEach

            val usageMillis = usageByPackage[rule.packageName] ?: 0L
            val progress = usageMillis.toFloat() / limitMillis.toFloat()

            val existingState = alertStateDao.getState(rule.id, dateKey)
                ?.let(UsageMonitoringMapper::toDomain)
                ?: UsageMonitoringAlertStateEntity(ruleId = rule.id, dateKey = dateKey)

            var notifiedAt80 = existingState.notifiedAt80
            var notifiedAt100 = existingState.notifiedAt100

            if (rule.notifyAt80Percent && !notifiedAt80 && progress >= 0.8f) {
                UsageLimitNotificationHelper.notifyNearLimit(
                    context = applicationContext,
                    ruleId = rule.id,
                    appName = rule.appName,
                    usageLabel = formatDuration(usageMillis),
                    limitLabel = formatDuration(limitMillis)
                )
                notifiedAt80 = true
            }

            if (rule.notifyAt100Percent && !notifiedAt100 && progress >= 1f) {
                UsageLimitNotificationHelper.notifyLimitReached(
                    context = applicationContext,
                    ruleId = rule.id,
                    appName = rule.appName,
                    usageLabel = formatDuration(usageMillis),
                    limitLabel = formatDuration(limitMillis)
                )
                notifiedAt100 = true
            }

            if (notifiedAt80 != existingState.notifiedAt80 ||
                notifiedAt100 != existingState.notifiedAt100
            ) {
                alertStateDao.upsertState(
                    UsageMonitoringMapper.toDb(
                        existingState.copy(
                            notifiedAt80 = notifiedAt80,
                            notifiedAt100 = notifiedAt100
                        )
                    )
                )
            }
        }

        return Result.success()
    }

    private fun formatDuration(durationMillis: Long): String {
        val totalMinutes = (durationMillis / 60_000L).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }
}

