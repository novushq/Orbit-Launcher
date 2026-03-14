package com.prafullkumar.orbit.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.prafullkumar.usage.presentation.UsagesRoutes
import com.prafullkumar.usage.presentation.screens.appDetail.AppDetails
import com.prafullkumar.usage.presentation.screens.usageStats.UsageScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.usageGraph(
    navController: NavHostController
) {
    navigation<Routes.UsageScreen>(startDestination = UsagesRoutes.UsageMain) {
        composable<UsagesRoutes.UsageMain> {
            UsageScreen(navController, koinViewModel())
        }
        composable<UsagesRoutes.AppDetail> {
            val packageName = it.toRoute<UsagesRoutes.AppDetail>().packageName
            AppDetails(navController, koinViewModel { parametersOf(packageName) })
        }
    }
}