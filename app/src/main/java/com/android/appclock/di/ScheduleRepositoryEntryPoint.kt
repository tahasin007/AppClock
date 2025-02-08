package com.android.appclock.di

import com.android.appclock.domain.repository.ScheduleRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ScheduleRepositoryEntryPoint {
    fun scheduleRepository(): ScheduleRepository
}