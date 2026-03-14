package com.prafullkumar.orbit.core.data.local.installedApps

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface InstalledAppsDao {

    @Query("SELECT * FROM InstalledAppsEntity")
    suspend fun getInstalledAppsList(): List<InstalledAppsEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertInstalledApps(installedAppsEntity: InstalledAppsEntity)

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertInstalledAppsList(installedAppsEntity: List<InstalledAppsEntity>)

    @androidx.room.Query("DELETE FROM InstalledAppsEntity")
    suspend fun deleteAllInstalledApps()

    @Query("DELETE FROM InstalledAppsEntity WHERE packageName = :packageName")
    suspend fun deleteInstalledApp(packageName: String)


    @Query("SELECT * FROM InstalledAppsEntity")
    fun getAllInstalledApps(): Flow<List<InstalledAppsEntity>>
}