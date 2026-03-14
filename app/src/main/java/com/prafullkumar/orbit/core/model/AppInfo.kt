package com.prafullkumar.orbit.core.model

import android.content.Intent
import android.graphics.drawable.Drawable
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledAppsEntity

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val launchIntent: Intent?
) {
    fun toInstalledAppsEntity(): InstalledAppsEntity {
        return InstalledAppsEntity(
            packageName = this.packageName,
            label = this.label
        )
    }
}
