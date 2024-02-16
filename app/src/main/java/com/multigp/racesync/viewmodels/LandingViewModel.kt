package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChaptersUiState {
    object Loading : ChaptersUiState()
    data class Success(val chapters: List<Chapter>) : ChaptersUiState()
    data class Error(val message: String) : ChaptersUiState()
}

sealed class RaceUiState {
    object Loading : RaceUiState()
    data class Success(val races: List<Race>) : RaceUiState()
    data class Error(val message: String) : RaceUiState()
}

@HiltViewModel
class LandingViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
) : ViewModel() {

    private val _chaptersUiState = MutableStateFlow<ChaptersUiState>(ChaptersUiState.Loading)
    val chaptersUiState: StateFlow<ChaptersUiState> = _chaptersUiState.asStateFlow()

    private val _nearbyUiState = MutableStateFlow<RaceUiState>(RaceUiState.Loading)
    val nearbyUiState: StateFlow<RaceUiState> = _nearbyUiState.asStateFlow()

    private val _joinedUiState = MutableStateFlow<RaceUiState>(RaceUiState.Loading)
    val JoinedUiState: StateFlow<RaceUiState> = _joinedUiState.asStateFlow()

    fun fetchChapters() {
        viewModelScope.launch {
            useCases.getChaptersUseCase()
                .collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            if (response.status) {
                                _chaptersUiState.value =
                                    ChaptersUiState.Success(response.data ?: emptyList())
                            } else {
                                _chaptersUiState.value =
                                    ChaptersUiState.Error(response.errorMessage())
                            }
                        },
                        onFailure = { throwable ->
                            _chaptersUiState.value = ChaptersUiState.Error(
                                throwable.localizedMessage ?: "Error loading chapters"
                            )
                        }
                    )
                }
        }
    }

    fun fetchNearbyChapters() {
        viewModelScope.launch {
            useCases.getRacesUseCase(500.0)
                .collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            if (response.status) {
                                _nearbyUiState.value =
                                    RaceUiState.Success(response.data ?: emptyList())
                            } else {
                                _nearbyUiState.value =
                                    RaceUiState.Error(response.errorMessage())
                            }
                        },
                        onFailure = { throwable ->
                            _nearbyUiState.value = RaceUiState.Error(
                                throwable.localizedMessage ?: "Error loading chapters"
                            )
                        }
                    )
                }
        }
    }

    fun fetchJoinedChapters() {
        viewModelScope.launch {
            _nearbyUiState.value = RaceUiState.Loading
            useCases.getRacesUseCase.fetchJoinedRaces()
                .collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            if (response.status) {
                                _joinedUiState.value =
                                    RaceUiState.Success(response.data ?: emptyList())
                            } else {
                                _joinedUiState.value =
                                    RaceUiState.Error(response.errorMessage())
                            }
                        },
                        onFailure = { throwable ->
                            _joinedUiState.value = RaceUiState.Error(
                                throwable.localizedMessage ?: "Error loading chapters"
                            )
                        }
                    )
                }
        }
    }
}