package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.UserInfo
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

    private val _uiState = MutableStateFlow<UiState<Pair<Profile, UserInfo>>>(UiState.None)
    val uiState: StateFlow<UiState<Pair<Profile, UserInfo>>> = _uiState.asStateFlow()

    private val _racesUiState = MutableStateFlow<UiState<List<Race>>>(UiState.Loading)
    val racesUiState: StateFlow<UiState<List<Race>>> = _racesUiState.asStateFlow()

    private val _chaptersUiState = MutableStateFlow<UiState<List<Chapter>>>(UiState.Loading)
    val chaptersUiState: StateFlow<UiState<List<Chapter>>> = _chaptersUiState.asStateFlow()


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
                        val sortedRaces = races.sortedByDescending { it.formattedStartDate }
                        _racesUiState.value = UiState.Success(sortedRaces)
                    }
            } catch (exception: Exception) {
                _racesUiState.value =
                    UiState.Error(exception.localizedMessage ?: "Failed to load races")
            }
        }
    }

    fun fetchChapters(pilotUserName: String) {
        viewModelScope.launch {
            _chaptersUiState.value = UiState.Loading
            try {
                useCases.getChaptersUseCase.fetchPilotChapters(pilotUserName)
                    .collect { races ->
                        _chaptersUiState.value = UiState.Success(races)
                    }
            } catch (exception: Exception) {
                _chaptersUiState.value =
                    UiState.Error(exception.localizedMessage ?: "Failed to load chapters")
            }
        }
    }
}