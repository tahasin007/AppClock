package com.android.appclock.data.mapper

import com.android.appclock.domain.model.ScheduleAppEntity
import com.android.appclock.presentation.common.SchedulesDataUI
import com.android.appclock.utils.DateTimeUtil.getFormattedDate2
import com.android.appclock.utils.DateTimeUtil.getFormattedTime2

object ScheduleMapper {

    fun toUiModel(entity: ScheduleAppEntity): SchedulesDataUI {
        return SchedulesDataUI(
            id = entity.id,
            appName = entity.appName,
            packageName = entity.packageName,
            scheduledTime = getFormattedTime2(entity.scheduledDateTime),
            scheduledDate = getFormattedDate2(entity.scheduledDateTime),
            description = entity.description,
            status = entity.status,
            recurringType = entity.recurringType
        )
    }

    fun toUiModelList(entities: List<ScheduleAppEntity>): List<SchedulesDataUI> {
        return entities.map { toUiModel(it) }
    }
}