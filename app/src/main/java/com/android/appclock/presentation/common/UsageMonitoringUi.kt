package com.android.appclock.presentation.common

import com.android.appclock.core.utils.Constants.USAGE_MONITORING_RULE_ID_DEFAULT

private const val MILLIS_PER_MINUTE = 60_000L

data class UsageMonitoringRuleUi(
    val id: Int = USAGE_MONITORING_RULE_ID_DEFAULT,
    val appName: String = "",
    val packageName: String = "",
    val dailyLimitMinutes: Int = 120,
    val usageTodayMillis: Long = 0L,
    val notifyAt80Percent: Boolean = true,
    val notifyAt100Percent: Boolean = true,
    val isActive: Boolean = true,
    val isForeground: Boolean = false
) {
    val progress: Float
        get() = if (dailyLimitMinutes <= 0) 0f else {
            (usageTodayMillis.toFloat() / (dailyLimitMinutes * MILLIS_PER_MINUTE).toFloat())
                .coerceIn(0f, 1.2f)
        }

    val hasReachedLimit: Boolean
        get() = isActive && notifyAt100Percent && progress >= 1f

    val isNearLimit: Boolean
        get() = isActive && notifyAt80Percent && progress >= 0.8f && !hasReachedLimit

    val usageLabel: String
        get() = formatDurationMillis(usageTodayMillis)

    val limitLabel: String
        get() = formatDurationMinutes(dailyLimitMinutes)

    val statusLabel: String
        get() = buildString {
            append(
                when {
                    !isActive -> "Paused"
                    hasReachedLimit -> "Limit reached"
                    isNearLimit -> "Near limit"
                    else -> "In range"
                }
            )
            if (isForeground) {
                append(" • In foreground")
            }
        }
}

data class UsageMonitoringSummaryUi(
    val hasUsageAccess: Boolean = false,
    val foregroundAppName: String = "Usage access required",
    val totalTrackedUsageMillis: Long = 0L,
    val nearLimitCount: Int = 0,
    val reachedLimitCount: Int = 0
) {
    val totalUsageTodayLabel: String
        get() = formatDurationMillis(totalTrackedUsageMillis)
}

data class AddEditUsageMonitoringUiState(
    val id: Int = USAGE_MONITORING_RULE_ID_DEFAULT,
    val selectedApp: InstalledAppUI? = null,
    val hours: String = "2",
    val minutes: String = "00",
    val notifyAt80Percent: Boolean = true,
    val notifyAt100Percent: Boolean = true,
    val isActive: Boolean = true
) {
    val dailyLimitMinutes: Int
        get() = (hours.toIntOrNull() ?: 0) * 60 + (minutes.toIntOrNull() ?: 0)

    val isNewRule: Boolean
        get() = id == USAGE_MONITORING_RULE_ID_DEFAULT
}

private fun formatDurationMinutes(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}

private fun formatDurationMillis(durationMillis: Long): String {
    val totalMinutes = (durationMillis / MILLIS_PER_MINUTE).toInt()
    return formatDurationMinutes(totalMinutes)
}

