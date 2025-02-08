package com.android.appclock.core.common

import com.android.appclock.data.model.ScheduleStatus

data class FilterState(
    val scheduleStatus: ScheduleStatus = ScheduleStatus.NONE
)