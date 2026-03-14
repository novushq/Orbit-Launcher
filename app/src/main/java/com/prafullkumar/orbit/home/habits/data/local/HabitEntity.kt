package com.prafullkumar.orbit.home.habits.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prafullkumar.orbit.home.habits.model.EnvironmentTag
import com.prafullkumar.orbit.home.habits.model.HabitDifficulty
import com.prafullkumar.orbit.home.habits.model.HabitType

@Entity(tableName = "habits")
data class HabitEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        val emoji: String = "",
        val colorIndex: Int = 0,
        val habitType: HabitType,
        val difficulty: HabitDifficulty,
        // For X_PER_WEEK: target count; for QUANTITY: target amount; for TIMER: minutes
        val targetValue: Int = 1,
        // For SPECIFIC_DAYS: comma-separated Calendar ints e.g. "1,3,5"
        val targetDays: String = "",
        // Unit label for QUANTITY/TIMER types e.g. "pages", "minutes"
        val unit: String = "",
        val defaultEnvironment: EnvironmentTag = EnvironmentTag.ANY,
        val note: String = "",
        val reminderTime: String? = null,
        val isActive: Boolean = true,
        val createdAt: Long = System.currentTimeMillis(),
)
