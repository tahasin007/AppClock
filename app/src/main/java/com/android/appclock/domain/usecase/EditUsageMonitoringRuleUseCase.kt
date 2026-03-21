package com.android.appclock.domain.usecase

import com.android.appclock.domain.model.UsageMonitoringRuleEntity
import com.android.appclock.domain.repository.UsageMonitoringRepository

class EditUsageMonitoringRuleUseCase(
    private val repository: UsageMonitoringRepository
) {
    suspend operator fun invoke(rule: UsageMonitoringRuleEntity) = repository.updateRule(rule)
}

