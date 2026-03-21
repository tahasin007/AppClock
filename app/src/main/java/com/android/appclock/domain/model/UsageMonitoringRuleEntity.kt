package com.android.appclock.domain.model

data class UsageMonitoringRuleEntity(
    val id: Int = 0,
    val appName: String,
    val packageName: String,
    val dailyLimitMinutes: Int,
    val notifyAt80Percent: Boolean = true,
    val notifyAt100Percent: Boolean = true,
    val isActive: Boolean = true
)

