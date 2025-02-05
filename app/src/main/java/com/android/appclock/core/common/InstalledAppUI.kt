package com.android.appclock.core.common

import android.graphics.Bitmap

data class InstalledAppUI(
    val appName: String,
    val packageName: String,
    val icon: Bitmap
)