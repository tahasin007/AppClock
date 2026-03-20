package com.android.appclock.presentation.navigation

sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Home")
    data object UsageMonitoring : Screen("usage_monitoring", "Usage Monitoring")
    data object AddEditUsageMonitoring :
        Screen("add_edit_usage_monitoring", "Add/Edit Usage Monitoring")

    data object AppLaunch : Screen("app_launch", "App Launch")
    data object History : Screen("history", "History")

    data object AddEditSchedule : Screen("add_edit_schedule", "Add/Edit Schedule")
    data object ScheduleDetails : Screen("schedule_details", "Schedule Details")
}