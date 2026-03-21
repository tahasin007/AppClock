package com.android.appclock.core.common

enum class ScheduleValidity(val message: String) {
    VALID(
        message = "Schedule is valid"
    ),
    APP_EMPTY(
        message = "Please select an app"
    ),
    DAILY_LIMIT_EMPTY(
        message = "Please set a daily usage limit"
    ),
    TIME_IN_PAST(
        message = "Schedule time cannot be in the past"
    ),
    NO_NEW_CHANGES(
        message = "No new changes were made"
    ),
    APP_ALREADY_TRACKED(
        message = "This app is already being monitored"
    ),
    CONFLICTS_WITH_EXISTING_SCHEDULES(
        message = "This schedule conflicts with existing schedules"
    ),
}