package com.prafullkumar.usage.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getInstalledAppsMain(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
    try {
        val pm: PackageManager = context.packageManager
        val applications = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val appList = mutableListOf<AppInfo>()
        for (appInfo in applications) {
            if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                val label = pm.getApplicationLabel(appInfo).toString()
                val packageName = appInfo.packageName
                val icon = pm.getApplicationIcon(appInfo)
                val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName)

                appList.add(AppInfo(label, packageName, icon, launchIntent))
            }
        }
        appList.sortedBy { it.label.lowercase() }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

data class AppInfo(
    val label: String, val packageName: String, val icon: Drawable, val launchIntent: Intent?
)
