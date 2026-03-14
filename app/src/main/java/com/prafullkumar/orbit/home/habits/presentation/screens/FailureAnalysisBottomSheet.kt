
package com.prafullkumar.orbit.home.habits.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafullkumar.orbit.home.habits.model.EnergyLevel
import com.prafullkumar.orbit.home.habits.model.FailureReason

@Composable
fun FailureAnalysisBottomSheet(
    habitName: String,
    onSave: (FailureReason, EnergyLevel, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedReason by remember { mutableStateOf<FailureReason?>(null) }
    var selectedEnergy by remember { mutableStateOf<EnergyLevel?>(null) }
    var notes by remember { mutableStateOf("") }
    val accentColor = Color(0xFF7C6FE0)

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color(0xFF111111))
            .padding(24.dp)
    ) {
        Text("What Happened?", style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold, color = Color.White
        ))
        Spacer(Modifier.height(4.dp))
        Text(
            "You skipped \"$habitName\". Let's understand why.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF888888)
        )

        Spacer(Modifier.height(20.dp))

        Text("Reason", style = MaterialTheme.typography.labelMedium, color = Color(0xFF888888))
        Spacer(Modifier.height(8.dp))

        // Failure reason chips
        val reasons = FailureReason.entries
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            reasons.chunked(3).forEach { rowReasons ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowReasons.forEach { reason ->
                        val isSelected = selectedReason == reason
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) accentColor else Color(0xFF1E1E1E))
                                .clickable { selectedReason = reason }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(reason.emoji, fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                reason.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else Color(0xFF888888)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Text("Energy Level", style = MaterialTheme.typography.labelMedium, color = Color(0xFF888888))
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EnergyLevel.values().forEach { level ->
                val isSelected = selectedEnergy == level
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) accentColor else Color(0xFF1E1E1E))
                        .clickable { selectedEnergy = level }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        when (level.value) {
                            1 -> "😴"; 2 -> "🥱"; 3 -> "😐"; 4 -> "😊"; else -> "⚡"
                        },
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        level.value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else Color(0xFF555555)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Optional notes...", color = Color(0xFF444444)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color(0xFF2A2A2A),
                cursorColor = accentColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color(0xFFCCCCCC)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(20.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text("Skip", color = Color(0xFF888888))
            }
            Button(
                onClick = {
                    val reason = selectedReason ?: FailureReason.OTHER
                    val energy = selectedEnergy ?: EnergyLevel.MEDIUM
                    onSave(reason, energy, notes)
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text("Save", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
