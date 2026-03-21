package com.android.appclock.di

import com.android.appclock.data.source.UsageMonitoringAlertStateDao
import com.android.appclock.domain.repository.UsageMonitoringRepository
import com.android.appclock.tracking.AppUsageStatsReader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UsageMonitoringWorkerEntryPoint {
    fun usageMonitoringRepository(): UsageMonitoringRepository
    fun appUsageStatsReader(): AppUsageStatsReader
    fun usageMonitoringAlertStateDao(): UsageMonitoringAlertStateDao
}

