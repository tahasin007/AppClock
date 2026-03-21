package com.android.appclock.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.appclock.domain.model.UsageMonitoringAlertStateEntity

@Dao
interface UsageMonitoringAlertStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertState(state: UsageMonitoringAlertStateEntity)

    @Query(
        "SELECT * FROM usage_monitoring_alert_state WHERE ruleId = :ruleId AND dateKey = :dateKey LIMIT 1"
    )
    suspend fun getState(ruleId: Int, dateKey: String): UsageMonitoringAlertStateEntity?

    @Query("DELETE FROM usage_monitoring_alert_state WHERE dateKey < :dateKey")
    suspend fun deleteStatesBefore(dateKey: String)
}

