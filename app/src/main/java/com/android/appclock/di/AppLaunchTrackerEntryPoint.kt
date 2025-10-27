package com.android.appclock.di

import com.android.appclock.tracking.AppLaunchTracker
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppLaunchTrackerEntryPoint {
    fun appLaunchTracker(): AppLaunchTracker
}

