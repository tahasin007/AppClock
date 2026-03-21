package com.android.appclock.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.appclock.data.source.entity.UsageMonitoringRuleDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageMonitoringDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRule(rule: UsageMonitoringRuleDbEntity): Long

    @Update
    suspend fun updateRule(rule: UsageMonitoringRuleDbEntity)

    @Delete
    suspend fun deleteRule(rule: UsageMonitoringRuleDbEntity)

    @Query("DELETE FROM usage_monitoring_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Int)

    @Query("SELECT * FROM usage_monitoring_rules ORDER BY appName ASC")
    fun getAllRules(): Flow<List<UsageMonitoringRuleDbEntity>>

    @Query("SELECT * FROM usage_monitoring_rules WHERE id = :id")
    suspend fun getRuleById(id: Int): UsageMonitoringRuleDbEntity?

    @Query("SELECT * FROM usage_monitoring_rules WHERE packageName = :packageName LIMIT 1")
    suspend fun getRuleByPackageName(packageName: String): UsageMonitoringRuleDbEntity?
}

