package com.android.appclock.presentation.common

import android.graphics.Bitmap

data class InstalledAppUI(
    val appName: String,
    val packageName: String,
    val icon: Bitmap
)
