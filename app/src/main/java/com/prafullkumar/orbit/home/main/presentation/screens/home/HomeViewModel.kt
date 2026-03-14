package com.prafullkumar.orbit.home.main.presentation.screens.home

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.uninstallAppMain
import com.prafullkumar.orbit.home.main.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import java.util.Calendar

class HomeViewModel(
    private val repository: HomeRepository,
) : ViewModel(), KoinComponent {


    val installedApps = repository.getInstalledApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUsage = repository.getTotalTimeSpent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val favApps = repository.getAllFavourites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupedApps: StateFlow<Map<Char, List<AppInfo>>> =
        installedApps.map { apps ->
            apps.groupBy { it.label.first().uppercaseChar() }.toSortedMap()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun launchClockApp(context: Context) {
        try {
            val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required when starting activity from non-activity context
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No clock app found", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCalendarApp(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_CALENDAR)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Required when starting activity from non-activity context
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No calendar app found", Toast.LENGTH_SHORT).show()
        }
    }

    fun uninstallApp(context: Context, currentApp: String) {
        viewModelScope.launch {
            try {
                launch { repository.removeFromFavourites(currentApp) }
                // Immediately remove from cache to update UI
//                launch { installedAppsCaches.removeFromPackageName(currentApp) }
                launch { uninstallAppMain(context, currentApp) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeFromFavorites(packageName: String) {
        viewModelScope.launch {
            repository.removeFromFavourites(packageName)
        }
    }

    fun launchCamera(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage("com.android.camera")
        intent?.let {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } ?: run {
            Toast.makeText(context, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }
}

fun getCalendar(): Calendar {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
