package com.android.appclock.domain.usecase

import com.android.appclock.domain.repository.UsageMonitoringRepository

class GetUsageMonitoringRulesUseCase(
    private val repository: UsageMonitoringRepository
) {
    operator fun invoke() = repository.getAllRules()
}

