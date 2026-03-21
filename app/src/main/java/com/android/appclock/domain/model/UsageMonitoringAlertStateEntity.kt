package com.android.appclock.domain.model

import androidx.room.Entity

@Entity(
    tableName = "usage_monitoring_alert_state",
    primaryKeys = ["ruleId", "dateKey"]
)
data class UsageMonitoringAlertStateEntity(
    val ruleId: Int,
    val dateKey: String,
    val notifiedAt80: Boolean = false,
    val notifiedAt100: Boolean = false
)

