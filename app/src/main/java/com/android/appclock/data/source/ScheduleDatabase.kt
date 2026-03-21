package com.android.appclock.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.appclock.data.source.entity.ScheduleDbEntity
import com.android.appclock.data.source.entity.UsageMonitoringAlertStateDbEntity
import com.android.appclock.data.source.entity.UsageMonitoringRuleDbEntity

@Database(
    entities = [
        ScheduleDbEntity::class,
        UsageMonitoringRuleDbEntity::class,
        UsageMonitoringAlertStateDbEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
    abstract fun usageMonitoringDao(): UsageMonitoringDao
    abstract fun usageMonitoringAlertStateDao(): UsageMonitoringAlertStateDao

    companion object {
        const val DATABASE_NAME = "schedule_database"
    }
}
