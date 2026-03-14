package com.prafullkumar.orbit

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.prafullkumar.hiddenapps.hiddenAppsModule
import com.prafullkumar.orbit.core.data.local.fav.FavDao
import com.prafullkumar.orbit.core.data.local.fav.FavDatabase
import com.prafullkumar.orbit.core.data.local.installedApps.DatabasePopulator
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledAppsRepository
import com.prafullkumar.orbit.core.data.local.installedApps.InstalledDatabase
import com.prafullkumar.orbit.core.domain.PopulateDatabaseUseCase
import com.prafullkumar.orbit.home.main.homeModule
import com.prafullkumar.orbit.onBoarding.OnBoardingViewModel
import com.prafullkumar.usage.usageModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class OrbitLauncher : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@OrbitLauncher)
            modules(
                    databaseModule,
                    hiddenAppsModule,
                    homeModule,
                    usageModule,
                    com.prafullkumar.orbit.home.habits.habitModule,
                    com.prafullkumar.orbit.appblocker.appBlockerModule,
                    com.prafullkumar.orbit.settings.settingsModule,
                    module { viewModel { OnBoardingViewModel(get()) } },
            )
        }
    }
}

val databaseModule = module {
    single<InstalledDatabase> {
        Room.databaseBuilder(get(), InstalledDatabase::class.java, "installed_database")
                .fallbackToDestructiveMigration()
                .addCallback(
                        object : RoomDatabase.Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                DatabasePopulator.populateDatabase(
                                        populateDatabaseUseCase = get<PopulateDatabaseUseCase>(),
                                        context = get<Application>()
                                )
                            }
                        }
                )
                .build()
    }
    single { get<InstalledDatabase>().installedAppsDao() }
    single<FavDatabase> {
        Room.databaseBuilder(get(), FavDatabase::class.java, "fav_database")
                .fallbackToDestructiveMigration()
                .build()
    }
    single<FavDao> { get<FavDatabase>().favDao() }
    single<InstalledAppsRepository> { InstalledAppsRepository(get()) }
    single<PopulateDatabaseUseCase> { PopulateDatabaseUseCase(get()) }
}
