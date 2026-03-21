package com.android.appclock.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.appclock.domain.model.ScheduleAppEntity
import com.android.appclock.domain.model.UsageMonitoringAlertStateEntity
import com.android.appclock.domain.model.UsageMonitoringRuleEntity

@Database(
    entities = [
        ScheduleAppEntity::class,
        UsageMonitoringRuleEntity::class,
        UsageMonitoringAlertStateEntity::class
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
