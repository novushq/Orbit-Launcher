package com.prafullkumar.usage.presentation

import kotlinx.serialization.Serializable

sealed interface UsagesRoutes {

    @Serializable
    data object UsageMain : UsagesRoutes


    @Serializable
    data class AppDetail(val packageName: String) : UsagesRoutes
}