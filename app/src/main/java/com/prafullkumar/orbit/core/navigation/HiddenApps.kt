package com.prafullkumar.orbit.core.navigation

import android.content.Context
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.prafullkumar.hiddenapps.HiddenAppRoutes
import com.prafullkumar.hiddenapps.presentation.hiddenAppsScreen.HiddenAppsScreen
import com.prafullkumar.hiddenapps.presentation.setPasswordScreen.SetPasswordScreen
import com.prafullkumar.hiddenapps.utils.PasswordManager
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.hiddenAppsNavGraph(navController: NavHostController, context: Context) {

    val isPasswordSet = PasswordManager(context).isPasswordSet()

    navigation<Routes.HiddenApps>(
        if (isPasswordSet) HiddenAppRoutes.HiddenAppsMain else HiddenAppRoutes.SetPasswordScreen(
            false, "", ""
        )
    ) {
        composable<HiddenAppRoutes.HiddenAppsMain> {
            HiddenAppsScreen(navController, koinViewModel())
        }

        composable<HiddenAppRoutes.SetPasswordScreen> {
            val details = it.toRoute<HiddenAppRoutes.SetPasswordScreen>()
            SetPasswordScreen(
                navController,
                koinViewModel {
                    parametersOf(
                        details.fromDrawer,
                        details.packageName,
                        details.label
                    )
                }
            )
        }
        composable<HiddenAppRoutes.UpdatePasswordScreen> {

        }
    }
}