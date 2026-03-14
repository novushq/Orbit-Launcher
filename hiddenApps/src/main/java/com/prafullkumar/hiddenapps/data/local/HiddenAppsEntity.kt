package com.prafullkumar.hiddenapps.data.local

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prafullkumar.hiddenapps.model.AppInfo

@Entity
data class HiddenAppsEntity(
    @PrimaryKey
    val packageName: String,
    val label: String,
    val timeStamp: Long
) {
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