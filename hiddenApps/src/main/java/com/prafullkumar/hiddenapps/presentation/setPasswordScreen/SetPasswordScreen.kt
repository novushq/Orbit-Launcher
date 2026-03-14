package com.prafullkumar.hiddenapps.presentation.setPasswordScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prafullkumar.hiddenapps.HiddenAppRoutes

private enum class PinStep { ENTER, CONFIRM }

@Composable
fun SetPasswordScreen(
    navController: NavController,
    viewModel: SetPasswordViewModel
) {
    var step by remember { mutableStateOf(PinStep.ENTER) }
    var enteredPin by remember { mutableStateOf(listOf<Int>()) }
    var confirmPin by remember { mutableStateOf(listOf<Int>()) }
    var shakeError by remember { mutableStateOf(false) }

    val shakeOffset by animateFloatAsState(
        targetValue = if (shakeError) 1f else 0f,
        animationSpec = keyframes {
            durationMillis = 400
            0f at 0
            8f at 50; -8f at 100; 8f at 150; -8f at 200; 8f at 250; -8f at 300; 0f at 400
        },
        finishedListener = { shakeError = false },
        label = "shake"
    )

    fun onDigit(d: Int) {
        if (step == PinStep.ENTER && enteredPin.size < 4) {
            val newPin = enteredPin + d
            enteredPin = newPin
            if (newPin.size == 4) { step = PinStep.CONFIRM }
        } else if (step == PinStep.CONFIRM && confirmPin.size < 4) {
            val newConfirm = confirmPin + d
            confirmPin = newConfirm
            if (newConfirm.size == 4) {
                if (enteredPin == newConfirm) {
                    viewModel.setPassword(enteredPin.joinToString("")) {
                        if (viewModel.fromDrawerFirstTime) navController.popBackStack()
                        else navController.navigate(HiddenAppRoutes.HiddenAppsMain) {
                            popUpTo(HiddenAppRoutes.SetPasswordScreen) { inclusive = true }
                        }
                    }
                } else {
                    shakeError = true
                    confirmPin = listOf()
                }
            }
        }
    }

    fun onBackspace() {
        if (step == PinStep.CONFIRM && confirmPin.isNotEmpty()) {
            confirmPin = confirmPin.dropLast(1)
        } else if (step == PinStep.ENTER && enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
        }
    }

    val bgColor = Color(0xFF090909)
    val accentColor = Color(0xFF7C6FE0)
    val currentPin = if (step == PinStep.CONFIRM) confirmPin else enteredPin

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (step == PinStep.ENTER) "Set PIN" else "Confirm PIN",
            fontSize = 26.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFFE8E8E8)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (step == PinStep.ENTER) "Enter a 4-digit PIN" else "Re-enter your PIN",
            fontSize = 14.sp,
            color = Color(0xFF888888)
        )

        Spacer(Modifier.height(40.dp))

        // PIN dots row with shake animation
        Row(
            modifier = Modifier.offset(x = shakeOffset.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { idx ->
                val filled = idx < currentPin.size
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (filled) accentColor else Color(0xFF2A2A2A))
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        // Keypad
        val keys = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9),
            listOf(-1, 0, -2)  // -1 = empty, -2 = backspace
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { key ->
                        when (key) {
                            -1 -> Spacer(Modifier.size(72.dp))
                            -2 -> {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .clickable { onBackspace() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = Color(0xFF888888),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1A1A1A))
                                        .clickable { onDigit(key) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key.toString(),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Light,
                                        color = Color(0xFFE8E8E8)
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
