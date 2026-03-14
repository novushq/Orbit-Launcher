package com.prafullkumar.orbit

import android.app.AppOpsManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.prafullkumar.orbit.core.receivers.AppInstallReceiver
import com.prafullkumar.orbit.core.receivers.AppUninstallReceiver
import com.prafullkumar.orbit.core.navigation.AppNavigation
import com.prafullkumar.orbit.core.navigation.Routes
import com.prafullkumar.orbit.ui.theme.CrazyLauncherTheme

class MainActivity : ComponentActivity() {
    lateinit var appInstallReceiver: AppInstallReceiver
    lateinit var appUninstallReceiver: AppUninstallReceiver

    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appInstallReceiver = AppInstallReceiver.register(this)
        appUninstallReceiver = AppUninstallReceiver.register(this)
        setupBackButtonBehavior()


        setContent {
            CrazyLauncherTheme {
                setupSystemUI()
                navController = rememberNavController()
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val navigationDestination = getNavigationDestination()
                    Log.d("MainActivity", "onCreate: $navigationDestination")
                    AppNavigation(navController, navigationDestination)
                }
            }
        }
    }

    fun getNavigationDestination(): Routes {
        Log.d("MainActivity", "getNavigationDestination: called")
        val pref = getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        return if (hasUsageStatsPermission(this) && getSystemService(RoleManager::class.java).isRoleHeld(
                RoleManager.ROLE_HOME
            ) && pref.getBoolean("onboarding_complete", false)
        ) {
            Routes.HomeScreen
        } else {
            Routes.OnboardingScreen
        }
    }

    override fun onDestroy() {
        AppInstallReceiver.unregister(this, appInstallReceiver)
        AppUninstallReceiver.unregister(this, appUninstallReceiver)
        super.onDestroy()
    }

    // In MainActivity.onNewIntent (if launchMode singleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
            navController.popBackStack(navController.graph.startDestinationId, false)
        }
    }


    private fun setupBackButtonBehavior() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing - stay in launcher
            }
        })
    }

    @Composable
    private fun setupSystemUI() {
        val systemUiController = rememberSystemUiController()
        val backgroundColor = MaterialTheme.colorScheme.surface
        val isLightBackground = backgroundColor.luminance() > 0.5f

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = backgroundColor, darkIcons = isLightBackground
            )
            systemUiController.setNavigationBarColor(
                color = backgroundColor,
                navigationBarContrastEnforced = false,
                darkIcons = isLightBackground
            )
        }
    }

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        context.startActivity(intent)
    }

    companion object {
        private const val REQUEST_CODE_DEFAULT_LAUNCHER = 100
    }
}