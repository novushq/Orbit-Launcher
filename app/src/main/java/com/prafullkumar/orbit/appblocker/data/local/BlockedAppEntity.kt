package com.prafullkumar.orbit.appblocker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey val packageName: String,
    val label: String,
    val isEnabled: Boolean = true
)
