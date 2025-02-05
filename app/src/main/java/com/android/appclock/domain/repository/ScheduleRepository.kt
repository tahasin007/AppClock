package com.android.appclock.domain.repository

import com.android.appclock.domain.model.ScheduleAppEntity
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getAllSchedules(): Flow<List<ScheduleAppEntity>>
    suspend fun insertSchedule(schedule: ScheduleAppEntity): Long
    suspend fun updateSchedule(schedule: ScheduleAppEntity)
    suspend fun deleteSchedule(schedule: ScheduleAppEntity)
    suspend fun getScheduleById(id: Int): ScheduleAppEntity?
    suspend fun deleteScheduleById(id: Int)
}
