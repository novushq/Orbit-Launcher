
package com.prafullkumar.orbit.home.habits

import androidx.room.Room
import com.prafullkumar.orbit.home.habits.data.local.HabitDatabase
import com.prafullkumar.orbit.home.habits.data.repository.HabitRepository
import com.prafullkumar.orbit.home.habits.presentation.HabitViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val habitModule = module {
    single<HabitDatabase> {
        Room.databaseBuilder(
            get(),
            HabitDatabase::class.java,
            "habit_database"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<HabitDatabase>().habitDao() }
    single { HabitRepository(get()) }
    viewModel { HabitViewModel(get()) }
}
