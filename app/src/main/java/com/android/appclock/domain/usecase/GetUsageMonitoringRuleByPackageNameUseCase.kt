package com.android.appclock.domain.usecase

import com.android.appclock.domain.repository.UsageMonitoringRepository

class GetUsageMonitoringRuleByPackageNameUseCase(
    private val repository: UsageMonitoringRepository
) {
    suspend operator fun invoke(packageName: String) = repository.getRuleByPackageName(packageName)
}

