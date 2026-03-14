package com.prafullkumar.hiddenapps

import kotlinx.serialization.Serializable


sealed interface HiddenAppRoutes {

    @Serializable
    data object HiddenAppsMain : HiddenAppRoutes

    @Serializable
    data class SetPasswordScreen(
        val fromDrawer: Boolean, // when user is trying to hide an app first time without setting password
        val packageName: String,
        val label: String
    ) : HiddenAppRoutes

    @Serializable
    data object UpdatePasswordScreen : HiddenAppRoutes
}