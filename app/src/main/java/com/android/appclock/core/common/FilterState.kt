package com.android.appclock.core.common

import com.android.appclock.domain.model.ScheduleStatus

data class FilterState(
    val scheduleStatus: ScheduleStatus = ScheduleStatus.NONE
)