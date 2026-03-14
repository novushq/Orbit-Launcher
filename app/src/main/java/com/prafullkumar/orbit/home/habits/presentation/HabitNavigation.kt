
package com.prafullkumar.orbit.home.habits.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.prafullkumar.orbit.home.habits.presentation.screens.AddEditHabitScreen
import com.prafullkumar.orbit.home.habits.presentation.screens.HabitDashboardScreen
import com.prafullkumar.orbit.home.habits.presentation.screens.HabitDetailScreen
import com.prafullkumar.orbit.home.habits.presentation.screens.OnboardingHabitScreen
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

sealed interface HabitRoutes {
    @Serializable data object Onboarding : HabitRoutes
    @Serializable data object Dashboard : HabitRoutes
    @Serializable data class Detail(val habitId: Long) : HabitRoutes
    @Serializable data class AddEdit(val habitId: Long = 0L) : HabitRoutes
}

@Composable
fun HabitTrackerRoot() {
    val navController: NavHostController = rememberNavController()
    val viewModel: HabitViewModel = koinViewModel()
    val dashboard by viewModel.dashboardState.collectAsState()

    // Determine the one-time start destination only after loading finishes.
    // We use a remembered value so that NavHost doesn't reinitialise on recomposition.
    var startDestination by remember { mutableStateOf<HabitRoutes?>(null) }

    LaunchedEffect(dashboard.isLoading) {
        if (!dashboard.isLoading && startDestination == null) {
            startDestination = if (dashboard.showOnboarding) HabitRoutes.Onboarding
                               else HabitRoutes.Dashboard
        }
    }

    val dest = startDestination ?: return  // Wait until we know the start destination

    NavHost(
        navController = navController,
        startDestination = dest
    ) {
        composable<HabitRoutes.Onboarding> {
            OnboardingHabitScreen(
                onGetStarted = {
                    navController.navigate(HabitRoutes.AddEdit()) {
                        popUpTo<HabitRoutes.Onboarding> { inclusive = true }
                    }
                }
            )
        }

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
