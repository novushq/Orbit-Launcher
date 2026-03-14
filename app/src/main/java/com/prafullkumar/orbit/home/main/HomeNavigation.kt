package com.prafullkumar.orbit.home.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.prafullkumar.orbit.core.navigation.HomeRoutes
import com.prafullkumar.orbit.core.navigation.Routes
import com.prafullkumar.orbit.home.PagerScreen
import com.prafullkumar.orbit.home.main.presentation.screens.drawerSettings.DrawerSettingsScreen
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.homeRoutes(navController: NavHostController) {
    navigation<Routes.HomeScreen>(startDestination = HomeRoutes.HomeMain) {
        composable<HomeRoutes.HomeMain> {
            PagerScreen(navController)
        }
        composable<HomeRoutes.DrawerSettings> {
            DrawerSettingsScreen(navController, koinViewModel())
        }
    }
}