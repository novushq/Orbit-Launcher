package com.prafullkumar.usage.presentation.screens.usageStats

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.usage.data.AppInfo
import com.prafullkumar.usage.domain.AppUsageData
import com.prafullkumar.usage.data.UsageDetails
import com.prafullkumar.usage.data.getInstalledAppsMain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * UI state data class containing all screen state information
 */
data class UiState(
    val appUsageList: Map<Int, List<AppUsageData>> = emptyMap(), // Usage data grouped by day of week
    val dailyUsage: Map<Int, Long> = emptyMap(), // Total usage time per day for chart display
    val loading: Boolean = true, // Loading state indicator
    val error: String? = null, // Error message if any
    val weekOffset: Int = 0 // Week offset: 0=current, -1=previous, etc.
)

/**
 * ViewModel for managing usage statistics screen state and business logic
 * 
 * Function Flow:
 * 1. init() - Initializes ViewModel and calls setup()
 * 2. setup() - Loads installed apps and initial week data
 * 3. loadWeekData() - Fetches and processes usage data for specific week
 * 4. navigateToPreviousWeek() - Moves to previous week and refreshes data
 * 5. navigateToNextWeek() - Moves to next week (if not future) and refreshes data
 * 6. updateSelectedDate() - Updates currently selected day for detail view
 */
class UsageScreenViewModel(
    private val context: Context
) : ViewModel() {

    // List of all installed apps on the device
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps = _installedApps.asStateFlow()

    // Currently selected day of the week (for detail view)
    private val _selectedDate = MutableStateFlow(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
    val selectedDate = _selectedDate.asStateFlow()

    // Main UI state containing all screen data
    private val _uiState = MutableStateFlow(UiState(loading = true))
    val uiState = _uiState.asStateFlow()

    init {
        setup()
    }

    /**
     * Initialize the ViewModel by loading installed apps and usage data
     */
    fun setup() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            try {
                // Load all installed applications
                val apps = getInstalledAppsMain(context)
                _installedApps.update { apps }

                // Extract package names for usage query
                val packageNames = apps.map { it.packageName }.toSet()
                
                // Load initial week data (current week)
                loadWeekData(packageNames, _uiState.value.weekOffset)

            } catch (e: Exception) {
                Log.e("UsageScreenViewModel", "Error loading usage data", e)
                _uiState.update { it.copy(loading = false, error = e.message ?: "Error") }
            }
        }
    }

    /**
     * Load usage data for a specific week and update UI state
     * @param packageNames Set of package names to query
     * @param weekOffset Week offset from current week
     */
    private fun loadWeekData(packageNames: Set<String>, weekOffset: Int) {
        try {
            // Fetch usage data for the specified week
            val weekData = UsageDetails.getWeekPhoneUsageData(context, packageNames, weekOffset)
            
            // Calculate daily totals for chart display (convert to hours and cap at 24)
            val dailyTotals = weekData.mapValues { entry -> 
                val totalMs = entry.value.sumOf { stats -> stats.totalTimeInForeground }
                // Cap daily usage at 24 hours maximum to prevent impossible values
                minOf(totalMs, 86400000L) // 24 hours in milliseconds
            }
            
            _uiState.update {
                it.copy(
                    appUsageList = weekData,
                    loading = false,
                    weekOffset = weekOffset,
                    dailyUsage = dailyTotals
                )
            }
        } catch (e: Exception) {
            Log.e("UsageScreenViewModel", "Error loading week data", e)
            _uiState.update { 
                it.copy(
                    loading = false, 
                    error = "Failed to load usage data: ${e.message}"
                ) 
            }
        }
    }

    /**
     * Navigate to the previous week and refresh data
     * Limited to maximum 3 weeks back
     */
    fun navigateToPreviousWeek() {
        val newOffset = _uiState.value.weekOffset - 1
        // Limit to maximum 3 weeks back (weekOffset = -3)
        if (newOffset >= -3) {
            val packageNames = _installedApps.value.map { it.packageName }.toSet()
            _uiState.update { it.copy(loading = true) }
            loadWeekData(packageNames, newOffset)
        }
    }

    /**
     * Navigate to the next week (only if not in future) and refresh data
     */
    fun navigateToNextWeek() {
        val newOffset = _uiState.value.weekOffset + 1
        // Prevent navigation to future weeks
        if (newOffset <= 0) {
            val packageNames = _installedApps.value.map { it.packageName }.toSet()
            _uiState.update { it.copy(loading = true) }
            loadWeekData(packageNames, newOffset)
        }
    }

    /**
     * Update the selected day for detailed app usage view
     * @param day Day of week (Calendar.SUNDAY to Calendar.SATURDAY)
     */
    fun updateSelectedDate(day: Int) {
        _selectedDate.update { day }
    }
}