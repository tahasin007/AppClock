package com.android.appclock.presentation.screens.addeditschedule

import com.android.appclock.domain.model.RecurringType
import com.android.appclock.domain.model.ScheduleStatus
import com.android.appclock.presentation.common.InstalledAppUI

sealed class AddEditScheduleEvent {
    data class EnteredApp(val app: InstalledAppUI) : AddEditScheduleEvent()
    data class EnteredTime(val hour: Int, val minute: Int) : AddEditScheduleEvent()
    data class EnteredDate(val date: String) : AddEditScheduleEvent()
    data class EnteredDescription(val description: String) : AddEditScheduleEvent()
    data class EnteredRecurringType(val recurringType: RecurringType) : AddEditScheduleEvent()
    data object DeleteSchedule : AddEditScheduleEvent()
    data object SaveSchedule : AddEditScheduleEvent()
    data class ChangeScheduleStatus(val status: ScheduleStatus) : AddEditScheduleEvent()
}