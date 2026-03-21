package com.android.appclock.domain.repository

import com.android.appclock.domain.model.UsageMonitoringRuleEntity
import kotlinx.coroutines.flow.Flow

interface UsageMonitoringRepository {
    fun getAllRules(): Flow<List<UsageMonitoringRuleEntity>>
    suspend fun insertRule(rule: UsageMonitoringRuleEntity): Long
    suspend fun updateRule(rule: UsageMonitoringRuleEntity)
    suspend fun deleteRule(rule: UsageMonitoringRuleEntity)
    suspend fun deleteRuleById(id: Int)
    suspend fun getRuleById(id: Int): UsageMonitoringRuleEntity?
    suspend fun getRuleByPackageName(packageName: String): UsageMonitoringRuleEntity?
}

