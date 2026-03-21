package com.android.appclock.presentation.mapper

import com.android.appclock.domain.model.UsageMonitoringRuleEntity
import com.android.appclock.presentation.common.AddEditUsageMonitoringUiState
import com.android.appclock.presentation.common.InstalledAppUI
import com.android.appclock.presentation.common.UsageMonitoringRuleUi

object UsageMonitoringUiMapper {
    fun toListItemUi(
        entity: UsageMonitoringRuleEntity,
        usageTodayMillis: Long,
        foregroundPackageName: String?
    ): UsageMonitoringRuleUi {
        return UsageMonitoringRuleUi(
            id = entity.id,
            appName = entity.appName,
            packageName = entity.packageName,
            dailyLimitMinutes = entity.dailyLimitMinutes,
            usageTodayMillis = usageTodayMillis,
            notifyAt80Percent = entity.notifyAt80Percent,
            notifyAt100Percent = entity.notifyAt100Percent,
            isActive = entity.isActive,
            isForeground = foregroundPackageName == entity.packageName
        )
    }

    fun toListItemUiList(
        entities: List<UsageMonitoringRuleEntity>,
        usageByPackage: Map<String, Long>,
        foregroundPackageName: String?
    ): List<UsageMonitoringRuleUi> {
        return entities.map { entity ->
            toListItemUi(
                entity = entity,
                usageTodayMillis = usageByPackage[entity.packageName] ?: 0L,
                foregroundPackageName = foregroundPackageName
            )
        }
    }

    fun toEditState(entity: UsageMonitoringRuleEntity): AddEditUsageMonitoringUiState {
        val hours = entity.dailyLimitMinutes / 60
        val minutes = entity.dailyLimitMinutes % 60
        return AddEditUsageMonitoringUiState(
            id = entity.id,
            selectedApp = InstalledAppUI(
                appName = entity.appName,
                packageName = entity.packageName
            ),
            hours = hours.toString(),
            minutes = minutes.toString().padStart(2, '0'),
            notifyAt80Percent = entity.notifyAt80Percent,
            notifyAt100Percent = entity.notifyAt100Percent,
            isActive = entity.isActive
        )
    }
}

