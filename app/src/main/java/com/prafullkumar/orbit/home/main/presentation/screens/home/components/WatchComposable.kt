package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
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
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance()
            delay(1000)
        }
    }

    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting
        Text(
            text = greeting,
            fontSize = 15.sp,
            color = Color(0xFF888888),
            fontWeight = FontWeight.Normal
        )

        Spacer(Modifier.height(8.dp))

        // Time row
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.clickable { viewModel.launchClockApp(context) }
        ) {
            Text(
                text = SimpleDateFormat("hh:mm", Locale.getDefault()).format(currentTime.time),
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFFE8E8E8),
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = SimpleDateFormat("a", Locale.getDefault()).format(currentTime.time),
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF888888),
                modifier = Modifier.padding(bottom = 14.dp)
            )
        }

        Spacer(Modifier.height(6.dp))

        // Date
        Text(
            text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(currentTime.time),
            fontSize = 14.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.clickable { viewModel.launchCalendarApp(context) }
        )
    }
}