package com.android.appclock.presentation.common

import com.android.appclock.core.utils.Constants.SCHEDULE_ID_DEFAULT
import com.android.appclock.core.utils.DateTimeUtil.getFormattedDate
import com.android.appclock.core.utils.DateTimeUtil.getFormattedTime
import com.android.appclock.domain.model.RecurringType
import com.android.appclock.domain.model.ScheduleStatus

data class SchedulesDataUI(
    val appName: String = "",
    val packageName: String = "",
    val scheduledTime: String = getFormattedTime(),
    val scheduledDate: String = getFormattedDate(),
    val description: String? = null,
    val status: ScheduleStatus = ScheduleStatus.UPCOMING,
    val recurringType: RecurringType = RecurringType.NONE,
    val id: Int = SCHEDULE_ID_DEFAULT
)
