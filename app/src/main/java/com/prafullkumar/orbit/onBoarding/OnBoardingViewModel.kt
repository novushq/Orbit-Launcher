package com.prafullkumar.orbit.onBoarding

import android.app.AppOpsManager
import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class representing the current onboarding state
 */
data class OnboardingState(
    val isDefaultLauncher: Boolean = false,
    val hasUsageAccess: Boolean = false,
    val isOnboardingComplete: Boolean = false
) {
    // All permissions must be granted for complete onboarding
    val allPermissionsGranted: Boolean get() = isDefaultLauncher && hasUsageAccess
}

/**
 * ViewModel that manages onboarding state and permission checks
 * Uses SharedPreferences to persist onboarding completion status
 */
class OnBoardingViewModel(private val app: Application) : AndroidViewModel(app) {

    // SharedPreferences for storing onboarding status
    private val prefs: SharedPreferences =
        app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // State flow for UI to observe
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    init {
        // Check current permission status on initialization
        refreshPermissionStatus()
    }

    /**
     * Refresh current permission status and update state
     * This should be called when returning from permission settings
     */
    fun refreshPermissionStatus() {
        val ctx = app.applicationContext
        val isDefaultLauncher = isDefaultLauncher(ctx)
        val hasUsageAccess = hasUsageStatsPermission(ctx)
        val wasOnboardingComplete = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)

        _state.value = OnboardingState(
            isDefaultLauncher = isDefaultLauncher,
            hasUsageAccess = hasUsageAccess,
            isOnboardingComplete = wasOnboardingComplete
        )
    }

    /**
     * Mark onboarding as complete and save to SharedPreferences
     * This should only be called when all permissions are granted
     */
    fun completeOnboarding() {
        if (_state.value.allPermissionsGranted) {
            prefs.edit {
                putBoolean(KEY_ONBOARDING_COMPLETE, true)
            }

            _state.value = _state.value.copy(isOnboardingComplete = true)
        }
    }

    /**
     * Check if user should see onboarding
     * Returns true if either onboarding was never completed OR permissions were revoked
     */
    fun shouldShowOnboarding(): Boolean {
        val wasOnboardingComplete = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
        val currentState = _state.value

        // Show onboarding if never completed OR if permissions were revoked after completion
        return !wasOnboardingComplete || !currentState.allPermissionsGranted
    }

    /**
     * Create intent to request default launcher role (Android Q+)
     * Returns null for older Android versions
     */
    fun createRoleRequestIntentOrNull(): Intent? {
        val roleManager = app.getSystemService(RoleManager::class.java)
        return roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
    }

    /**
     * Create intent to open usage access settings
     */
    fun createUsageAccessIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }

    /**
     * Create intent for setting default launcher on older Android versions
     */
    fun createLegacyLauncherIntent(): Intent {
        return Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
    }

    /**
     * Check if this app is set as the default launcher
     */
    private fun isDefaultLauncher(context: Context): Boolean {
        // Use RoleManager for Android Q+
        val roleManager = context.getSystemService(RoleManager::class.java)
        return roleManager.isRoleHeld(RoleManager.ROLE_HOME)
    }

    /**
     * Check if app has usage stats permission
     */
    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode =
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    companion object {
        private const val PREFS_NAME = "onboarding_prefs"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}