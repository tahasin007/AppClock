package com.android.appclock.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usage_monitoring_rules",
    indices = [Index(value = ["packageName"], unique = true)]
)
data class UsageMonitoringRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val dailyLimitMinutes: Int,
    val notifyAt80Percent: Boolean = true,
    val notifyAt100Percent: Boolean = true,
    val isActive: Boolean = true
)

