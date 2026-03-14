package com.prafullkumar.orbit.settings.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "orbit_settings")

class SettingsPreferenceStore(private val context: Context) {
    private object Keys {
        val ENABLE_HABITS = booleanPreferencesKey("enable_habits")
    }

    val habitsEnabled: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[Keys.ENABLE_HABITS] ?: false // Default to false
    }

    suspend fun setHabitsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.ENABLE_HABITS] = enabled
        }
    }
}
