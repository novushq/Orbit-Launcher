
package com.prafullkumar.orbit.home.habits.data.repository

import com.prafullkumar.orbit.home.habits.data.local.HabitDao
import com.prafullkumar.orbit.home.habits.data.local.HabitEntity
import com.prafullkumar.orbit.home.habits.data.local.HabitLogEntity
import com.prafullkumar.orbit.home.habits.model.DayData
import com.prafullkumar.orbit.home.habits.model.EnvironmentTag
import com.prafullkumar.orbit.home.habits.model.Habit
import com.prafullkumar.orbit.home.habits.model.HabitLog
import com.prafullkumar.orbit.home.habits.model.HabitStats
import com.prafullkumar.orbit.home.habits.model.HabitType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HabitRepository(private val dao: HabitDao) {

    // ── Habits ────────────────────────────────────────────────────────────────

    fun getAllHabitsFlow(): Flow<List<Habit>> =
        dao.getAllActiveHabits().map { entities -> entities.map { it.toDomain() } }

    suspend fun insertHabit(habit: Habit): Long =
        dao.insertHabit(habit.toEntity())

    suspend fun updateHabit(habit: Habit) =
        dao.updateHabit(habit.toEntity())

    suspend fun archiveHabit(id: Long) = dao.archiveHabit(id)
    suspend fun deleteHabit(id: Long) = dao.deleteHabit(id)

    // ── Logs ─────────────────────────────────────────────────────────────────

    suspend fun toggleHabitForToday(
        habit: Habit,
        epochDay: Long,
        environment: EnvironmentTag
    ): Boolean {
        val existing = dao.getLogForHabitOnDay(habit.id, epochDay)
        return if (existing == null || !existing.completed) {
            val log = HabitLogEntity(
                habitId = habit.id,
                epochDay = epochDay,
                completed = true,
                environment = environment
            )
            if (existing != null) {
                dao.updateLog(log.copy(id = existing.id))
            } else {
                dao.insertLog(log)
            }
            true
        } else {
            // un-complete – return false so caller can show failure analysis
            dao.updateLog(existing.copy(completed = false))
            false
        }
    }

    suspend fun saveLog(log: HabitLog) {
        val existing = dao.getLogForHabitOnDay(log.habitId, log.epochDay)
        if (existing != null) {
            dao.updateLog(log.toEntity().copy(id = existing.id))
        } else {
            dao.insertLog(log.toEntity())
        }
    }

    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> =
        dao.getLogsForHabit(habitId).map { it.map { e -> e.toDomain() } }

    suspend fun getTodayLog(habitId: Long, today: Long): HabitLog? =
        dao.getLogForHabitOnDay(habitId, today)?.toDomain()

    // ── Stats / Analytics ────────────────────────────────────────────────────

    suspend fun getHabitStats(habit: Habit): HabitStats {
        val today = LocalDate.now().toEpochDay()
        val yearAgo = today - 365

        val allLogs = dao.getLogsForHabitBetween(habit.id, yearAgo, today)
        val completedDays = allLogs.filter { it.completed }.map { it.epochDay }.toSet()

        val currentStreak = computeCurrentStreak(completedDays, today)
        val longestStreak = computeLongestStreak(completedDays)
        val consistency = if (allLogs.size > 0)
            completedDays.size.toFloat() / allLogs.size.toFloat() else 0f

        // Heatmap last 365 days
        val heatmap = (yearAgo..today).associate { day ->
            day to (day in completedDays)
        }

        // Weekly last 7 days
        val weeklyData = (6 downTo 0).map { offset ->
            val day = today - offset
            DayData(day, day in completedDays)
        }

        // Momentum: completions in last 7 days (0..7)
        val momentumScore = (0..6).count { offset -> (today - offset) in completedDays }

        // Environment success rate
        val envMap = allLogs
            .groupBy { it.environment }
            .mapValues { (_, logs) ->
                val done = logs.count { it.completed }.toFloat()
                if (logs.isEmpty()) 0f else done / logs.size
            }

        // Annual projection: based on 30-day rate
        val last30 = allLogs.filter { it.epochDay >= today - 30 }
        val completedLast30 = last30.count { it.completed }
        val annualProjection = (completedLast30 * (365f / 30f)).toInt()

        // Identity prompt based on habit name
        val identityPrompt = buildIdentityPrompt(habit.name, currentStreak)

        val domainHabit = habit.copy(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            consistencyRate = consistency,
            momentumScore = momentumScore,
            totalCompletions = completedDays.size
        )
        return HabitStats(
            habit = domainHabit,
            logs = allLogs.map { it.toDomain() },
            weeklyData = weeklyData,
            heatmapData = heatmap,
            environmentSuccessRate = envMap,
            annualProjection = annualProjection,
            identityPrompt = identityPrompt
        )
    }

    /** Overall 7-day momentum score across all habits (0..100) */
    suspend fun getOverallMomentumScore(habits: List<Habit>): Int {
        if (habits.isEmpty()) return 0
        val today = LocalDate.now().toEpochDay()
        var total = 0
        var max = 0
        habits.forEach { h ->
            val weight = h.difficulty.weight.toInt()
            max += 7 * weight
            val completions = dao.getCompletionCountSince(h.id, today - 6)
            total += completions * weight
        }
        return if (max == 0) 0 else ((total.toFloat() / max) * 100).toInt().coerceIn(0, 100)
    }

    /** Simple stacking suggestions: "After A → try B" */
    fun buildStackingSuggestions(habits: List<Habit>): List<Pair<String, String>> {
        if (habits.size < 2) return emptyList()
        val suggestions = mutableListOf<Pair<String, String>>()
        for (i in 0 until habits.size - 1) {
            suggestions.add(Pair(habits[i].name, habits[i + 1].name))
        }
        return suggestions.take(3)
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun computeCurrentStreak(completedDays: Set<Long>, today: Long): Int {
        var streak = 0
        var day = today
        while (day in completedDays) {
            streak++
            day--
        }
        return streak
    }

    private fun computeLongestStreak(completedDays: Set<Long>): Int {
        if (completedDays.isEmpty()) return 0
        val sorted = completedDays.sorted()
        var longest = 1
        var current = 1
        for (i in 1 until sorted.size) {
            current = if (sorted[i] - sorted[i - 1] == 1L) current + 1 else 1
            if (current > longest) longest = current
        }
        return longest
    }

    private fun buildIdentityPrompt(name: String, streak: Int): String {
        val lower = name.lowercase()
        return when {
            "gym" in lower || "workout" in lower || "exercise" in lower ->
                "You showed up like an athlete. 💪"
            "read" in lower || "book" in lower ->
                "You're becoming the person who reads every day. 📖"
            "meditat" in lower || "mindful" in lower ->
                "You chose peace over noise. 🧘"
            "run" in lower || "jog" in lower ->
                "Every km is proof you keep your promises. 🏃"
            "water" in lower || "hydrat" in lower ->
                "You're building a healthier body, one sip at a time. 💧"
            "sleep" in lower ->
                "You're investing in tomorrow's energy. 😴"
            streak >= 7 ->
                "7+ days strong. Habits are becoming who you are. 🔥"
            else ->
                "You showed up. That's what builders do. ✅"
        }
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun HabitEntity.toDomain() = Habit(
        id = id, name = name, habitType = habitType, difficulty = difficulty,
        targetValue = targetValue,
        targetDays = targetDays.split(",").mapNotNull { it.trim().toIntOrNull() },
        unit = unit, defaultEnvironment = defaultEnvironment,
        note = note, createdAt = createdAt, color = color
    )

    private fun Habit.toEntity() = HabitEntity(
        id = id, name = name, habitType = habitType, difficulty = difficulty,
        targetValue = targetValue,
        targetDays = targetDays.joinToString(","),
        unit = unit, defaultEnvironment = defaultEnvironment,
        note = note, createdAt = createdAt, color = color
    )

    private fun HabitLogEntity.toDomain() = HabitLog(
        id = id, habitId = habitId, epochDay = epochDay, completed = completed,
        completedValue = completedValue, failureReason = failureReason,
        energyLevel = energyLevel, environment = environment, notes = notes
    )

    private fun HabitLog.toEntity() = HabitLogEntity(
        id = id, habitId = habitId, epochDay = epochDay, completed = completed,
        completedValue = completedValue, failureReason = failureReason,
        energyLevel = energyLevel, environment = environment, notes = notes
    )
}
