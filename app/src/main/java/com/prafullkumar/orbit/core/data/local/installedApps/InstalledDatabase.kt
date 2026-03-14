package com.prafullkumar.orbit.core.data.local.installedApps

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [InstalledAppsEntity::class], version = 1, exportSchema = true)
abstract class InstalledDatabase : RoomDatabase() {
    abstract fun installedAppsDao(): InstalledAppsDao
}