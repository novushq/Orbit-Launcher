package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun WatchComposable(viewModel: HomeViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

        LaunchedEffect(Unit) {
            while (true) {
                currentTime = Calendar.getInstance()
                delay(1000)
            }
        }

        // Time and AM/PM in same row
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clickable {
                viewModel.launchClockApp(context)
            }
        ) {
            Text(
                text = SimpleDateFormat("hh:mm", Locale.getDefault()).format(currentTime.time),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = SimpleDateFormat("a", Locale.getDefault()).format(currentTime.time),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Date Display
        Text(
            text = SimpleDateFormat(
                "EEEE, MMM dd, yyyy",
                Locale.getDefault()
            ).format(currentTime.time),
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.pointerInput(Unit) {
                // Detect tap gesture
                detectTapGestures(
                    onTap = {
                        viewModel.launchCalendarApp(context)
                    }
                )
            }
        )
    }
}