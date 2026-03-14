package com.prafullkumar.orbit.appblocker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "block_schedules")
data class BlockScheduleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val activeDays: String,
    val isEnabled: Boolean = true
)
