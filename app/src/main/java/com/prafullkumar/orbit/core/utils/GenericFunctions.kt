package com.prafullkumar.orbit.core.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import com.prafullkumar.orbit.core.model.AppInfo

fun launchApp(context: Context, appInfo: AppInfo) {
    appInfo.launchIntent?.let {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Important for launching from non-Activity context
        try {
            context.startActivity(it)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun openAppInfo(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = "package:$packageName".toUri()
    context.startActivity(intent)
}

fun uninstallAppMain(context: Context, packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = "package:$packageName".toUri()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Important for launching from non-Activity context
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}