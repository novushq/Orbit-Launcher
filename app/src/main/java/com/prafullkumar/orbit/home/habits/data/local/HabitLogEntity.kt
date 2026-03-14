package com.prafullkumar.orbit.home.habits.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prafullkumar.orbit.home.habits.model.EnergyLevel
import com.prafullkumar.orbit.home.habits.model.EnvironmentTag
import com.prafullkumar.orbit.home.habits.model.FailureReason

@Entity(
        tableName = "habit_logs",
        foreignKeys =
                [
                        ForeignKey(
                                entity = HabitEntity::class,
                                parentColumns = ["id"],
                                childColumns = ["habitId"],
                                onDelete = ForeignKey.CASCADE
                        )],
        indices = [Index("habitId")]
)
data class HabitLogEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val habitId: Long,
        // Stored as epoch day (LocalDate.toEpochDay())
        val epochDay: Long,
        val completed: Boolean = false,
        // Value completed for QUANTITY/TIMER habits
        val completedValue: Int = 0,
        val failureReason: FailureReason? = null,
        val energyLevel: EnergyLevel? = null,
        val environment: EnvironmentTag = EnvironmentTag.ANY,
        val notes: String = "",
        val loggedAt: Long = System.currentTimeMillis()
)
