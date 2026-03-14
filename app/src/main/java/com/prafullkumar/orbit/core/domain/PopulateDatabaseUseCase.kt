package com.prafullkumar.orbit.core.domain

import android.content.Context
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledAppsRepository

class PopulateDatabaseUseCase(
    private val installedAppsRepository: InstalledAppsRepository
) {
    
    suspend operator fun invoke(context: Context) {
        try {
            installedAppsRepository.populateDatabase(context)
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error appropriately - could emit to error state, log, etc.
        }
    }
}
