
package com.prafullkumar.orbit.home.habits.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafullkumar.orbit.home.habits.model.EnvironmentTag
import com.prafullkumar.orbit.home.habits.model.Habit
import com.prafullkumar.orbit.home.habits.model.HabitDifficulty
import com.prafullkumar.orbit.home.habits.model.HabitType
import com.prafullkumar.orbit.home.habits.presentation.HabitViewModel

private val accentColor = Color(0xFF7C6FE0)
private val surfaceColor = Color(0xFF111111)
private val dimText = Color(0xFF888888)

@Composable
fun AddEditHabitScreen(
    viewModel: HabitViewModel,
    existingHabit: Habit? = null,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.addEditState.collectAsState()

    LaunchedEffect(existingHabit) {
        viewModel.resetAddEditForm(existingHabit)
    }

    val titles = listOf("Name", "Type", "Difficulty", "Schedule / Goal", "Environment")

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF090909))
    ) {
        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (state.step == 0) onBack() else viewModel.prevStep()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
            Column {
                Text(
                    if (existingHabit != null) "Edit Habit" else "New Habit",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    "Step ${state.step + 1} of ${titles.size}: ${titles[state.step]}",
                    style = MaterialTheme.typography.labelSmall,
                    color = dimText
                )
            }
        }

        // Progress bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF1E1E1E)),
        ) {
            Box(
                Modifier
                    .fillMaxWidth((state.step + 1f) / titles.size)
                    .height(4.dp)
                    .background(accentColor)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Step content
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            when (state.step) {
                0 -> StepName(state.name) { viewModel.updateName(it) }
                1 -> StepType(state.habitType) { viewModel.updateHabitType(it) }
                2 -> StepDifficulty(state.difficulty) { viewModel.updateDifficulty(it) }
                3 -> StepSchedule(state, viewModel)
                4 -> StepEnvironment(state.environment) { viewModel.updateEnvironment(it) }
            }
        }

        // Navigation buttons
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.step < titles.size - 1) {
                Button(
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    enabled = state.name.isNotBlank()
                ) {
                    Text("Next", fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = { viewModel.saveHabit { onSaved() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    enabled = state.name.isNotBlank() && !state.isSaving
                ) {
                    Text(if (state.isSaving) "Saving…" else "Save Habit", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Step 0: Name ─────────────────────────────────────────────────────────────
@Composable
private fun StepName(name: String, onChanged: (String) -> Unit) {
    Column {
        Text("What habit do you want to build?", style = MaterialTheme.typography.titleLarge.copy(
            color = Color.White, fontWeight = FontWeight.Bold
        ))
        Spacer(Modifier.height(8.dp))
        Text("Be specific. \"Go to gym\" > \"Exercise more\".", style = MaterialTheme.typography.bodySmall, color = dimText)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("e.g. Run 3km, Read 20 pages, Meditate", color = Color(0xFF444444)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color(0xFF2A2A2A),
                cursorColor = accentColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color(0xFFCCCCCC)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

// ── Step 1: Type ─────────────────────────────────────────────────────────────
@Composable
private fun StepType(selected: HabitType, onSelected: (HabitType) -> Unit) {
    Column {
        Text("How do you track it?", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(24.dp))
        HabitType.values().forEach { type ->
            val isSelected = type == selected
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) accentColor.copy(alpha = 0.15f) else surfaceColor)
                    .clickable { onSelected(type) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) accentColor else Color(0xFF333333))
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            type.displayName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isSelected) Color.White else Color(0xFFCCCCCC),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Text(type.description, style = MaterialTheme.typography.labelSmall, color = dimText)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Step 2: Difficulty ────────────────────────────────────────────────────────
@Composable
private fun StepDifficulty(selected: HabitDifficulty, onSelected: (HabitDifficulty) -> Unit) {
    Column {
        Text("How hard is this?", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Text("Effort is weighted in your momentum score.", style = MaterialTheme.typography.bodySmall, color = dimText)
        Spacer(Modifier.height(24.dp))
        HabitDifficulty.values().forEach { d ->
            val isSelected = d == selected
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) accentColor.copy(alpha = 0.15f) else surfaceColor)
                    .clickable { onSelected(d) }
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(d.emoji, fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            d.displayName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = if (isSelected) Color.White else Color(0xFFCCCCCC),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                        Text(
                            "Weight ×${d.weight.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) accentColor else dimText
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Step 3: Schedule / Goal ──────────────────────────────────────────────────
@Composable
private fun StepSchedule(
    state: com.prafullkumar.orbit.home.habits.presentation.AddEditHabitState,
    viewModel: HabitViewModel
) {
    Column {
        Text("Set your goal", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(24.dp))
        when (state.habitType) {
            HabitType.SPECIFIC_DAYS -> {
                Text("Which days?", style = MaterialTheme.typography.labelMedium, color = dimText)
                Spacer(Modifier.height(8.dp))
                val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    dayNames.forEachIndexed { idx, name ->
                        val day = idx + 1
                        val isSelected = day in state.targetDays
                        Box(
                            Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) accentColor else surfaceColor)
                                .clickable { viewModel.toggleTargetDay(day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                name.first().toString(),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else dimText
                            )
                        }
                    }
                }
            }
            HabitType.X_PER_WEEK -> {
                Text("Times per week", style = MaterialTheme.typography.labelMedium, color = dimText)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..7).forEach { n ->
                        Box(
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (state.targetValue == n) accentColor else surfaceColor)
                                .clickable { viewModel.updateTargetValue(n) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$n",
                                color = if (state.targetValue == n) Color.White else dimText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            HabitType.QUANTITY -> {
                Text("Target amount", style = MaterialTheme.typography.labelMedium, color = dimText)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.targetValue.toString(),
                        onValueChange = { it.toIntOrNull()?.let { v -> viewModel.updateTargetValue(v) } },
                        modifier = Modifier.weight(1f),
                        label = { Text("Target", color = dimText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor, unfocusedBorderColor = Color(0xFF2A2A2A),
                            cursorColor = accentColor, focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = state.unit,
                        onValueChange = { viewModel.updateUnit(it) },
                        modifier = Modifier.weight(1f),
                        label = { Text("Unit (pages, km…)", color = dimText) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor, unfocusedBorderColor = Color(0xFF2A2A2A),
                            cursorColor = accentColor, focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFCCCCCC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
            HabitType.TIMER -> {
                Text("Duration in minutes", style = MaterialTheme.typography.labelMedium, color = dimText)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.targetValue.toString(),
                    onValueChange = { it.toIntOrNull()?.let { v -> viewModel.updateTargetValue(v) } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Minutes", color = dimText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor, unfocusedBorderColor = Color(0xFF2A2A2A),
                        cursorColor = accentColor, focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            HabitType.DAILY -> {
                Text(
                    "Every day ✓\nThis habit will be tracked daily.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

// ── Step 4: Environment ───────────────────────────────────────────────────────
@Composable
private fun StepEnvironment(selected: EnvironmentTag, onSelected: (EnvironmentTag) -> Unit) {
    Column {
        Text("Where do you usually do this?", style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Text("We'll track your environment success rate over time.", style = MaterialTheme.typography.bodySmall, color = dimText)
        Spacer(Modifier.height(24.dp))
        EnvironmentTag.values().forEach { env ->
            val isSelected = env == selected
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) accentColor.copy(alpha = 0.15f) else surfaceColor)
                    .clickable { onSelected(env) }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(env.emoji, fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        env.displayName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isSelected) Color.White else Color(0xFFCCCCCC),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
