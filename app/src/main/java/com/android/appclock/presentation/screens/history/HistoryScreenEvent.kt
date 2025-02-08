package com.android.appclock.presentation.screens.history

sealed class HistoryScreenEvent {
    data object ShowLaunched : HistoryScreenEvent()
    data object ShowFailed : HistoryScreenEvent()
}