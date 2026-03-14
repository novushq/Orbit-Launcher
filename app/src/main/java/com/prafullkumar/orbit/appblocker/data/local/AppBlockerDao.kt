package com.prafullkumar.orbit.appblocker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppBlockerDao {

    @Query("SELECT * FROM blocked_apps")
    fun getAllBlockedApps(): Flow<List<BlockedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedApp(app: BlockedAppEntity)

    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun deleteBlockedApp(packageName: String)

    @Update
    suspend fun updateBlockedApp(app: BlockedAppEntity)

    @Query("SELECT * FROM block_schedules")
    fun getAllSchedules(): Flow<List<BlockScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: BlockScheduleEntity)

    @Query("DELETE FROM block_schedules WHERE id = :id")
    suspend fun deleteSchedule(id: String)
}
