package com.prafullkumar.hiddenapps.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HiddenAppsDao {

    @Insert(onConflict = REPLACE)
    suspend fun insert(hiddenApp: HiddenAppsEntity)

    @Query("SELECT * FROM HiddenAppsEntity")
    fun getAll(): Flow<List<HiddenAppsEntity>>

    @Update
    suspend fun update(hiddenApp: HiddenAppsEntity)

    @Delete
    suspend fun delete(hiddenApp: HiddenAppsEntity)

    @Query("DELETE FROM HiddenAppsEntity WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}
