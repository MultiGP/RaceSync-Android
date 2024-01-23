package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class LoginUiState(
    val email: String = "",
    val password: String = ""
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newValue: String): Unit {
        _uiState.update { currentState ->
            currentState.copy(email = newValue)
        }
    }

    fun onPasswordChanged(newValue: String): Unit {
        _uiState.update { currentState ->
            currentState.copy(password = newValue)
        }
    }
}