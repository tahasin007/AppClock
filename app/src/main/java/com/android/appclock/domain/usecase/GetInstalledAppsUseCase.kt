package com.android.appclock.domain.usecase

import android.content.Context
import android.content.Intent
import androidx.core.graphics.drawable.toBitmap
import com.android.appclock.core.common.InstalledAppUI
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
        pm.queryIntentActivities(intent, 0).map {
            InstalledAppUI(
                appName = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName,
                icon = it.activityInfo.loadIcon(pm).toBitmap()
            )
        }.sortedBy { it.appName }
    }
}