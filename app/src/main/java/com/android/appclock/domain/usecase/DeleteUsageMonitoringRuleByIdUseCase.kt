package com.android.appclock.domain.usecase

import com.android.appclock.domain.repository.UsageMonitoringRepository

class DeleteUsageMonitoringRuleByIdUseCase(
    private val repository: UsageMonitoringRepository
) {
    suspend operator fun invoke(id: Int) = repository.deleteRuleById(id)
}

