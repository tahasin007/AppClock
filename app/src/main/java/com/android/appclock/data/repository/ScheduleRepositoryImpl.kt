package com.android.appclock.data.repository

import com.android.appclock.data.source.ScheduleDao
import com.android.appclock.domain.model.ScheduleAppEntity
import com.android.appclock.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleDao: ScheduleDao
) : ScheduleRepository {

    override fun getAllSchedules(): Flow<List<ScheduleAppEntity>> {
        return scheduleDao.getAllSchedules()
    }

    override suspend fun insertSchedule(schedule: ScheduleAppEntity): Long {
        return scheduleDao.insertSchedule(schedule)
    }

    override suspend fun updateSchedule(schedule: ScheduleAppEntity) {
        scheduleDao.updateSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: ScheduleAppEntity) {
        scheduleDao.deleteSchedule(schedule)
    }

    override suspend fun getScheduleById(id: Int): ScheduleAppEntity? {
        return scheduleDao.getScheduleById(id)
    }

    override suspend fun deleteScheduleById(id: Int) {
        scheduleDao.deleteScheduleById(id)
    }
}