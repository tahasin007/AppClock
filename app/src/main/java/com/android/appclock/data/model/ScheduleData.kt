package com.android.appclock.data.model

data class ScheduleData(
    val appName: String,
    val packageName: String,
    val scheduledTime: String,
    val scheduledDate: String,
    val description: String?,
    val status: ScheduleStatus,
    val id: Int? = null
)