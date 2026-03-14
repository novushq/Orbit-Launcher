package com.prafullkumar.orbit.core.data.local.installedApps

import android.content.Context
import com.prafullkumar.orbit.core.data.usageData.getInstalledAppsMain
import com.prafullkumar.orbit.core.model.AppInfo
import kotlinx.coroutines.flow.Flow

class InstalledAppsRepository(
    private val installedAppsDao: InstalledAppsDao
) {
    
    suspend fun populateDatabase(context: Context) {
        val installedApps = getInstalledAppsMain(context)
        val entities = installedApps.map { appInfo ->
            InstalledAppsEntity(
                packageName = appInfo.packageName,
                label = appInfo.label
            )
        }
        
        // Clear existing data and insert new data
        installedAppsDao.deleteAllInstalledApps()
        installedAppsDao.insertInstalledAppsList(entities)
    }
    
    fun getAllInstalledApps(): Flow<List<InstalledAppsEntity>> {
        return installedAppsDao.getAllInstalledApps()
    }
    
    suspend fun insertApp(app: InstalledAppsEntity) {
        installedAppsDao.insertInstalledApps(app)
    }
    
    suspend fun deleteApp(packageName: String) {
        installedAppsDao.deleteInstalledApp(packageName)
    }
}
