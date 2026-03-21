package com.android.appclock.domain.usecase

data class UsageMonitoringUseCases(
    val getRules: GetUsageMonitoringRulesUseCase,
    val addRule: AddUsageMonitoringRuleUseCase,
    val editRule: EditUsageMonitoringRuleUseCase,
    val getRuleById: GetUsageMonitoringRuleByIdUseCase,
    val getRuleByPackageName: GetUsageMonitoringRuleByPackageNameUseCase,
    val deleteRuleById: DeleteUsageMonitoringRuleByIdUseCase
)

