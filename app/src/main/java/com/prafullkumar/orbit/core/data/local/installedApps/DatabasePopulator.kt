package com.prafullkumar.orbit.core.data.local.installedApps

import android.content.Context
import com.prafullkumar.orbit.core.domain.PopulateDatabaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object DatabasePopulator {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun populateDatabase(
        populateDatabaseUseCase: PopulateDatabaseUseCase,
        context: Context
    ) {
        scope.launch {
            populateDatabaseUseCase(context)
        }
    }
}