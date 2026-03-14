package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import android.content.Intent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel

@Composable
fun UsageComposable(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val currentUsage by viewModel.currentUsage.collectAsStateWithLifecycle()
    Text(
        text = "Wellbeing",// formatUsageTime(currentUsage),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.apps.wellbeing")
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)

                    }
                }
            }
    )
}


fun formatUsageTime(usageInMillis: Long): String {
    if (usageInMillis < 60000) { // Less than 1 minute
        return "Today's Usage: <1m"
    }

    // Convert milliseconds to total minutes
    val totalMinutes = (usageInMillis / 60000).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return buildString {
        append("Today's Usage: ")
        if (hours > 0) {
            append("${hours}h ")
        }
        append("${minutes}m")
    }
}

