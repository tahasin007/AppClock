package com.android.appclock.domain.usecase

import com.android.appclock.domain.repository.UsageMonitoringRepository

class GetUsageMonitoringRuleByIdUseCase(
    private val repository: UsageMonitoringRepository
) {
    suspend operator fun invoke(id: Int) = repository.getRuleById(id)
}

