package com.android.appclock.presentation.screens.history

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.appclock.core.common.FilterState
import com.android.appclock.core.common.SchedulesDataUI
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.domain.usecase.ScheduleUseCases
import com.android.appclock.utils.DateTimeUtil.getFormattedDate2
import com.android.appclock.utils.DateTimeUtil.getFormattedTime2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val useCases: ScheduleUseCases,
) : ViewModel() {

    private val _allSchedules = mutableStateListOf<SchedulesDataUI>()
    val allSchedules: SnapshotStateList<SchedulesDataUI> = _allSchedules

    private val _schedulesState = mutableStateListOf<SchedulesDataUI>()
    val schedulesState: SnapshotStateList<SchedulesDataUI> = _schedulesState

    private var getSchedulesJob: Job? = null

    private val _selectedFilter = mutableStateOf(FilterState())
    var selectedFilter: State<FilterState> = _selectedFilter

    init {
        getSchedules()
    }

    fun onEvent(event: HistoryScreenEvent) {
        when (event) {
            is HistoryScreenEvent.ShowLaunched -> applyFilter(ScheduleStatus.LAUNCHED)
            is HistoryScreenEvent.ShowFailed -> applyFilter(ScheduleStatus.FAILED)
        }
    }

    private fun getSchedules() {
        getSchedulesJob?.cancel()

        getSchedulesJob = useCases.getSchedules()
            .onEach { scheduleEntities ->
                val uiDataList = scheduleEntities.map { schedule ->
                    SchedulesDataUI(
                        appName = schedule.appName,
                        packageName = schedule.packageName,
                        scheduledTime = getFormattedTime2(schedule.scheduledDateTime),
                        scheduledDate = getFormattedDate2(schedule.scheduledDateTime),
                        description = schedule.description,
                        status = schedule.status,
                        appIcon = schedule.appIcon,
                        id = schedule.id
                    )
                }
                    .filter { it.status == ScheduleStatus.LAUNCHED || it.status == ScheduleStatus.FAILED }


                _allSchedules.clear()
                _allSchedules.addAll(uiDataList)
                _schedulesState.clear()
                _schedulesState.addAll(uiDataList)
            }
            .launchIn(viewModelScope)
    }

    private fun applyFilter(scheduleStatus: ScheduleStatus) {
        _selectedFilter.value = _selectedFilter.value.copy(scheduleStatus = scheduleStatus)

        _schedulesState.clear()
        _schedulesState.addAll(
            when (scheduleStatus) {
                ScheduleStatus.LAUNCHED -> _allSchedules.filter { it.status == ScheduleStatus.LAUNCHED }
                ScheduleStatus.FAILED -> _allSchedules.filter { it.status == ScheduleStatus.FAILED }
                else -> _allSchedules
            }
        )
    }
}
