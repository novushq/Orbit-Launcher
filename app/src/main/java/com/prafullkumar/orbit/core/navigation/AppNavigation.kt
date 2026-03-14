package com.prafullkumar.orbit.core.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.prafullkumar.orbit.home.main.homeRoutes
import com.prafullkumar.orbit.onBoarding.OnBoardingScreen
import com.prafullkumar.orbit.settings.settingsGraph
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: Routes
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        startDestination = startDestination // Start with onboarding
    ) {
        onBoarding(navController)
        homeRoutes(navController)
        settingsGraph(navController)
        usageGraph(navController)
        hiddenAppsNavGraph(navController, context)
    }
}

fun NavGraphBuilder.onBoarding(navController: NavController) {
    navigation<Routes.OnboardingScreen>(startDestination = OnBoardingRoutes.OnBoardingMain) {
        composable<OnBoardingRoutes.OnBoardingMain> {
            OnBoardingScreen(
                viewModel = koinViewModel(),
                navController = navController
            )
        }
    }
}

sealed interface OnBoardingRoutes {
    @Serializable
    data object OnBoardingMain : OnBoardingRoutes
}