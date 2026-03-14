package com.prafullkumar.orbit.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.prafullkumar.orbit.core.navigation.Routes
import com.prafullkumar.orbit.core.navigation.SettingsRoutes
import com.prafullkumar.orbit.settings.presentation.SettingsScreen
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.settingsGraph(
    navController: NavController
) {
    navigation<Routes.SettingsScreen>(startDestination = SettingsRoutes.SettingsMain) {
        composable<SettingsRoutes.SettingsMain> {
            val viewModel: com.prafullkumar.orbit.settings.presentation.SettingsViewModel = koinViewModel()
            SettingsScreen(navController = navController, viewModel = viewModel)
        }
    }
}