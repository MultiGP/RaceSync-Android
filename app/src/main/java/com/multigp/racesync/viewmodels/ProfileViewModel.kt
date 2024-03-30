package com.multigp.racesync.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Profile>>(UiState.None)
    val uiState: StateFlow<UiState<Profile>> = _uiState.asStateFlow()

    var isDialogShown by mutableStateOf(false)
        private set

    fun onShowQrCode() {
        isDialogShown = true
    }

    fun onDismissDialog() {
        isDialogShown = false
    }


    init {
        Log.d("TAG", "Hello World")
        viewModelScope.launch {
            try {
                useCases.getProfileUseCase()
                    .collect { profile ->
                        _uiState.value = UiState.Success(profile)
                    }
            }catch (exception: Exception){
                _uiState.value = UiState.Error(exception.localizedMessage ?: "Failed to fetch profile")
            }
        }
    }
}