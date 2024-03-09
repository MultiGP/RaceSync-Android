package com.multigp.racesync.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.domain.model.Aircraft
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

    private val _raceFeedOoption = MutableStateFlow(Pair(100.0, "mi"))
    val raceFeedOption: StateFlow<Pair<Double, String>> = _raceFeedOoption.asStateFlow()

    private val _aircraftsUiState = MutableStateFlow<UiState<List<Aircraft>>>(UiState.Loading)
    val aircraftsUiState: StateFlow<UiState<List<Aircraft>>> = _aircraftsUiState.asStateFlow()

    @SuppressLint("MissingPermission")
    fun fetchNearbyRaces() {
        viewModelScope.launch {
            locationClient.lastLocation.await()?.let { curLocation ->
                val searchRadius = useCases.getRacesUseCase.fetchSearchRadius()
                val racesPagingData = useCases.getRacesUseCase.fetchNearbyRaces(searchRadius)
                racesPagingData
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _nearbyRacesPagingData.value = pagingData
                            .filter { it.isUpcoming }
                            .filter { race ->
                                race.isWithInSearchRadius(curLocation, searchRadius)
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
                .collect { pagingData ->
                    _joinedRacesPagingData.value = pagingData.filter { it.isUpcoming }
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

    fun saveSearchRadius(radius: Double, unit: String) {
        viewModelScope.launch {
            useCases.getRacesUseCase.saveSearchRadius(radius, unit)
            fetchNearbyRaces()
        }
    }

    fun fetchRaceFeedOptions() {
        viewModelScope.launch {
            useCases.getRacesUseCase.fetchRaceFeedOptions().collect {
                _raceFeedOoption.value = it
            }
        }
    }

    fun fetchAircrafts() {
        viewModelScope.launch {
            useCases.getAllAircraftUseCase()
                .collect { aircrafts ->
                    _aircraftsUiState.value = UiState.Success(aircrafts)
                }
        }
    }
}