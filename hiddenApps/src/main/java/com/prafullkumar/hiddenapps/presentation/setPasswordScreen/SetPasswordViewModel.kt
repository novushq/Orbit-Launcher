package com.prafullkumar.hiddenapps.presentation.setPasswordScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.hiddenapps.data.HiddenAppsRepository
import com.prafullkumar.hiddenapps.data.local.HiddenAppsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SetPasswordViewModel(
    val fromDrawerFirstTime: Boolean,
    val packageName: String? = null,
    val label: String? = null
) : ViewModel(), KoinComponent {
    private val repository: HiddenAppsRepository by inject()

    /**
     * Sets new password
     */
    fun setPassword(password: String, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setPassword(password)
            hideApp { coroutineScope ->
                coroutineScope.launch {
                    withContext(Dispatchers.Main) {
                        onComplete()
                    }
                }
            }
        }
    }

    fun hideApp(onComplete: (CoroutineScope) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (fromDrawerFirstTime && !packageName.isNullOrEmpty() && !label.isNullOrEmpty()) {
                repository.insertApp(
                    HiddenAppsEntity(
                        packageName, label, System.currentTimeMillis()
                    )
                )
            }
            onComplete(this)
        }
    }
}