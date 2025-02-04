package com.android.appclock.presentation.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.android.appclock.data.model.ScheduleData
import com.android.appclock.data.model.ScheduleStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor() : ViewModel() {
    private val _schedules = mutableStateListOf<ScheduleData>()
    val schedules: List<ScheduleData> = _schedules

    init {
        // Dummy data
        _schedules.addAll(
            listOf(
                ScheduleData(
                    "App1",
                    "com.app1",
                    "09:00 AM",
                    "2025-02-05",
                    "Description for App1",
                    ScheduleStatus.UPCOMING
                ),
                ScheduleData(
                    "App2",
                    "com.app2",
                    "10:00 AM",
                    "2025-02-06",
                    "Description for App2",
                    ScheduleStatus.CANCELED
                ),
                ScheduleData(
                    "App3",
                    "com.app3",
                    "11:00 AM",
                    "2025-02-07",
                    "Description for App3",
                    ScheduleStatus.LAUNCHED
                ),
                ScheduleData(
                    "App4",
                    "com.app4",
                    "12:00 PM",
                    "2025-02-08",
                    "Description for App4",
                    ScheduleStatus.FAILED
                )
            )
        )
    }

    sealed class ScheduleAction {
        data class Add(val schedule: ScheduleData) : ScheduleAction()
        data class Edit(val oldSchedule: ScheduleData, val newSchedule: ScheduleData) :
            ScheduleAction()

        data class Delete(val schedule: ScheduleData) : ScheduleAction()
    }

    fun handleAction(action: ScheduleAction) {
        when (action) {
            is ScheduleAction.Add -> _schedules.add(action.schedule)
            is ScheduleAction.Edit -> {
                val index = _schedules.indexOf(action.oldSchedule)
                if (index != -1) _schedules[index] = action.newSchedule
            }

            is ScheduleAction.Delete -> _schedules.remove(action.schedule)
        }
    }
}