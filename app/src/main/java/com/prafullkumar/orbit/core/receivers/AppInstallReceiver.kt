package com.prafullkumar.orbit.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledAppsDao
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledAppsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppInstallReceiver : BroadcastReceiver(), KoinComponent {
    private val installedAppsDao by inject<InstalledAppsDao>()

    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult = goAsync()
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED -> {
                val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)

                if (!isReplacing) {
                    val packageName = intent.data?.encodedSchemeSpecificPart
                    if (packageName != null) {
                        Log.d("AppUninstallReceiver", "App uninstalled: $packageName")
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                installedAppsDao.insertInstalledApps(
                                    InstalledAppsEntity(
                                        packageName = packageName,
                                        label = context?.packageManager?.getApplicationLabel(
                                            context.packageManager.getApplicationInfo(
                                                packageName,
                                                PackageManager.GET_META_DATA
                                            )
                                        )?.toString() ?: packageName
                                    )
                                )
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
        fun register(context: Context): AppInstallReceiver {
            val receiver = AppInstallReceiver()
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addDataScheme("package")
            }
            context.registerReceiver(receiver, filter)
            return receiver
        }

        fun unregister(context: Context, receiver: AppInstallReceiver) {
            context.unregisterReceiver(receiver)
        }
    }
}