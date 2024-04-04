package com.multigp.racesync.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.google.android.gms.location.FusedLocationProviderClient
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class UiState<out T> {
    object None : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@SuppressLint("MissingPermission")
@HiltViewModel
class LandingViewModel @Inject constructor(
    val useCases: RaceSyncUseCases,
    private val locationClient: FusedLocationProviderClient,
) : ViewModel() {

    private val _nearbyRacesPagingData = MutableStateFlow<PagingData<Race>>(PagingData.empty())
    val nearbyRacesPagingData: StateFlow<PagingData<Race>> = _nearbyRacesPagingData

    private val _joinedRacesPagingData = MutableStateFlow<PagingData<Race>>(PagingData.empty())
    val joinedRacesPagingData: StateFlow<PagingData<Race>> = _joinedRacesPagingData

    private val _raceDetailsUiState =
        MutableStateFlow<UiState<Triple<Profile, Race, RaceView>>>(UiState.Loading)
    val raceDetailsUiState: StateFlow<UiState<Triple<Profile, Race, RaceView>>> =
        _raceDetailsUiState.asStateFlow()

    private val _chapterDetailsUiState = MutableStateFlow<UiState<Chapter>>(UiState.Loading)
    val chapterDetailsUiState: StateFlow<UiState<Chapter>> = _chapterDetailsUiState.asStateFlow()

    private val _joineChapterRacesUiState = MutableStateFlow<UiState<List<Race>>>(UiState.Loading)
    val joineChapterRacesUiState: StateFlow<UiState<List<Race>>> =
        _joineChapterRacesUiState.asStateFlow()

    private val _raceFeedOoption = MutableStateFlow(Pair(100.0, "mi"))
    val raceFeedOption: StateFlow<Pair<Double, String>> = _raceFeedOoption.asStateFlow()

    private val _aircraftsUiState = MutableStateFlow<UiState<List<Aircraft>>>(UiState.Loading)
    val aircraftsUiState: StateFlow<UiState<List<Aircraft>>> = _aircraftsUiState.asStateFlow()

    private val _joinRaceUiState = MutableStateFlow<UiState<Boolean>>(UiState.None)
    val joinRaceUiState: StateFlow<UiState<Boolean>> = _joinRaceUiState.asStateFlow()

    private val _resignRaceUiState = MutableStateFlow<UiState<Boolean>>(UiState.None)
    val resignRaceUiState: StateFlow<UiState<Boolean>> = _resignRaceUiState.asStateFlow()

    @SuppressLint("MissingPermission")
    fun fetchNearbyRaces() {
        viewModelScope.launch {
            locationClient.lastLocation.await()?.let { curLocation ->
                val searchRadius = useCases.getRacesUseCase.fetchSearchRadius()
                val racesPagingData = useCases.getRacesUseCase.fetchNearbyRaces(searchRadius)
                val (radius, unit) = useCases.getRacesUseCase.fetchRaceFeedOptions().first()
                racesPagingData
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _nearbyRacesPagingData.value = pagingData
                            .filter { it.isUpcoming }
                            .filter { race ->
                                race.isWithInSearchRadius(curLocation, searchRadius)
                            }
                            .map {race ->
                                useCases.getRacesUseCase.calculateRaceDistace(race, curLocation)
                                race
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

    fun fetchRaceView(raceId: String) {
        viewModelScope.launch {
            try {
                _raceDetailsUiState.value = UiState.Loading
                useCases.getRacesUseCase.fetchRaceView(raceId).collect { data ->
                    _raceDetailsUiState.value = UiState.Success(data)
                }
            } catch (exception: Exception) {
                _raceDetailsUiState.value = UiState.Error(exception.localizedMessage ?: "")
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
            _aircraftsUiState.value = UiState.Loading
            try {
                useCases.getAllAircraftUseCase()
                    .collect { aircrafts ->
                        _aircraftsUiState.value = UiState.Success(aircrafts)
                    }
            } catch (exception: Exception) {
                _aircraftsUiState.value =
                    UiState.Error(exception.localizedMessage ?: "Failed to load aircrafts")
            }
        }
    }

    fun joinRace(raceId: String, aircraftId: String) {
        viewModelScope.launch {
            _joinRaceUiState.value = UiState.Loading
            try {
                useCases.getRacesUseCase.joinRace(raceId, aircraftId).collect {
                    _joinRaceUiState.value = UiState.Success(it)
                }
            } catch (exception: Exception) {
                _joinRaceUiState.value =
                    UiState.Error(exception.localizedMessage ?: "Failed to join the race")
            }
        }
    }

    fun resignFromRace(raceId: String) {
        viewModelScope.launch {
            _resignRaceUiState.value = UiState.Loading
            try {
                useCases.getRacesUseCase.resignFromRace(raceId).collect {
                    _resignRaceUiState.value = UiState.Success(it)
                }
            } catch (exception: Exception) {
                _resignRaceUiState.value =
                    UiState.Error(exception.localizedMessage ?: "Failed to resign from the race")
            }
        }
    }

    fun updateJoinRaceUiState(isClosed: Boolean = true) {
        if (isClosed) {
            _joinRaceUiState.value = UiState.None
        }
    }

    fun updateResignRaceUiState(isClosed: Boolean = true) {
        if (isClosed) {
            _resignRaceUiState.value = UiState.None
        }
    }
}