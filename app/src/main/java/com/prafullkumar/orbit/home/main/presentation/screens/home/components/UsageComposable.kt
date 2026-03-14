package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.core.navigation.Routes
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel

@Composable
fun UsageComposable(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val currentUsage by viewModel.currentUsage.collectAsStateWithLifecycle()
    val timeText = formatUsageTime(currentUsage)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "📱 $timeText today",
            fontSize = 13.sp,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable {
                navController.navigate(Routes.UsageScreen)
            }
        )
    }
}

fun formatUsageTime(usageInMillis: Long): String {
    if (usageInMillis <= 0L) return "0m"
    val totalMinutes = (usageInMillis / 60000).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}

