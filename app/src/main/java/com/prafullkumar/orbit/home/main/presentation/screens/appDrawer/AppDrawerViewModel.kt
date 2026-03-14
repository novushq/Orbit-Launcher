package com.prafullkumar.orbit.home.main.presentation.screens.appDrawer


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.hiddenapps.utils.PasswordManager
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.uninstallAppMain
import com.prafullkumar.orbit.home.main.data.DrawerSettingsPreferenceStore
import com.prafullkumar.orbit.home.main.data.repository.AppDrawerRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// Data class to hold app information

class AppDrawerViewModel() : ViewModel(), KoinComponent {

    private val appListRepository: AppDrawerRepository by inject()
    private val preferences: DrawerSettingsPreferenceStore by inject()

    val layoutType = preferences.layoutType


    fun uninstallApp(context: Context, app: AppInfo) {
        viewModelScope.launch {
            uninstallAppMain(context, app.packageName)
        }
    }

    fun addToFavorites(app: AppInfo) {
        viewModelScope.launch {
            try {
                appListRepository.addToFavourites(app)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hideApp(context: Context, app: AppInfo, navigateToSetPasswordScreen: () -> Unit = {}) {
        viewModelScope.launch {
            if (isPasswordSaved(context)) {
                Log.d("AppDrawerViewModel", "Hiding app: ${app.packageName}")
                appListRepository.addToHiddenApps(app.packageName, app.label)
            } else {
                navigateToSetPasswordScreen()
            }
        }
    }

    private fun isPasswordSaved(context: Context): Boolean {
        return PasswordManager(context).isPasswordSet()
    }

}
