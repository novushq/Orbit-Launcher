package com.prafullkumar.usage.domain

import android.app.usage.UsageStats
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

data class AppUsageData(
    val packageName: String,
    val appName: String,
    val totalTimeInForeground: Long,
    val lastTimeUsed: Long = 0L,
    val firstTimeStamp: Long = 0L,
    val lastTimeStamp: Long = 0L,
    val icon: Drawable? = null
)

fun UsageStats.toAppUsageData(context: Context) = AppUsageData(
    packageName = this.packageName,
    appName = try {
        context.packageManager.getApplicationLabel(
            context.packageManager.getApplicationInfo(
                this.packageName,
                0
            )
        ).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        this.packageName // Fallback to package name
    },
    totalTimeInForeground = this.totalTimeInForeground,
    lastTimeUsed = this.lastTimeUsed,
    firstTimeStamp = this.firstTimeStamp,
    lastTimeStamp = this.lastTimeStamp,
    icon = try {
        context.packageManager.getApplicationIcon(this.packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        null // Fallback to null if icon not found
    }
)