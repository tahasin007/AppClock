package com.android.appclock.domain.usecase

data class ScheduleUseCases(
    val getSchedules: GetSchedulesUseCase,
    val addSchedule: AddScheduleUseCase,
    val editSchedule: EditScheduleUseCase,
    val deleteSchedule: DeleteScheduleUseCase,
    val getScheduleById: GetScheduleByIdUseCase,
    val deleteScheduleById: DeleteScheduleByIdUseCase
)