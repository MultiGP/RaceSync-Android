package com.multigp.racesync.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class LandingViewModel @Inject constructor(
    val useCases: RaceSyncUseCases,
    private val locationClient: FusedLocationProviderClient,
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

    private val _joineChapterRacesUiState = MutableStateFlow<UiState<List<Race>>>(UiState.Loading)
    val joineChapterRacesUiState: StateFlow<UiState<List<Race>>> =
        _joineChapterRacesUiState.asStateFlow()

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

    @SuppressLint("MissingPermission")
    fun fetchNearbyRaces(radius: Double = 3000.0) {
        viewModelScope.launch {
            locationClient.lastLocation.await()?.let { curLocation ->
                val racesPagingData = useCases.getRacesUseCase(radius)
                racesPagingData
                    .cachedIn(viewModelScope)
                    .collect {
                        _nearbyRacesPagingData.value = it.filter { race ->
                            calculateDistance(
                                race.latitude!!,
                                race.latitude!!,
                                curLocation.latitude,
                                curLocation.longitude
                            ) > radius
                        }
                    }
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

    fun fetchJoinedChapterRaces() {
        viewModelScope.launch {
            try {
                _joineChapterRacesUiState.value = UiState.Loading
                useCases.getRacesUseCase.fetchJoinedChapterRaces().collect {
                    _joineChapterRacesUiState.value = UiState.Success(it)
                }
            } catch (exception: Exception) {
                _joineChapterRacesUiState.value = UiState.Error(exception.localizedMessage ?: "")
            }
        }
    }

    fun fetchRace(raceId: String) {
        viewModelScope.launch {
            useCases.getRacesUseCase.fetchRace(raceId).collect { race ->
                _raceDetailsUiState.value = UiState.Success(race)
            }
        }
    }

    fun fetchChapter(chapterId: String) {
        viewModelScope.launch {
            useCases.getChaptersUseCase(chapterId).collect { chapter ->
                _chapterDetailsUiState.value = UiState.Success(chapter)
            }
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
}