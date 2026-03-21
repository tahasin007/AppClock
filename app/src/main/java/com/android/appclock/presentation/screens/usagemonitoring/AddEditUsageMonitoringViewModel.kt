package com.android.appclock.presentation.screens.usagemonitoring

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.appclock.core.common.ScheduleValidity
import com.android.appclock.core.utils.AppIconLoader
import com.android.appclock.core.utils.Constants.NAV_ARG_USAGE_MONITORING_RULE_ID
import com.android.appclock.core.utils.Constants.USAGE_MONITORING_RULE_ID_DEFAULT
import com.android.appclock.core.utils.Constants.USAGE_MONITORING_RULE_ID_INVALID
import com.android.appclock.domain.model.UsageMonitoringRuleEntity
import com.android.appclock.domain.service.UsageAlertScheduler
import com.android.appclock.domain.usecase.GetInstalledAppsUseCase
import com.android.appclock.domain.usecase.UsageMonitoringUseCases
import com.android.appclock.presentation.common.AddEditUsageMonitoringUiState
import com.android.appclock.presentation.common.InstalledAppUI
import com.android.appclock.presentation.mapper.UsageMonitoringUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditUsageMonitoringViewModel @Inject constructor(
    private val installedAppsUseCase: GetInstalledAppsUseCase,
    private val usageMonitoringUseCases: UsageMonitoringUseCases,
    private val usageAlertScheduler: UsageAlertScheduler,
    val appIconLoader: AppIconLoader,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usageMonitoringRuleId =
        savedStateHandle.get<Int>(NAV_ARG_USAGE_MONITORING_RULE_ID)
            ?: USAGE_MONITORING_RULE_ID_INVALID

    private val _uiState = mutableStateOf(AddEditUsageMonitoringUiState())
    val uiState: State<AddEditUsageMonitoringUiState> = _uiState

    private val _originalState = mutableStateOf(AddEditUsageMonitoringUiState())

    private val _installedApps = mutableStateListOf<InstalledAppUI>()
    val installedApps: SnapshotStateList<InstalledAppUI> = _installedApps

    private val _expanded = mutableStateOf(false)
    val expanded: State<Boolean> = _expanded

    private val _validityState = mutableStateOf(ScheduleValidity.APP_EMPTY)
    val validityState: State<ScheduleValidity> = _validityState

    private var installedAppsLoaded = false

    init {
        loadRuleIfNeeded()
        revalidate()
    }

    private fun loadRuleIfNeeded() {
        if (usageMonitoringRuleId == USAGE_MONITORING_RULE_ID_INVALID) return

        viewModelScope.launch {
            usageMonitoringUseCases.getRuleById(usageMonitoringRuleId)?.let { rule ->
                val mappedState = UsageMonitoringUiMapper.toEditState(rule)
                _uiState.value = mappedState
                _originalState.value = mappedState
                revalidate()
            }
        }
    }

    fun loadInstalledAppsIfNeeded() {
        if (installedAppsLoaded) return

        viewModelScope.launch {
            installedAppsLoaded = true
            _installedApps.clear()
            _installedApps.addAll(installedAppsUseCase.execute())
        }
    }

    fun onAppSelected(app: InstalledAppUI) {
        _uiState.value = _uiState.value.copy(selectedApp = app)
        revalidate()
    }

    fun onHoursChanged(input: String) {
        if (input.all(Char::isDigit) && input.length <= 2) {
            val hours = input.toIntOrNull()
            if (hours == null || hours in 0..23) {
                _uiState.value = _uiState.value.copy(hours = input)
                revalidate()
            }
        }
    }

    fun onMinutesChanged(input: String) {
        if (input.all(Char::isDigit) && input.length <= 2) {
            val minutes = input.toIntOrNull()
            if (minutes == null || minutes in 0..59) {
                _uiState.value = _uiState.value.copy(minutes = input)
                revalidate()
            }
        }
    }

    fun toggleNotifyAt80() {
        _uiState.value = _uiState.value.copy(
            notifyAt80Percent = !_uiState.value.notifyAt80Percent
        )
        revalidate()
    }

    fun toggleNotifyAt100() {
        _uiState.value = _uiState.value.copy(
            notifyAt100Percent = !_uiState.value.notifyAt100Percent
        )
        revalidate()
    }

    fun onActiveChanged(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(isActive = isActive)
        revalidate()
    }

    fun saveRule(onSaved: () -> Unit) {
        viewModelScope.launch {
            val validity = resolveValidity(_uiState.value)
            _validityState.value = validity
            if (validity != ScheduleValidity.VALID) return@launch

            val currentState = _uiState.value
            val selectedApp = currentState.selectedApp ?: return@launch
            val entity = UsageMonitoringRuleEntity(
                id = if (currentState.id == USAGE_MONITORING_RULE_ID_INVALID) {
                    USAGE_MONITORING_RULE_ID_DEFAULT
                } else {
                    currentState.id
                },
                appName = selectedApp.appName,
                packageName = selectedApp.packageName,
                dailyLimitMinutes = currentState.dailyLimitMinutes,
                notifyAt80Percent = currentState.notifyAt80Percent,
                notifyAt100Percent = currentState.notifyAt100Percent,
                isActive = currentState.isActive
            )

            if (currentState.isNewRule) {
                usageMonitoringUseCases.addRule(entity)
            } else {
                usageMonitoringUseCases.editRule(entity)
            }
            usageAlertScheduler.ensureScheduled()
            onSaved()
        }
    }

    fun deleteRule(onDeleted: () -> Unit) {
        val currentId = _uiState.value.id
        if (currentId == USAGE_MONITORING_RULE_ID_DEFAULT ||
            currentId == USAGE_MONITORING_RULE_ID_INVALID
        ) {
            return
        }

        viewModelScope.launch {
            usageMonitoringUseCases.deleteRuleById(currentId)
            usageAlertScheduler.ensureScheduled()
            onDeleted()
        }
    }

    fun toggleDropdown() {
        _expanded.value = !_expanded.value
    }

    private fun revalidate() {
        viewModelScope.launch {
            _validityState.value = resolveValidity(_uiState.value)
        }
    }

    private suspend fun resolveValidity(state: AddEditUsageMonitoringUiState): ScheduleValidity {
        val selectedApp = state.selectedApp
        if (selectedApp == null || selectedApp.packageName.isBlank()) {
            return ScheduleValidity.APP_EMPTY
        }

        if (state.dailyLimitMinutes <= 0) {
            return ScheduleValidity.DAILY_LIMIT_EMPTY
        }

        val existingRule = usageMonitoringUseCases.getRuleByPackageName(selectedApp.packageName)
        if (existingRule != null && existingRule.id != state.id) {
            return ScheduleValidity.APP_ALREADY_TRACKED
        }

        val isExistingRule = state.id != USAGE_MONITORING_RULE_ID_DEFAULT &&
                state.id != USAGE_MONITORING_RULE_ID_INVALID
        if (isExistingRule && _originalState.value == state) {
            return ScheduleValidity.NO_NEW_CHANGES
        }

        return ScheduleValidity.VALID
    }
}
