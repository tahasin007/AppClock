package com.android.appclock.presentation.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.appclock.core.common.SchedulesDataUI
import com.android.appclock.domain.usecase.ScheduleUseCases
import com.android.appclock.utils.DateTimeUtil.getFormattedDate2
import com.android.appclock.utils.DateTimeUtil.getFormattedTime2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel

class ScheduleViewModel @Inject constructor(
    private val useCases: ScheduleUseCases,
) : ViewModel() {

    private val _schedulesState = mutableStateListOf<SchedulesDataUI>()
    val schedulesState: SnapshotStateList<SchedulesDataUI> = _schedulesState

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private var getSchedulesJob: Job? = null

    init {
        getSchedules()
    }

    private fun getSchedules() {
        getSchedulesJob?.cancel()
        _isLoading.value = true

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
                _schedulesState.clear()
                _schedulesState.addAll(uiDataList)
                delay(500)
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
}
