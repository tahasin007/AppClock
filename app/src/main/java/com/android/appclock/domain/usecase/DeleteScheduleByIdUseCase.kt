package com.android.appclock.domain.usecase

import com.android.appclock.domain.repository.ScheduleRepository
import javax.inject.Inject

class DeleteScheduleByIdUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteScheduleById(id)
    }
}