package com.android.appclock.domain.usecase

import android.content.Context
import android.content.Intent
import com.android.appclock.data.model.InstalledApp
import javax.inject.Inject

class GetInstalledAppsUseCase @Inject constructor(
    private val context: Context
) {
    fun execute(): List<InstalledApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        return pm.queryIntentActivities(intent, 0).map {
            InstalledApp(
                appName = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName,
                icon = it.activityInfo.loadIcon(pm)
            )
        }
    }
}
