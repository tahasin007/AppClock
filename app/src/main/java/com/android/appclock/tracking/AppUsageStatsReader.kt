package com.android.appclock.tracking

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class AppUsageStatsReader @Inject constructor(
    private val context: Context
) {
    private val usageStatsManager: UsageStatsManager
        get() = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    private val appOpsManager: AppOpsManager
        get() = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

    @Suppress("DEPRECATION")
    fun hasUsageAccessPermission(): Boolean {
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    suspend fun getTodayUsageMillis(packageNames: Set<String>): Map<String, Long> =
        withContext(Dispatchers.IO) {
            if (!hasUsageAccessPermission() || packageNames.isEmpty()) {
                return@withContext emptyMap()
            }

            val startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            val endTime = System.currentTimeMillis()

            usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startOfDay,
                endTime
            )
                .filter { it.packageName in packageNames }
                .groupBy { it.packageName }
                .mapValues { (_, stats) -> stats.sumOf { it.totalTimeInForeground } }
        }

    suspend fun getCurrentForegroundPackageName(): String? = withContext(Dispatchers.IO) {
        if (!hasUsageAccessPermission()) {
            return@withContext null
        }

        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 15 * 60 * 1000L
        val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()

        var foregroundPackageName: String? = null

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> foregroundPackageName = event.packageName

                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    if (foregroundPackageName == event.packageName) {
                        foregroundPackageName = null
                    }
                }
            }
        }

        foregroundPackageName
    }
}

