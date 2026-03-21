package com.android.appclock.data.mapper

import com.android.appclock.data.source.entity.ScheduleDbEntity
import com.android.appclock.domain.model.ScheduleAppEntity

object ScheduleMapper {

    fun toDomain(entity: ScheduleDbEntity): ScheduleAppEntity {
        return ScheduleAppEntity(
            id = entity.id,
            appName = entity.appName,
            packageName = entity.packageName,
            scheduledDateTime = entity.scheduledDateTime,
            description = entity.description,
            status = entity.status,
            recurringType = entity.recurringType
        )
    }

    fun toDb(entity: ScheduleAppEntity): ScheduleDbEntity {
        return ScheduleDbEntity(
            id = entity.id,
            appName = entity.appName,
            packageName = entity.packageName,
            scheduledDateTime = entity.scheduledDateTime,
            description = entity.description,
            status = entity.status,
            recurringType = entity.recurringType
        )
    }

    fun toDomainList(entities: List<ScheduleDbEntity>): List<ScheduleAppEntity> {
        return entities.map(::toDomain)
    }
}