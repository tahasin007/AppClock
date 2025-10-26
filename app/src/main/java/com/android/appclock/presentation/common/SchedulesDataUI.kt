package com.android.appclock.presentation.common

import android.graphics.Bitmap
import com.android.appclock.data.model.ScheduleStatus
import com.android.appclock.utils.Constants.SCHEDULE_ID_DEFAULT
import com.android.appclock.utils.DateTimeUtil.getFormattedDate
import com.android.appclock.utils.DateTimeUtil.getFormattedTime

data class SchedulesDataUI(
    val appName: String = "",
    val packageName: String = "",
    val scheduledTime: String = getFormattedTime(),
    val scheduledDate: String = getFormattedDate(),
    val description: String? = null,
    val status: ScheduleStatus = ScheduleStatus.UPCOMING,
    val appIcon: Bitmap? = null,
    val id: Int = SCHEDULE_ID_DEFAULT
)
