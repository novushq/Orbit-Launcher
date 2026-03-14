package com.prafullkumar.orbit.home.habits.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // ── Habits ──────────────────────────────────────────────────────────────
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt ASC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :id")
    suspend fun archiveHabit(id: Long)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabit(id: Long)

    // ── Logs ─────────────────────────────────────────────────────────────────
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY epochDay DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE epochDay = :epochDay")
    fun getLogsForDay(epochDay: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND epochDay = :epochDay LIMIT 1")
    suspend fun getLogForHabitOnDay(habitId: Long, epochDay: Long): HabitLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity): Long

    @Update
    suspend fun updateLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE id = :logId")
    suspend fun deleteLog(logId: Long)

    // Last 30 days logs for all habits (for dashboard stats)
    @Query("SELECT * FROM habit_logs WHERE epochDay >= :fromDay ORDER BY epochDay DESC")
    fun getLogsSince(fromDay: Long): Flow<List<HabitLogEntity>>

    // Completion count in last N days for momentum score
    @Query(
        "SELECT COUNT(*) FROM habit_logs WHERE habitId = :habitId " +
            "AND epochDay >= :fromDay AND completed = 1"
    )
    suspend fun getCompletionCountSince(habitId: Long, fromDay: Long): Int

    // All logs for a habit in a date range (for heatmap / charts)
    @Query(
        "SELECT * FROM habit_logs WHERE habitId = :habitId " +
            "AND epochDay BETWEEN :fromDay AND :toDay ORDER BY epochDay ASC"
    )
    suspend fun getLogsForHabitBetween(
        habitId: Long,
        fromDay: Long,
        toDay: Long
    ): List<HabitLogEntity>
}
