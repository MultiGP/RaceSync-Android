package com.multigp.racesync.viewmodels

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.BuildConfig
import com.multigp.racesync.domain.model.LoginRequest
import com.multigp.racesync.domain.model.UserInfo
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isValidForm: Boolean = false,
    val isLoginInProgress: Boolean = false,
    val didLoginSucceed: Boolean = false,
    val didLoginFailed: Boolean = false,
    val loginError: String? = null,
    val sessionId: String? = null,
    val userInfo: UserInfo? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    val useCases: RaceSyncUseCases,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(newValue: String): Unit {
        _uiState.update { currentState ->
            currentState.copy(
                email = newValue.trim(),
                isValidForm = validateForm(newValue, currentState.password)
            )
        }
    }

    fun onPasswordChanged(newValue: String): Unit {
        _uiState.update { currentState ->
            currentState.copy(
                password = newValue.trim(),
                isValidForm = validateForm(currentState.email, newValue)
            )
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun onLogin() {
        val request =
            LoginRequest(BuildConfig.API_KEY, _uiState.value.email, _uiState.value.password)

        viewModelScope.launch {
            viewModelScope.launch {
                useCases.performLoginUseCase(request)
                    .onStart {
                        _uiState.update { it.copy(isLoginInProgress = true) }
                    }
                    .collect { result ->
                        result.fold(
                            onSuccess = { response ->
                                if (response.status == 1) {
                                    _uiState.update { curr ->
                                        curr.copy(
                                            isLoginInProgress = false,
                                            didLoginSucceed = true,
                                            sessionId = response.sessionId,
                                            userInfo = response.userInfo
                                        )
                                    }
                                } else {
                                    _uiState.update { curr ->
                                        curr.copy(
                                            isLoginInProgress = false,
                                            didLoginFailed = true,
                                            loginError = response.errorMessage()
                                        )
                                    }
                                }
                            },
                            onFailure = { throwable ->
                                _uiState.update { curr ->
                                    curr.copy(
                                        isLoginInProgress = false,
                                        didLoginFailed = true,
                                        loginError = throwable.localizedMessage
                                    )
                                }
                            }
                        )
                    }
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