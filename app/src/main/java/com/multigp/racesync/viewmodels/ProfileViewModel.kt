package com.multigp.racesync.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val simpleText: String = "Hello World"
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
): ViewModel(){
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init{
        Log.d("TAG", "Hello World")
    }
}