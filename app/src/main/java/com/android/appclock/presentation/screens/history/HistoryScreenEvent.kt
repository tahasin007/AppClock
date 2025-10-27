package com.android.appclock.presentation.screens.history

sealed class HistoryScreenEvent {
    data object ShowLaunched : HistoryScreenEvent()
    data object ShowFailed : HistoryScreenEvent()
    data class ToggleSelection(val scheduleId: Int) : HistoryScreenEvent()
    data object ClearSelection : HistoryScreenEvent()
    data object DeleteSelected : HistoryScreenEvent()
}