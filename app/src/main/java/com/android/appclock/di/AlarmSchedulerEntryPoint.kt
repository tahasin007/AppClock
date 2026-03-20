package com.android.appclock.di
import com.android.appclock.data.alarm.AlarmScheduler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmSchedulerEntryPoint {
    fun alarmScheduler(): AlarmScheduler
}
