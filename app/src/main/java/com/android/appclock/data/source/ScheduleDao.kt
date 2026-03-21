package com.android.appclock.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.appclock.data.source.entity.ScheduleDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleDbEntity): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduleDbEntity)

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleDbEntity)

    @Query("SELECT * FROM schedules ORDER BY scheduledDateTime ASC")
    fun getAllSchedules(): Flow<List<ScheduleDbEntity>>

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Int): ScheduleDbEntity?

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Int)
}
