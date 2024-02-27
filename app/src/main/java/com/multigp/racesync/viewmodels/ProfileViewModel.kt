package com.multigp.racesync.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.BuildConfig
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        val apikey = BuildConfig.API_KEY

        viewModelScope.launch {
            useCases.getProfileUseCase(apikey)
                .collect{
                    val data = it.data!!
                    val name = data.firstName
                    _uiState.update { curr ->
                        curr.copy(
                            isLoading = false,
                            simpleText = name
                        )
                    }

                }
        }
    }
}