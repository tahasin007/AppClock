package com.android.appclock.domain.model

data class UsageMonitoringAlertStateEntity(
    val ruleId: Int,
    val dateKey: String,
    val notifiedAt80: Boolean = false,
    val notifiedAt100: Boolean = false
)

