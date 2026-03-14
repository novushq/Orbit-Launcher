
package com.prafullkumar.orbit.home.habits.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prafullkumar.orbit.home.habits.model.Habit
import com.prafullkumar.orbit.home.habits.presentation.HabitViewModel
import com.prafullkumar.orbit.home.habits.presentation.components.CompoundingProjection
import com.prafullkumar.orbit.home.habits.presentation.components.HeatmapComponent
import com.prafullkumar.orbit.home.habits.presentation.components.IdentityPromptCard
import com.prafullkumar.orbit.home.habits.presentation.components.StreakBadge
import com.prafullkumar.orbit.home.habits.presentation.components.WeeklyTrendChart

@Composable
fun HabitDetailScreen(
    habit: Habit,
    viewModel: HabitViewModel,
    onBack: () -> Unit
) {
    val stats by viewModel.selectedHabitStats.collectAsState()

    LaunchedEffect(habit.id) {
        viewModel.loadHabitStats(habit)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF090909))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Top bar
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    habit.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Text(
                    "${habit.habitType.displayName} · ${habit.difficulty.emoji} ${habit.difficulty.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (stats == null) {
            // Loading placeholder
            Text("Loading stats...", color = Color(0xFF555555))
        } else {
            val s = stats!!

            // Identity prompt
            IdentityPromptCard(prompt = s.identityPrompt)
            Spacer(Modifier.height(16.dp))

            // Streak badge
            StreakBadge(
                current = s.habit.currentStreak,
                longest = s.habit.longestStreak,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Consistency
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("Consistency", "${(s.habit.consistencyRate * 100).toInt()}%")
                StatCard("Total Done", "${s.habit.totalCompletions}")
                StatCard("Momentum", "${s.habit.momentumScore}/7")
            }

            Spacer(Modifier.height(20.dp))

            // Heatmap
            HeatmapComponent(data = s.heatmapData)
            Spacer(Modifier.height(20.dp))

            // Weekly trend
            WeeklyTrendChart(weeklyData = s.weeklyData)
            Spacer(Modifier.height(20.dp))

            // Compounding
            CompoundingProjection(
                habitName = habit.name,
                projection = s.annualProjection,
                unit = habit.unit
            )
            Spacer(Modifier.height(20.dp))

            // Environment success rates
            if (s.environmentSuccessRate.isNotEmpty()) {
                Text("Environment Success Rate", style = MaterialTheme.typography.labelMedium, color = Color(0xFF888888))
                Spacer(Modifier.height(8.dp))
                s.environmentSuccessRate.forEach { (env, rate) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${env.emoji} ${env.displayName}", color = Color(0xFFCCCCCC), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${(rate * 100).toInt()}%",
                            color = Color(0xFF7C6FE0),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF888888))
    }
}
