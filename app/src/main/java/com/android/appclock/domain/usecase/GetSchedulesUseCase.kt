package com.android.appclock.domain.usecase

import com.android.appclock.domain.model.ScheduleAppEntity
import com.android.appclock.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSchedulesUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    operator fun invoke(): Flow<List<ScheduleAppEntity>> {
        return repository.getAllSchedules()
    }
}
