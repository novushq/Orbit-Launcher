package com.prafullkumar.orbit.core.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object HiddenApps: Routes

    @Serializable
    data object UsageScreen : Routes


    @Serializable
    data object HomeScreen : Routes


    @Serializable
    data object SettingsScreen : Routes


    @Serializable
    data object OnboardingScreen : Routes
}

sealed interface SettingsRoutes {

    @Serializable
    data object SettingsMain : SettingsRoutes

}



sealed interface HomeRoutes {


    @Serializable
    data object HomeMain : HomeRoutes

    @Serializable
    data object DrawerSettings : HomeRoutes
}