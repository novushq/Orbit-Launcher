package com.prafullkumar.orbit.appblocker

import androidx.room.Room
import com.prafullkumar.orbit.appblocker.data.AppBlockerRepository
import com.prafullkumar.orbit.appblocker.data.local.AppBlockerDatabase
import com.prafullkumar.orbit.appblocker.presentation.AppBlockerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appBlockerModule = module {
    single<AppBlockerDatabase> {
        Room.databaseBuilder(get(), AppBlockerDatabase::class.java, "appblocker_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<AppBlockerDatabase>().appBlockerDao() }
    single { AppBlockerRepository(get()) }
    viewModel { AppBlockerViewModel(get(), get()) }
}
