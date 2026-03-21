package com.android.appclock.presentation.screens.usagemonitoring

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.appclock.core.utils.AppIconLoader
import com.android.appclock.domain.model.UsageMonitoringRuleEntity
import com.android.appclock.domain.usecase.UsageMonitoringUseCases
import com.android.appclock.presentation.common.UsageMonitoringRuleUi
import com.android.appclock.presentation.common.UsageMonitoringSummaryUi
import com.android.appclock.presentation.mapper.UsageMonitoringUiMapper
import com.android.appclock.tracking.AppUsageStatsReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val USAGE_REFRESH_INTERVAL_MILLIS = 15_000L

@HiltViewModel
class UsageMonitoringViewModel @Inject constructor(
    private val usageMonitoringUseCases: UsageMonitoringUseCases,
    private val appUsageStatsReader: AppUsageStatsReader,
    val appIconLoader: AppIconLoader
) : ViewModel() {

    private val _trackedRules = mutableStateListOf<UsageMonitoringRuleUi>()
    val trackedRules: SnapshotStateList<UsageMonitoringRuleUi> = _trackedRules

    private val _summaryState = mutableStateOf(UsageMonitoringSummaryUi())
    val summaryState: State<UsageMonitoringSummaryUi> = _summaryState

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private var currentRules: List<UsageMonitoringRuleEntity> = emptyList()
    private var refreshJob: Job? = null

    init {
        observeRules()
    }

    private fun observeRules() {
        usageMonitoringUseCases.getRules()
            .onEach { rules ->
                currentRules = rules
                refreshUsageSnapshotInternal()
            }
            .launchIn(viewModelScope)
    }

    fun refreshUsageSnapshot() {
        viewModelScope.launch {
            refreshUsageSnapshotInternal()
        }
    }

    fun startRefreshing() {
        if (refreshJob?.isActive == true) return

        refreshJob = viewModelScope.launch {
            while (true) {
                refreshUsageSnapshotInternal()
                delay(USAGE_REFRESH_INTERVAL_MILLIS)
            }
        }
    }

    fun stopRefreshing() {
        refreshJob?.cancel()
        refreshJob = null
    }

    private suspend fun refreshUsageSnapshotInternal() {
        val hasUsageAccess = appUsageStatsReader.hasUsageAccessPermission()
        val foregroundPackageName = appUsageStatsReader.getCurrentForegroundPackageName()
        val usageByPackage = appUsageStatsReader.getTodayUsageMillis(
            currentRules.map { it.packageName }.toSet()
        )

        val uiRules = UsageMonitoringUiMapper.toListItemUiList(
            entities = currentRules,
            usageByPackage = usageByPackage,
            foregroundPackageName = foregroundPackageName
        )

        _trackedRules.clear()
        _trackedRules.addAll(uiRules)

        val foregroundTrackedRule = uiRules.firstOrNull { it.packageName == foregroundPackageName }
        _summaryState.value = UsageMonitoringSummaryUi(
            hasUsageAccess = hasUsageAccess,
            foregroundAppName = when {
                !hasUsageAccess -> "Usage access required"
                foregroundTrackedRule != null -> foregroundTrackedRule.appName
                foregroundPackageName != null -> "Foreground app not tracked"
                else -> "No foreground app detected"
            },
            totalTrackedUsageMillis = uiRules.sumOf { it.usageTodayMillis },
            nearLimitCount = uiRules.count { it.isNearLimit },
            reachedLimitCount = uiRules.count { it.hasReachedLimit }
        )
        _isLoading.value = false
    }

    override fun onCleared() {
        stopRefreshing()
        super.onCleared()
    }
}
