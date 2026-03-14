package com.prafullkumar.orbit.appblocker.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.prafullkumar.orbit.appblocker.presentation.AppBlockerScreen
import com.prafullkumar.orbit.core.navigation.Routes
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.appBlockerGraph(navController: NavHostController) {
    navigation<Routes.AppBlockerScreen>(startDestination = AppBlockerRoutes.Main) {
        composable<AppBlockerRoutes.Main> {
            AppBlockerScreen(navController = navController, viewModel = koinViewModel())
        }
    }
}

sealed interface AppBlockerRoutes {
    @Serializable
    data object Main : AppBlockerRoutes
}
