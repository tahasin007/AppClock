package com.android.appclock.domain.usecase

import com.android.appclock.domain.model.ScheduleAppEntity
import com.android.appclock.domain.repository.ScheduleRepository
import javax.inject.Inject

class AddScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(schedule: ScheduleAppEntity): Long {
        return repository.insertSchedule(schedule)
    }
}
