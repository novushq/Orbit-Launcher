package com.prafullkumar.orbit.home.utility

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.home.habits.presentation.HabitTrackerRoot

@Composable
fun UtilityScreen(navController: NavHostController, viewModel: UtilityViewModel) {
    HabitTrackerRoot()
}
