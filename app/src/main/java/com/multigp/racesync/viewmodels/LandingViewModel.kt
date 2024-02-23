package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class LandingViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
) : ViewModel() {

    private val _chaptersPagingData = MutableStateFlow<PagingData<Chapter>>(PagingData.empty())
    val chaptersPagingData: StateFlow<PagingData<Chapter>> = _chaptersPagingData

    private val _nearbyRacesPagingData = MutableStateFlow<PagingData<Race>>(PagingData.empty())
    val nearbyRacesPagingData: StateFlow<PagingData<Race>> = _nearbyRacesPagingData

    private val _joinedRacesPagingData = MutableStateFlow<PagingData<Race>>(PagingData.empty())
    val joinedRacesPagingData: StateFlow<PagingData<Race>> = _joinedRacesPagingData

    private val _raceDetailsUiState = MutableStateFlow<UiState<Race>>(UiState.Loading)
    val raceDetailsUiState: StateFlow<UiState<Race>> = _raceDetailsUiState.asStateFlow()

    private val _chapterDetailsUiState = MutableStateFlow<UiState<Chapter>>(UiState.Loading)
    val chapterDetailsUiState: StateFlow<UiState<Chapter>> = _chapterDetailsUiState.asStateFlow()

    fun fetchChapters() {
        viewModelScope.launch {
            val chaptersPagingData = useCases.getChaptersUseCase()
            chaptersPagingData
                .cachedIn(viewModelScope)
                .collect {
                    _chaptersPagingData.value = it
                }
        }
    }

    fun fetchNearbyRaces() {
        viewModelScope.launch {
            val racesPagingData = useCases.getRacesUseCase(10000.0)
            racesPagingData
                .cachedIn(viewModelScope)
                .collect {
                    _nearbyRacesPagingData.value = it
                }
        }
    }

    fun fetchJoinedRaces() {
        viewModelScope.launch {
            val racesPagingData = useCases.getRacesUseCase.fetchJoinedRaces()
            racesPagingData
                .cachedIn(viewModelScope)
                .collect {
                    _joinedRacesPagingData.value = it
                }
        }
    }

    fun fetchRace(raceId: String){
        viewModelScope.launch {
            useCases.getRacesUseCase.fetchRace(raceId).collect{race ->
                _raceDetailsUiState.value = UiState.Success(race)
            }
        }
    }

    fun fetchChapter(chapterId: String){
        viewModelScope.launch {
            useCases.getChaptersUseCase(chapterId).collect{chapter ->
                _chapterDetailsUiState.value = UiState.Success(chapter)
            }
        }
    }
}