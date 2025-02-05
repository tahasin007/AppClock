package com.android.appclock.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.appclock.data.model.ScheduleStatus

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val scheduledDateTime: Long,
    val description: String?,
    val status: ScheduleStatus,
    val appIcon: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleEntity

        if (appIcon != null) {
            if (other.appIcon == null) return false
            if (!appIcon.contentEquals(other.appIcon)) return false
        } else if (other.appIcon != null) return false

        return true
    }

    override fun hashCode(): Int {
        return appIcon?.contentHashCode() ?: 0
    }
}