package com.android.appclock.presentation.screens.usagemonitoring

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.appclock.domain.usecase.GetInstalledAppsUseCase
import com.android.appclock.presentation.common.InstalledAppUI
import com.android.appclock.utils.AppIconLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditUsageMonitoringViewModel @Inject constructor(
    private val installedAppsUseCase: GetInstalledAppsUseCase,
    val appIconLoader: AppIconLoader
) : ViewModel() {

    private val _installedApps = mutableStateListOf<InstalledAppUI>()
    val installedApps: SnapshotStateList<InstalledAppUI> = _installedApps

    private val _expanded = mutableStateOf(false)
    val expanded: State<Boolean> = _expanded

    private var installedAppsLoaded = false

    fun loadInstalledAppsIfNeeded() {
        if (!installedAppsLoaded) {
            viewModelScope.launch {
                installedAppsLoaded = true
                _installedApps.clear()
                _installedApps.addAll(installedAppsUseCase.execute())
            }
        }
    }

    fun toggleDropdown() {
        _expanded.value = !_expanded.value
    }
}

