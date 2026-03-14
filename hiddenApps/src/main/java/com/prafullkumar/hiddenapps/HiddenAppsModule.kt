package com.prafullkumar.hiddenapps

import androidx.room.Room
import com.prafullkumar.hiddenapps.data.HiddenAppsRepository
import com.prafullkumar.hiddenapps.data.local.HiddenAppsDatabase
import com.prafullkumar.hiddenapps.presentation.hiddenAppsScreen.HiddenAppsViewModel
import com.prafullkumar.hiddenapps.presentation.setPasswordScreen.SetPasswordViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for hidden apps feature dependencies
 */
val hiddenAppsModule = module {
    single<HiddenAppsDatabase> {
        Room.databaseBuilder(
            get(),
            HiddenAppsDatabase::class.java,
            "hidden_apps_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<HiddenAppsDatabase>().hiddenAppsDao() }
    single { HiddenAppsRepository() }
    viewModel<HiddenAppsViewModel> { HiddenAppsViewModel(get()) }
    viewModel { SetPasswordViewModel(get(), get(), get()) }
//    viewModel { ChangePasswordViewModel() }
}