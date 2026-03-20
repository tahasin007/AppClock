package com.android.appclock.domain.usecase

import android.content.Context
import android.content.Intent
import com.android.appclock.presentation.common.InstalledAppUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val context: Context
) {
    suspend fun execute(): List<InstalledAppUI> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        pm.queryIntentActivities(intent, 0)
            .asSequence()
            .map {
                InstalledAppUI(
                    appName = it.loadLabel(pm).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.appName.lowercase() }
            .toList()
    }
}