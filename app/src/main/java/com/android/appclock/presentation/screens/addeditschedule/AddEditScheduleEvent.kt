package com.android.appclock.presentation.screens.addeditschedule

import com.android.appclock.core.common.InstalledAppUI
import com.android.appclock.data.model.ScheduleStatus

sealed class AddEditScheduleEvent {
    data class EnteredApp(val app: InstalledAppUI) : AddEditScheduleEvent()
    data class EnteredTime(val hour: Int, val minute: Int) : AddEditScheduleEvent()
    data class EnteredDate(val date: String) : AddEditScheduleEvent()
    data class EnteredDescription(val description: String) : AddEditScheduleEvent()
    data object DeleteSchedule : AddEditScheduleEvent()
    data object SaveSchedule : AddEditScheduleEvent()
    data class ChangeScheduleStatus(val status: ScheduleStatus) : AddEditScheduleEvent()
}