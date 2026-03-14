package com.prafullkumar.orbit.core.data.local.fav

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDao {


    @Query("SELECT * FROM FavEntity")
    fun getAllFavorites(): Flow<List<FavEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favEntity: FavEntity)

    @Query("DELETE FROM FavEntity WHERE packageName = :packageName")
    suspend fun deleteFavorite(packageName: String)

    @Query("SELECT COUNT(*) FROM FavEntity")
    suspend fun getFavoriteCount(): Int
}

