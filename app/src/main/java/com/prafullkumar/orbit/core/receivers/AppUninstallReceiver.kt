package com.prafullkumar.orbit.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledAppsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppUninstallReceiver : BroadcastReceiver(), KoinComponent {
    private val installedAppsDao by inject<InstalledAppsDao>()

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        when (intent?.action) {
            Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_FULLY_REMOVED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                if (!isReplacing) {
                    val packageName = intent.data?.encodedSchemeSpecificPart
                    if (packageName != null) {
                        Log.d("AppUninstallReceiver", "App uninstalled: $packageName")
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                installedAppsDao.deleteInstalledApp(packageName)
                            } finally {
                                pendingResult.finish()
                            }
                        }
                    } else {
                        pendingResult.finish()
                    }
                } else {
                    pendingResult.finish()
                }
            }

            else -> pendingResult.finish()
        }
    }

    companion object {
        fun register(context: Context): AppUninstallReceiver {
            val receiver = AppUninstallReceiver()
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
                addDataScheme("package")
            }
            context.registerReceiver(receiver, filter)
            return receiver
        }

        fun unregister(context: Context, receiver: AppUninstallReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
}