package com.prafullkumar.hiddenapps.presentation.hiddenAppsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.hiddenapps.data.HiddenAppsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

enum class HiddenAppScreen {
    APP_LIST, VERIFY_PASSWORD
}

data class UIState(
    val currentScreen: HiddenAppScreen,
    val wrongPassword: Boolean = false,
    val isPasswordSet: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * ViewModel for managing hidden apps and password verification
 */
class HiddenAppsViewModel(
    private val repository: HiddenAppsRepository
) : ViewModel(), KoinComponent {

    private val _uiState = MutableStateFlow(
        UIState(
            currentScreen = HiddenAppScreen.VERIFY_PASSWORD
        )
    )
    val uiState = _uiState.asStateFlow()

    val hiddenApps = repository.getAllHiddenApps().stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

//    init {
//        checkPasswordStatus()
//    }
//
//    /**
//     * Checks if password is set and updates UI state accordingly
//     */
//    private fun checkPasswordStatus() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val isPasswordSet = repository.isPasswordSet()
//            _uiState.value = _uiState.value.copy(
//                isPasswordSet = isPasswordSet
//            )
//        }
//    }

    /**
     * Verifies entered password
     */
    fun verifyPassword(password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val isValid = repository.verifyPassword(password)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                wrongPassword = !isValid,
                currentScreen = if (isValid) HiddenAppScreen.APP_LIST else HiddenAppScreen.VERIFY_PASSWORD
            )

            onResult(isValid)
        }
    }

    
    /**
     * Unhides an app with confirmation
     */
    fun unhideApp(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHiddenApp(packageName)
        }
    }

    /**
     * Updates current screen
     */
    fun updateCurrentScreen(currentScreen: HiddenAppScreen) {
        _uiState.value = _uiState.value.copy(
            currentScreen = currentScreen
        )
    }

    /**
     * Clears wrong password state
     */
    fun clearWrongPasswordState() {
        _uiState.value = _uiState.value.copy(wrongPassword = false)
    }
}