
package com.prafullkumar.orbit.home.habits.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.prafullkumar.orbit.home.habits.presentation.screens.AddEditHabitScreen
import com.prafullkumar.orbit.home.habits.presentation.screens.HabitDashboardScreen
import com.prafullkumar.orbit.home.habits.presentation.screens.HabitDetailScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

sealed interface HabitRoutes {
    @Serializable data object Dashboard : HabitRoutes
    @Serializable data class Detail(val habitId: Long) : HabitRoutes
    @Serializable data class AddEdit(val habitId: Long = 0L) : HabitRoutes
}

@Composable
fun HabitTrackerRoot() {
    val navController: NavHostController = rememberNavController()
    val viewModel: HabitViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = HabitRoutes.Dashboard
    ) {
        composable<HabitRoutes.Dashboard> {
            HabitDashboardScreen(
                viewModel = viewModel,
                onAddHabit = { navController.navigate(HabitRoutes.AddEdit()) },
                onHabitDetail = { habit ->
                    navController.navigate(HabitRoutes.Detail(habit.id))
                }
            )
        }

        composable<HabitRoutes.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<HabitRoutes.Detail>()
            val habits by viewModel.habits.collectAsState()
            val habit = habits.find { it.id == route.habitId }
            habit?.let {
                HabitDetailScreen(
                    habit = it,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable<HabitRoutes.AddEdit> { backStackEntry ->
            val route = backStackEntry.toRoute<HabitRoutes.AddEdit>()
            val habits by viewModel.habits.collectAsState()
            val existingHabit = if (route.habitId != 0L) habits.find { it.id == route.habitId } else null
            AddEditHabitScreen(
                viewModel = viewModel,
                existingHabit = existingHabit,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.navigate(HabitRoutes.Dashboard) {
                        popUpTo<HabitRoutes.Dashboard> { inclusive = true }
                    }
                }
            )
        }
    }
}
