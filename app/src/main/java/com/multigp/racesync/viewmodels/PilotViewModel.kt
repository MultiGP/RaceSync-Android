package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PilotViewModel @Inject constructor(
    val useCases: RaceSyncUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Profile>>(UiState.None)
    val uiState: StateFlow<UiState<Profile>> = _uiState.asStateFlow()

    private val _racesUiState = MutableStateFlow<UiState<List<Race>>>(UiState.Loading)
    val racesUiState: StateFlow<UiState<List<Race>>> = _racesUiState.asStateFlow()

    fun fetchPilotProfile(pilotName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                useCases.getProfileUseCase(pilotName).collect {
                    _uiState.value = UiState.Success(it)
                }
            } catch (exception: Exception) {
                _uiState.value = UiState.Error(exception.localizedMessage ?: "")
            }
        }
    }

    fun fetchRaces(pilotUserName: String) {
        viewModelScope.launch {
            _racesUiState.value = UiState.Loading
            try {
                useCases.getRacesUseCase.fetchPilotRaces(pilotUserName)
                    .collect { races ->
                        _racesUiState.value = UiState.Success(races)
                    }
            } catch (exception: Exception) {
                _racesUiState.value =
                    UiState.Error(exception.localizedMessage ?: "Failed to load aircrafts")
            }
        }
    }
}