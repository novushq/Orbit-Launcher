


package com.prafullkumar.orbit.home.habits.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafullkumar.orbit.home.habits.model.Habit
import com.prafullkumar.orbit.home.habits.presentation.HabitViewModel
import com.prafullkumar.orbit.home.habits.presentation.components.HabitCard
import com.prafullkumar.orbit.home.habits.presentation.components.HabitStackingSuggestion
import com.prafullkumar.orbit.home.habits.presentation.components.IdentityPromptCard
import com.prafullkumar.orbit.home.habits.presentation.components.MomentumScoreCard
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDashboardScreen(
    viewModel: HabitViewModel,
    onAddHabit: () -> Unit,
    onHabitDetail: (Habit) -> Unit
) {
    val dashboard by viewModel.dashboardState.collectAsState()
    val accentColor = Color(0xFF7C6FE0)
    val today = LocalDate.now()
    val todayEpoch = today.toEpochDay()

    var failureAnalysisHabit by remember { mutableStateOf<Habit?>(null) }
    var showIdentityPrompt by remember { mutableStateOf<String?>(null) }
    val failureSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFF090909),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                shape = CircleShape,
                containerColor = accentColor,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF888888)
                    )
                    Text(
                        "${today.dayOfMonth} ${today.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            // Momentum score
            item {
                MomentumScoreCard(score = dashboard.momentumScore)
            }

            // Identity prompt (shown after completion)
            showIdentityPrompt?.let { prompt ->
                item {
                    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                        IdentityPromptCard(prompt = prompt)
                    }
                }
            }

            // Today's habits
            item {
                Text(
                    "Today's Habits",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color(0xFF888888),
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            if (dashboard.habits.isEmpty()) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF111111))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌱", fontSize = 36.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "No habits yet.\nTap + to add your first.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF555555),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(dashboard.habits) { habit ->
                    val isCompleted = habit.todayLog?.completed == true || habit.id in dashboard.todayCompletions

                    HabitCard(
                        habit = habit,
                        isCompleted = isCompleted,
                        onToggle = {
                            viewModel.toggleHabitCompletion(habit) { nowCompleted ->
                                if (!nowCompleted) {
                                    failureAnalysisHabit = habit
                                } else {
                                    showIdentityPrompt = "You showed up. That's what builders do. ✅"
                                }
                            }
                        },
                        onDetail = { onHabitDetail(habit) },
                        onDelete = { viewModel.deleteHabit(habit.id) }
                    )
                }
            }

            // Habit stacking
            if (dashboard.stackingSuggestions.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(4.dp))
                    HabitStackingSuggestion(suggestions = dashboard.stackingSuggestions)
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // Failure analysis bottom sheet
    failureAnalysisHabit?.let { habit ->
        ModalBottomSheet(
            onDismissRequest = { failureAnalysisHabit = null },
            sheetState = failureSheetState,
            containerColor = Color(0xFF111111),
            dragHandle = null
        ) {
            FailureAnalysisBottomSheet(
                habitName = habit.name,
                onSave = { reason, energy, notes ->
                    viewModel.saveFailureAnalysis(habit.id, reason, energy, notes)
                    scope.launch { failureSheetState.hide(); failureAnalysisHabit = null }
                },
                onDismiss = {
                    scope.launch { failureSheetState.hide(); failureAnalysisHabit = null }
                }
            )
        }
    }
}
