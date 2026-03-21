package com.android.appclock.data.repository

import com.android.appclock.data.mapper.ScheduleMapper
import com.android.appclock.data.source.ScheduleDao
import com.android.appclock.domain.model.ScheduleAppEntity
import com.android.appclock.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {

    override fun getAllSchedules(): Flow<List<ScheduleAppEntity>> {
        return scheduleDao.getAllSchedules().map(ScheduleMapper::toDomainList)
    }

    override suspend fun insertSchedule(schedule: ScheduleAppEntity): Long {
        return scheduleDao.insertSchedule(ScheduleMapper.toDb(schedule))
    }

    override suspend fun updateSchedule(schedule: ScheduleAppEntity) {
        scheduleDao.updateSchedule(ScheduleMapper.toDb(schedule))
    }

    override suspend fun deleteSchedule(schedule: ScheduleAppEntity) {
        scheduleDao.deleteSchedule(ScheduleMapper.toDb(schedule))
    }

    override suspend fun getScheduleById(id: Int): ScheduleAppEntity? {
        return scheduleDao.getScheduleById(id)?.let(ScheduleMapper::toDomain)
    }

    override suspend fun deleteScheduleById(id: Int) {
        scheduleDao.deleteScheduleById(id)
    }
}