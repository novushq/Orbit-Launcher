
package com.prafullkumar.orbit.home.habits.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val habitType: HabitType,
    val difficulty: HabitDifficulty,
    val targetValue: Int = 1,
    val targetDays: List<Int> = emptyList(), // 1=Mon..7=Sun
    val unit: String = "",
    val defaultEnvironment: EnvironmentTag = EnvironmentTag.ANY,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val color: Int = 0,
    // Computed insights (filled by repository)
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val consistencyRate: Float = 0f, // 0..1
    val momentumScore: Int = 0,      // 0..7 (last 7 days)
    val totalCompletions: Int = 0,
    val todayLog: HabitLog? = null
)

data class HabitLog(
    val id: Long = 0,
    val habitId: Long,
    val epochDay: Long,
    val completed: Boolean = false,
    val completedValue: Int = 0,
    val failureReason: FailureReason? = null,
    val energyLevel: EnergyLevel? = null,
    val environment: EnvironmentTag = EnvironmentTag.ANY,
    val notes: String = ""
)

data class HabitStats(
    val habit: Habit,
    val logs: List<HabitLog>,
    val weeklyData: List<DayData>,           // last 7 days
    val heatmapData: Map<Long, Boolean>,     // epochDay -> completed
    val environmentSuccessRate: Map<EnvironmentTag, Float>,
    val annualProjection: Int,               // "At this rate: X per year"
    val identityPrompt: String
)

data class DayData(val epochDay: Long, val completed: Boolean)
