package com.prafullkumar.usage.presentation.screens.usageStats.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.prafullkumar.usage.domain.AppUsageData
import com.prafullkumar.usage.presentation.UsagesRoutes

@Composable
fun UsageStatItem(
    usageData: AppUsageData,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,

) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                navHostController.navigate(UsagesRoutes.AppDetail(usageData.packageName))
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Icon
        AsyncImage(
            model = usageData.icon,
            contentDescription = "${usageData.appName} icon",
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        // App Information
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = usageData.appName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            Text(
                    text = formatDuration(usageData.totalTimeInForeground),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "•",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                    text = formatTimeAgo(usageData.lastTimeUsed),
                    style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        }

        // Arrow Icon
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "View details",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    }
}


/**
 * Format duration from milliseconds to readable format
 * Ensures maximum of 24 hours per day to prevent impossible values
 */
fun formatDuration(millis: Long): String {
    // Cap the duration at 24 hours (86400000 ms) to prevent impossible values
    val cappedMillis = minOf(millis, 86400000L)

    val totalMinutes = cappedMillis / (1000 * 60)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "<1m"
    }
}

/**
 * Format timestamp to relative time string (e.g., "2h ago", "1d ago")
 */
private fun formatTimeAgo(timeInMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timeInMillis

    // Convert to different time units
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 7 -> "${days}d ago"
        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "Just now"
    }
}