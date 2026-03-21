package com.android.appclock.data.repository

import com.android.appclock.data.source.UsageMonitoringDao
import com.android.appclock.domain.model.UsageMonitoringRuleEntity
import com.android.appclock.domain.repository.UsageMonitoringRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsageMonitoringRepositoryImpl @Inject constructor(
    private val usageMonitoringDao: UsageMonitoringDao
) : UsageMonitoringRepository {

    override fun getAllRules(): Flow<List<UsageMonitoringRuleEntity>> =
        usageMonitoringDao.getAllRules()

    override suspend fun insertRule(rule: UsageMonitoringRuleEntity): Long =
        usageMonitoringDao.insertRule(rule)

    override suspend fun updateRule(rule: UsageMonitoringRuleEntity) {
        usageMonitoringDao.updateRule(rule)
    }

    override suspend fun deleteRule(rule: UsageMonitoringRuleEntity) {
        usageMonitoringDao.deleteRule(rule)
    }

    override suspend fun deleteRuleById(id: Int) {
        usageMonitoringDao.deleteRuleById(id)
    }

    override suspend fun getRuleById(id: Int): UsageMonitoringRuleEntity? =
        usageMonitoringDao.getRuleById(id)

    override suspend fun getRuleByPackageName(packageName: String): UsageMonitoringRuleEntity? =
        usageMonitoringDao.getRuleByPackageName(packageName)
}

