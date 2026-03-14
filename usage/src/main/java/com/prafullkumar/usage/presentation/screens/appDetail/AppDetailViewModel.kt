package com.prafullkumar.usage.presentation.screens.appDetail

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.usage.data.AppUsageDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppDetailUiState(
    val appName: String = "",
    val packageName: String = "",
    val icon: Drawable? = null,
    val totalTimeToday: Long = 0L,
    val openCountToday: Int = 0,
    val hourlyUsage: Map<Int, Int> = emptyMap(),   // hour -> open count
    val weeklyUsage: Map<String, Long> = emptyMap(), // "YYYY-M-D" -> actual usage durationMs
    val isLoading: Boolean = true
)

class AppDetailViewModel(
    private val packageName: String,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val appName = pm.getApplicationLabel(appInfo).toString()
                val icon = try { pm.getApplicationIcon(packageName) } catch (e: PackageManager.NameNotFoundException) {
                    android.util.Log.w("AppDetailViewModel", "Icon not found for $packageName", e)
                    null
                }

                val totalTime = AppUsageDetails.getAppUsageTimeToday(context, packageName) ?: 0L
                val openCount = AppUsageDetails.getNumberOfTimesAppOpens(context, packageName)
                val hourly = AppUsageDetails.getAppOpenCountByHour(context, packageName)
                val weekly = getWeeklyUsageMs()

                _uiState.update {
                    it.copy(
                        appName = appName,
                        packageName = packageName,
                        icon = icon,
                        totalTimeToday = totalTime,
                        openCountToday = openCount,
                        hourlyUsage = hourly,
                        weeklyUsage = weekly,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /** Returns actual screen-time (ms) per day for the last 7 days. */
    private fun getWeeklyUsageMs(): Map<String, Long> {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as android.app.usage.UsageStatsManager
        val result = mutableMapOf<String, Long>()
        val calendar = java.util.Calendar.getInstance()

        repeat(7) {
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH) + 1
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            val dateKey = "$year-$month-$day"

            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val startMs = calendar.timeInMillis
            val endMs = startMs + 86_400_000L

            val stats = usageStatsManager.queryUsageStats(
                android.app.usage.UsageStatsManager.INTERVAL_DAILY, startMs, endMs
            )
            val durationMs = stats
                .filter { it.packageName == packageName }
                .sumOf { it.totalTimeInForeground }
            result[dateKey] = durationMs

            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        return result
    }
}