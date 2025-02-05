package com.android.appclock.domain.usecase

import com.android.appclock.domain.model.ScheduleEntity
import com.android.appclock.domain.repository.ScheduleRepository
import javax.inject.Inject

class AddScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(schedule: ScheduleEntity) {
        repository.insertSchedule(schedule)
    }
}
