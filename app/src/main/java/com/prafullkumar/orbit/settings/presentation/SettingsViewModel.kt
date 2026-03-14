package com.prafullkumar.orbit.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.orbit.settings.data.SettingsPreferenceStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferenceStore: SettingsPreferenceStore
) : ViewModel() {

    val habitsEnabled: StateFlow<Boolean> = settingsPreferenceStore.habitsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setHabitsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsPreferenceStore.setHabitsEnabled(enabled)
        }
    }
}
