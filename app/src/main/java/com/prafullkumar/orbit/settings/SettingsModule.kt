package com.prafullkumar.orbit.settings

import com.prafullkumar.orbit.settings.data.SettingsPreferenceStore
import com.prafullkumar.orbit.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single { SettingsPreferenceStore(get()) }
    viewModel { SettingsViewModel(get()) }
}

