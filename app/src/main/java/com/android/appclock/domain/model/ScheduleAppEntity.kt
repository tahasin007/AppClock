package com.android.appclock.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.appclock.data.model.RecurringType
import com.android.appclock.data.model.ScheduleStatus

@Entity(tableName = "schedules")
data class ScheduleAppEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val scheduledDateTime: Long,
    val description: String?,
    val status: ScheduleStatus,
    val recurringType: RecurringType = RecurringType.NONE
)