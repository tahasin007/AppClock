package com.android.appclock.data.mapper

import com.android.appclock.data.source.entity.UsageMonitoringAlertStateDbEntity
import com.android.appclock.data.source.entity.UsageMonitoringRuleDbEntity
import com.android.appclock.domain.model.UsageMonitoringAlertStateEntity
import com.android.appclock.domain.model.UsageMonitoringRuleEntity

object UsageMonitoringMapper {

    fun toDomain(entity: UsageMonitoringRuleDbEntity): UsageMonitoringRuleEntity {
        return UsageMonitoringRuleEntity(
            id = entity.id,
            appName = entity.appName,
            packageName = entity.packageName,
            dailyLimitMinutes = entity.dailyLimitMinutes,
            notifyAt80Percent = entity.notifyAt80Percent,
            notifyAt100Percent = entity.notifyAt100Percent,
            isActive = entity.isActive
        )
    }

    fun toDb(entity: UsageMonitoringRuleEntity): UsageMonitoringRuleDbEntity {
        return UsageMonitoringRuleDbEntity(
            id = entity.id,
            appName = entity.appName,
            packageName = entity.packageName,
            dailyLimitMinutes = entity.dailyLimitMinutes,
            notifyAt80Percent = entity.notifyAt80Percent,
            notifyAt100Percent = entity.notifyAt100Percent,
            isActive = entity.isActive
        )
    }

    fun toDomainList(entities: List<UsageMonitoringRuleDbEntity>): List<UsageMonitoringRuleEntity> {
        return entities.map(::toDomain)
    }

    fun toDomain(entity: UsageMonitoringAlertStateDbEntity): UsageMonitoringAlertStateEntity {
        return UsageMonitoringAlertStateEntity(
            ruleId = entity.ruleId,
            dateKey = entity.dateKey,
            notifiedAt80 = entity.notifiedAt80,
            notifiedAt100 = entity.notifiedAt100
        )
    }

    fun toDb(entity: UsageMonitoringAlertStateEntity): UsageMonitoringAlertStateDbEntity {
        return UsageMonitoringAlertStateDbEntity(
            ruleId = entity.ruleId,
            dateKey = entity.dateKey,
            notifiedAt80 = entity.notifiedAt80,
            notifiedAt100 = entity.notifiedAt100
        )
    }
}

