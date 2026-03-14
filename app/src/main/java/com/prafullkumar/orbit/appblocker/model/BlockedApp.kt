package com.prafullkumar.orbit.appblocker.model

data class BlockedApp(
    val packageName: String,
    val label: String,
    val isEnabled: Boolean = true
)

data class BlockSchedule(
    val id: String,
    val name: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val activeDays: List<Int>,
    val isEnabled: Boolean = true
)
