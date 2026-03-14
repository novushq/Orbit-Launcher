package com.prafullkumar.orbit.home.main

import com.prafullkumar.orbit.home.main.data.DrawerSettingsPreferenceStore
import com.prafullkumar.orbit.home.main.data.repository.AppDrawerRepository
import com.prafullkumar.orbit.home.main.data.repository.HomeRepository
import com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.AppDrawerViewModel
import com.prafullkumar.orbit.home.main.presentation.screens.drawerSettings.DrawerSettingsViewModel
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel
import com.prafullkumar.orbit.home.utility.UtilityViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val homeModule = module {
    single {
        HomeRepository(get(), get(), get())
    }
    viewModel {
        HomeViewModel(get())
    }
    single<AppDrawerRepository> {
        AppDrawerRepository(context = get())
    }
    single {
        DrawerSettingsPreferenceStore(get())
    }
    viewModel {
        AppDrawerViewModel()
    }
    viewModel { DrawerSettingsViewModel() }
    viewModel { UtilityViewModel() }
}