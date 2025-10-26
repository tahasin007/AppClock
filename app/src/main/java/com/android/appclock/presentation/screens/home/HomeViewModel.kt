package com.android.appclock.presentation.screens.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.appclock.data.alarm.AlarmScheduler
import com.android.appclock.data.mapper.ScheduleMapper
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.domain.usecase.ScheduleUseCases
import com.android.appclock.presentation.common.SchedulesDataUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: ScheduleUseCases,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _schedulesState = mutableStateListOf<SchedulesDataUI>()
    val schedulesState: SnapshotStateList<SchedulesDataUI> = _schedulesState

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private var getSchedulesJob: Job? = null

    init {
        updateAndGetSchedules()
    }

    private fun updateAndGetSchedules() {
        viewModelScope.launch {
            val nowMillis = System.currentTimeMillis()
            val schedules = useCases.getSchedules().first()

            schedules.forEach { schedule ->
                if (schedule.status == ScheduleStatus.UPCOMING && schedule.scheduledDateTime < nowMillis) {
                    useCases.editSchedule(schedule.copy(status = ScheduleStatus.FAILED))
                    alarmScheduler.cancelScheduledLaunch(schedule.id)
                }
            }

            getSchedules()
        }
    }

    private fun getSchedules() {
        getSchedulesJob?.cancel()
        _isLoading.value = true

        getSchedulesJob = useCases.getSchedules()
            .onEach { scheduleEntities ->
                val uiDataList = ScheduleMapper.toUiModelList(scheduleEntities)

                _schedulesState.clear()

                val sortedSchedules = uiDataList
                    .filter { it.status == ScheduleStatus.UPCOMING } +
                        uiDataList.filter { it.status == ScheduleStatus.CANCELED }

                _schedulesState.addAll(sortedSchedules)
                _isLoading.value = false
            }
            .launchIn(viewModelScope)
    }
}
