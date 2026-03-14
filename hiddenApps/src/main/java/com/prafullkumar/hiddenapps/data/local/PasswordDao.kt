package com.prafullkumar.hiddenapps.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO for password management operations
 */
@Dao
interface PasswordDao {
    
    @Query("SELECT * FROM password_table WHERE id = 1 LIMIT 1")
    suspend fun getPassword(): PasswordEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassword(password: PasswordEntity)
    
    @Query("DELETE FROM password_table")
    suspend fun deletePassword()
    
    @Query("SELECT COUNT(*) > 0 FROM password_table WHERE id = 1")
    suspend fun isPasswordSet(): Boolean
}
