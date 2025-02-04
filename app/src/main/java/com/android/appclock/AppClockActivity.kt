package com.android.appclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.appclock.ui.theme.AppClockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AppClockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppClockTheme {
                AppClockContent()
            }
        }
    }
}