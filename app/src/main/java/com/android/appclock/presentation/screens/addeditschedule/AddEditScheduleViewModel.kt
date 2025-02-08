package com.android.appclock.presentation.screens.addeditschedule

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.appclock.core.common.InstalledAppUI
import com.android.appclock.core.common.SchedulesDataUI
import com.android.appclock.data.alarm.AlarmScheduler
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.data.model.ScheduleValidity
import com.android.appclock.domain.model.ScheduleAppEntity
import com.android.appclock.domain.usecase.GetInstalledAppsUseCase
import com.android.appclock.domain.usecase.ScheduleUseCases
import com.android.appclock.utils.Constants.NAV_ARG_SCHEDULE_ID
import com.android.appclock.utils.Constants.SCHEDULE_ID_INVALID
import com.android.appclock.utils.DateTimeUtil
import com.android.appclock.utils.DateTimeUtil.getFormattedDate
import com.android.appclock.utils.DateTimeUtil.getFormattedDate2
import com.android.appclock.utils.DateTimeUtil.getFormattedTime
import com.android.appclock.utils.DateTimeUtil.getFormattedTime2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditScheduleViewModel @Inject constructor(
    private val scheduleUseCases: ScheduleUseCases,
    private val installedAppUseCase: GetInstalledAppsUseCase,
    private val alarmScheduler: AlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _editScheduleState = mutableStateOf(SchedulesDataUI())
    val editScheduleState: State<SchedulesDataUI> = _editScheduleState

    private val _installedApps = mutableStateListOf<InstalledAppUI>()
    val installedApps: SnapshotStateList<InstalledAppUI> = _installedApps

    private val _validityState = mutableStateOf(ScheduleValidity.VALID)
    val validityState: State<ScheduleValidity> = _validityState

    private val _schedulesState = mutableStateListOf<SchedulesDataUI>()
    private val _originalEditSchedulesState = mutableStateOf(SchedulesDataUI())

    private var getSchedulesJob: Job? = null

    init {
        val scheduleId = savedStateHandle.get<Int>(NAV_ARG_SCHEDULE_ID)
        getSchedules()
        setScheduleState(scheduleId)
        getInstalledApps()
    }

    fun onEvent(event: AddEditScheduleEvent) {
        when (event) {
            is AddEditScheduleEvent.DeleteSchedule -> deleteSchedule()
            is AddEditScheduleEvent.EnteredApp -> enterApp(event.app)
            is AddEditScheduleEvent.EnteredDate -> enterDate(event.date)
            is AddEditScheduleEvent.EnteredDescription -> enterDescription(event.description)
            is AddEditScheduleEvent.EnteredTime -> enterTime(event.hour, event.minute)
            is AddEditScheduleEvent.SaveSchedule -> saveSchedule()
            is AddEditScheduleEvent.ChangeScheduleStatus -> changeScheduleStatus(event.status)
        }
    }

    private fun changeScheduleStatus(status: ScheduleStatus) {
        if (_editScheduleState.value.status != status) {
            _editScheduleState.value = _editScheduleState.value.copy(status = status)
            validateEditSchedule()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun enterTime(hour: Int, minute: Int) {
        val formattedTime = String.format("%02d:%02d", hour, minute)
        if (_editScheduleState.value.scheduledTime != formattedTime) {
            _editScheduleState.value = _editScheduleState.value.copy(
                scheduledTime = formattedTime
            )
            validateEditSchedule()
        }
    }

    private fun enterDate(date: String) {
        if (_editScheduleState.value.scheduledDate != date) {
            _editScheduleState.value = _editScheduleState.value.copy(
                scheduledDate = date
            )
            validateEditSchedule()
        }
    }

    private fun setScheduleState(scheduleId: Int?) {
        viewModelScope.launch {
            if (scheduleId != null && scheduleId != SCHEDULE_ID_INVALID) {
                scheduleUseCases.getScheduleById(scheduleId)?.let {
                    _editScheduleState.value = _editScheduleState.value.copy(
                        appName = it.appName,
                        packageName = it.packageName,
                        scheduledTime = getFormattedTime(it.scheduledDateTime),
                        scheduledDate = getFormattedDate(it.scheduledDateTime),
                        description = it.description,
                        status = it.status,
                        appIcon = it.appIcon,
                        id = it.id
                    )
                    _originalEditSchedulesState.value = _editScheduleState.value
                }
            } else {
                val currentDateTime = System.currentTimeMillis()
                _editScheduleState.value = _editScheduleState.value.copy(
                    scheduledTime = getFormattedTime(currentDateTime),
                    scheduledDate = getFormattedDate(currentDateTime),
                )
            }
            validateEditSchedule()
        }
    }

    private fun deleteSchedule() {
        viewModelScope.launch {
            scheduleUseCases.deleteScheduleById(_editScheduleState.value.id)
        }
    }

    private fun enterApp(app: InstalledAppUI) {
        if (_editScheduleState.value.appName != app.appName) {
            _editScheduleState.value = _editScheduleState.value.copy(
                appName = app.appName,
                packageName = app.packageName,
                appIcon = app.icon
            )
            validateEditSchedule()
        }
    }

    private fun enterDescription(description: String) {
        if (_editScheduleState.value.description != description) {
            _editScheduleState.value = _editScheduleState.value.copy(description = description)
            validateEditSchedule()
        }
    }

    private fun saveSchedule() {
        val date = DateTimeUtil.validateDate(_editScheduleState.value.scheduledDate)
        val time = DateTimeUtil.validateTime(_editScheduleState.value.scheduledTime)

        val scheduledEpochMillis = DateTimeUtil.toEpochMillis(date, time)
        val newSchedule = ScheduleAppEntity(
            id = _editScheduleState.value.id,
            appName = _editScheduleState.value.appName,
            packageName = _editScheduleState.value.packageName,
            scheduledDateTime = scheduledEpochMillis,
            description = _editScheduleState.value.description?.trim(),
            status = _editScheduleState.value.status,
            appIcon = _editScheduleState.value.appIcon
        )

        viewModelScope.launch {
            val scheduleId = scheduleUseCases.addSchedule(newSchedule).toInt()
            if (scheduleId != SCHEDULE_ID_INVALID) {
                alarmScheduler.scheduleAppLaunch(
                    scheduleId,
                    newSchedule.packageName,
                    newSchedule.scheduledDateTime
                )
            }
        }
    }

    private fun getInstalledApps() {
        viewModelScope.launch {
            _installedApps.clear()
            _installedApps.addAll(installedAppUseCase.execute())
        }
    }

    private fun getSchedules() {
        getSchedulesJob?.cancel()

        getSchedulesJob = scheduleUseCases.getSchedules()
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
            }
            .launchIn(viewModelScope)
    }

    private fun validateEditSchedule() {
        val currentData = _editScheduleState.value

        // Check if package name is selected
        if (currentData.packageName.isBlank()) {
            _validityState.value = ScheduleValidity.APP_EMPTY
            return
        }

        // Check if schedule time is in the past
        val scheduledEpochMillis = DateTimeUtil.toEpochMillis(
            DateTimeUtil.validateDate(currentData.scheduledDate),
            DateTimeUtil.validateTime(currentData.scheduledTime)
        )
        if (scheduledEpochMillis < System.currentTimeMillis()) {
            _validityState.value = ScheduleValidity.TIME_IN_PAST
            return
        }

        viewModelScope.launch {
            val existingSchedules = _schedulesState

            // Check if another schedule exists with the same package & time
            val isDuplicate = existingSchedules.any {
                it.packageName == currentData.packageName &&
                        currentData.scheduledDate == it.scheduledDate &&
                        currentData.scheduledTime == it.scheduledTime &&
                        it.id != currentData.id
            }
            if (isDuplicate) {
                _validityState.value = ScheduleValidity.CONFLICTS_WITH_EXISTING_SCHEDULES
                return@launch
            }

            // If editing an existing schedule, check if any changes were made
            val isUnchanged = _originalEditSchedulesState.value == currentData &&
                    existingSchedules.any { it.id == currentData.id }

            if (isUnchanged) {
                _validityState.value = ScheduleValidity.NO_NEW_CHANGES
                return@launch
            }

            _validityState.value = ScheduleValidity.VALID
        }
    }
}
