package com.prafullkumar.hiddenapps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for hidden apps and password management
 */
@Database(
    entities = [HiddenAppsEntity::class, PasswordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class HiddenAppsDatabase : RoomDatabase() {
    abstract fun hiddenAppsDao(): HiddenAppsDao
    abstract fun passwordDao(): PasswordDao
}