package com.android.appclock.presentation.screens.usagemonitoring

import androidx.lifecycle.ViewModel
import com.android.appclock.utils.AppIconLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsageMonitoringViewModel @Inject constructor(
    val appIconLoader: AppIconLoader
) : ViewModel()

