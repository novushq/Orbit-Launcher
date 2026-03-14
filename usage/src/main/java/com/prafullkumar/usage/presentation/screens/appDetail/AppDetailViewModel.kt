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
    val weeklyUsage: Map<String, Long> = emptyMap(), // "YYYY-M-D" -> durationMs
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
                val icon = try { pm.getApplicationIcon(packageName) } catch (e: PackageManager.NameNotFoundException) { null }

                val totalTime = AppUsageDetails.getAppUsageTimeToday(context, packageName) ?: 0L
                val openCount = AppUsageDetails.getNumberOfTimesAppOpens(context, packageName)
                val hourly = AppUsageDetails.getAppOpenCountByHour(context, packageName)
                val weekly = AppUsageDetails.getAppOpenCountByDay(context, packageName, 7)
                    .mapValues { (_, count) -> count.toLong() * 60_000L } // open count used as proxy for duration (1 open ≈ 1 min)

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
}