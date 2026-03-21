package com.android.appclock.domain.model

data class ScheduleAppEntity(
    val id: Int = 0,
    val appName: String,
    val packageName: String,
    val scheduledDateTime: Long,
    val description: String?,
    val status: ScheduleStatus,
    val recurringType: RecurringType = RecurringType.NONE
)