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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChaptersUiState {
    object Loading : ChaptersUiState()
    data class Success(val chapters: List<Chapter>) : ChaptersUiState()
    data class Error(val message: String) : ChaptersUiState()
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

    fun fetchRace(raceId: String) = useCases.getRacesUseCase.fetchRace(raceId)

    fun fetchChapter(chapterId: String) = useCases.getChaptersUseCase(chapterId)
}