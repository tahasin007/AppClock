package com.android.appclock.domain.repository

import com.android.appclock.domain.model.ScheduleEntity
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getAllSchedules(): Flow<List<ScheduleEntity>>
    suspend fun insertSchedule(schedule: ScheduleEntity)
    suspend fun updateSchedule(schedule: ScheduleEntity)
    suspend fun deleteSchedule(schedule: ScheduleEntity)
    suspend fun getScheduleById(id: Int): ScheduleEntity?
}
