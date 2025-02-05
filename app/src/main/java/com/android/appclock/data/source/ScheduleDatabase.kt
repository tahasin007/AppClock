package com.android.appclock.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.android.appclock.domain.model.ScheduleEntity

@Database(entities = [ScheduleEntity::class], version = 1, exportSchema = false)
@TypeConverters(BitmapConverter::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        const val DATABASE_NAME = "schedule_database"
    }
}
