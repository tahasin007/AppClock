package com.android.appclock.core.common

enum class ScheduleValidity(val message: String) {
    VALID(
        message = "Schedule is valid"
    ),
    APP_EMPTY(
        message = "Please select an app"
    ),
    TIME_IN_PAST(
        message = "Schedule time cannot be in the past"
    ),
    NO_NEW_CHANGES(
        message = "No new changes were made"
    ),
    CONFLICTS_WITH_EXISTING_SCHEDULES(
        message = "This schedule conflicts with existing schedules"
    ),
}