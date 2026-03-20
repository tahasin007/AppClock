package com.android.appclock.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.appclock.domain.model.ScheduleAppEntity

@Database(
    entities = [ScheduleAppEntity::class],
    version = 3,
    exportSchema = true
)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        const val DATABASE_NAME = "schedule_database"
    }
}
