package com.prafullkumar.usage.presentation.screens.usageStats

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Weekly usage graph composable with navigation controls
 *
 * Function Flow:
 * 1. WeeklyGraph() - Main composable with navigation header and chart
 * 2. Navigation Row - Previous/Next week buttons with week display
 * 3. ColumnChart - Displays daily usage data as bar chart
 * 4. getWeekDisplayText() - Formats week offset to readable text
 * 5. Bar click handler - Updates selected date when user taps a bar
 */

@Composable
fun WeeklyGraph(viewModel: UsageScreenViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column {
        // Week navigation header with previous/next arrows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous week button (disabled when reached 3 weeks limit)
            IconButton(
                onClick = { viewModel.navigateToPreviousWeek() },
                enabled = uiState.weekOffset > -3
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous week",
                    tint = if (uiState.weekOffset > -3)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            // Week display text (e.g., "This Week", "Last Week", or date range)
            Text(
                text = getWeekDisplayText(uiState.weekOffset),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Next week button (disabled for future weeks)
            IconButton(
                onClick = { viewModel.navigateToNextWeek() },
                enabled = uiState.weekOffset < 0
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next week",
                    tint = if (uiState.weekOffset < 0)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }

        // Weekly usage bar chart
        ColumnChart(
            data = uiState.dailyUsage.toList().map { pair ->
                Bars(
                    values = listOf(
                        Bars.Data(
                            // Convert milliseconds to hours and cap at 24 hours maximum
                            value = minOf(pair.second.div(3600000).toDouble(), 24.0),
                            color = Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                    ),
                    // Day labels for each bar
                    label = when (pair.first) {
                        1 -> "Sun"
                        2 -> "Mon"
                        3 -> "Tue"
                        4 -> "Wed"
                        5 -> "Thu"
                        6 -> "Fri"
                        7 -> "Sat"
                        else -> ""
                    },
                )
            },
            labelProperties = LabelProperties(
                enabled = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
            ),
            modifier = Modifier.height(300.dp),
            barProperties = BarProperties(spacing = 3.dp),
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
            ),
            // Set maximum chart value with reasonable upper bound
            maxValue = minOf(
                uiState.dailyUsage.values.maxOrNull()?.div(3600000)?.toDouble()?.plus(1) ?: 1.0,
                25.0 // Maximum 25 hours on chart for better visualization
            ),
            // Handle bar clicks to update selected date
            onBarClick = { barPopupData ->
                Log.d("WeeklyGraph", "Bar clicked: ${barPopupData.dataIndex}")
                viewModel.updateSelectedDate(barPopupData.dataIndex + 1)
            }
        )
    }
}

/**
 * Generate display text for week based on offset from current week
 * @param weekOffset 0 = current week, -1 = last week, limited to -3 minimum
 * @return Formatted week display string
 */
private fun getWeekDisplayText(weekOffset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.WEEK_OF_YEAR, weekOffset)

    // Calculate start of week (Sunday)
    val startOfWeek = Calendar.getInstance().apply {
        timeInMillis = calendar.timeInMillis
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    }

    // Calculate end of week (Saturday)
    val endOfWeek = Calendar.getInstance().apply {
        timeInMillis = startOfWeek.timeInMillis
        add(Calendar.DAY_OF_YEAR, 6)
    }

    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    return when (weekOffset) {
        0 -> "This Week"
        -1 -> "Last Week"
        -2 -> "2 Weeks Ago"
        -3 -> "3 Weeks Ago"
        else -> "${dateFormat.format(startOfWeek.time)} - ${dateFormat.format(endOfWeek.time)}"
    }
}