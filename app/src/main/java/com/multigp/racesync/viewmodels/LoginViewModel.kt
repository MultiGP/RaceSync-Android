package com.multigp.racesync.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.multigp.racesync.BuildConfig
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.requests.LoginRequest
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


data class LoginFormUiState(
    val email: String = "",
    val password: String = "",
    val isValidForm: Boolean = false,
)

sealed class LoginUiState {
    object Initializing : LoginUiState()
    object None : LoginUiState()
    data class Loading(val messageId: Int) : LoginUiState()
    data class Success(val data: Boolean) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}


@HiltViewModel
class LoginViewModel @Inject constructor(
    val useCases: RaceSyncUseCases,
) : ViewModel() {

    private val _formUiState = MutableStateFlow(LoginFormUiState())
    val formUiState: StateFlow<LoginFormUiState> = _formUiState.asStateFlow()

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Initializing)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Initializing
            useCases.getLoginInfoUseCase()
                .collect { (sessionId, userInfo) ->
                    if (sessionId != null && userInfo != null) {
                        _loginUiState.value = LoginUiState.Success(true)
                    } else {
                        _loginUiState.value = LoginUiState.None
                    }
                }
        }
    }

    fun onEmailChanged(newValue: String): Unit {
        _formUiState.update { currentState ->
            currentState.copy(
                email = newValue.trim(),
                isValidForm = validateForm(newValue, currentState.password)
            )
        }
    }

    fun onPasswordChanged(newValue: String): Unit {
        _formUiState.update { currentState ->
            currentState.copy(
                password = newValue.trim(),
                isValidForm = validateForm(currentState.email, newValue)
            )
        }
    }

    fun onLogin() {
        val request =
            LoginRequest(BuildConfig.API_KEY, _formUiState.value.email, _formUiState.value.password)

        viewModelScope.launch {
            useCases.performLoginUseCase(request)
                .onStart {
                    _loginUiState.value = LoginUiState.Loading(R.string.hud_login_progress)
                }
                .collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            if (response.status == 1) {
                                _loginUiState.value = LoginUiState.Success(true)
                            } else {
                                _loginUiState.value = LoginUiState.Error(response.errorMessage())
                            }
                        },
                        onFailure = { throwable ->
                            _loginUiState.value =
                                LoginUiState.Error(throwable.localizedMessage ?: "Failed to login")
                        }
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _loginUiState.value = LoginUiState.Loading(R.string.hud_logout_progress)
                if (useCases.performLoginUseCase.getNotificationPreference().first()){
                    val token = Firebase.messaging.token.await()
                    useCases.performLoginUseCase("delete", token).first()
                }
                useCases.performLoginUseCase.logout().first()
                _loginUiState.value = LoginUiState.None
            }catch (exception: Exception){
                useCases.performLoginUseCase.clearSession()
                _loginUiState.value = LoginUiState.None
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return false
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}