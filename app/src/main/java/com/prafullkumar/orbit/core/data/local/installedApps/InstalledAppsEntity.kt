package com.prafullkumar.orbit.core.data.local.installedApps

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prafullkumar.orbit.core.model.AppInfo

@Entity
data class InstalledAppsEntity(@PrimaryKey val packageName: String, val label: String) {
    fun toAppInfo(context: Context): AppInfo {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(this.packageName)
        val icon = context.packageManager.getApplicationIcon(this.packageName)
        return AppInfo(
            label = this.label,
            packageName = this.packageName,
            icon = icon,
            launchIntent = launchIntent
        )
    }
}
