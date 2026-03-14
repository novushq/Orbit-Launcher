package com.prafullkumar.orbit.appblocker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [BlockedAppEntity::class, BlockScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppBlockerDatabase : RoomDatabase() {
    abstract fun appBlockerDao(): AppBlockerDao
}
