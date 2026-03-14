package com.prafullkumar.usage.presentation.screens.appDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetails(
    navController: NavHostController,
    viewModel: AppDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val accentColor = Color(0xFF7C6FE0)
    val bgColor = Color(0xFF090909)
    val cardColor = Color(0xFF111111)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isLoading) "App Details" else uiState.appName,
                        color = Color(0xFFE8E8E8)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFE8E8E8))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentColor)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = uiState.icon,
                            contentDescription = uiState.appName,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = uiState.appName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE8E8E8)
                        )
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(
                                label = "Screen Time",
                                value = formatDuration(uiState.totalTimeToday)
                            )
                            Box(
                                Modifier.width(1.dp).height(40.dp).background(Color(0xFF333333))
                            )
                            StatBox(
                                label = "Opens Today",
                                value = "${uiState.openCountToday}"
                            )
                        }
                    }
                }
            }

            // Hourly usage chart
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Hourly Activity",
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(12.dp))
                        if (uiState.hourlyUsage.isNotEmpty()) {
                            val maxVal = uiState.hourlyUsage.values.maxOrNull()?.toDouble()?.coerceAtLeast(1.0) ?: 1.0
                            ColumnChart(
                                data = (0..23).map { hour ->
                                    Bars(
                                        label = if (hour % 6 == 0) "${hour}h" else "",
                                        values = listOf(
                                            Bars.Data(
                                                value = (uiState.hourlyUsage[hour] ?: 0).toDouble(),
                                                color = Brush.verticalGradient(
                                                    listOf(accentColor, accentColor.copy(alpha = 0.5f))
                                                )
                                            )
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                barProperties = BarProperties(spacing = 1.dp),
                                maxValue = maxVal + 1,
                                labelProperties = LabelProperties(
                                    enabled = true,
                                    textStyle = TextStyle(color = Color(0xFF888888), fontSize = 9.sp)
                                ),
                                indicatorProperties = HorizontalIndicatorProperties(
                                    textStyle = TextStyle(color = Color(0xFF555555), fontSize = 9.sp)
                                )
                            )
                        } else {
                            Box(
                                Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No data for today", color = Color(0xFF555555))
                            }
                        }
                    }
                }
            }

            // Weekly chip row
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Last 7 Days",
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
                            val weeklyList = uiState.weeklyUsage.entries.toList().takeLast(7)
                            days.forEachIndexed { idx, day ->
                                val ms = weeklyList.getOrNull(idx)?.value ?: 0L
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = formatDuration(ms),
                                        fontSize = 10.sp,
                                        color = if (ms > 0) accentColor else Color(0xFF444444),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = day,
                                        fontSize = 10.sp,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE8E8E8))
        Text(label, fontSize = 12.sp, color = Color(0xFF888888))
    }
}

private fun formatDuration(millis: Long): String {
    if (millis <= 0L) return "0m"
    val totalMinutes = (millis / 60000).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}