package com.android.appclock.data.source.entity

import androidx.room.Entity

@Entity(
    tableName = "usage_monitoring_alert_state",
    primaryKeys = ["ruleId", "dateKey"]
)
data class UsageMonitoringAlertStateDbEntity(
    val ruleId: Int,
    val dateKey: String,
    val notifiedAt80: Boolean = false,
    val notifiedAt100: Boolean = false
)

