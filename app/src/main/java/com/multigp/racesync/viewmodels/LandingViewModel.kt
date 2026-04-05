package com.multigp.racesync.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.Chapter
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
import javax.inject.Inject

sealed class UiState<out T> {
    object None : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class LandingViewModel @Inject constructor(
    val useCases: RaceSyncUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Profile>>(UiState.None)
    val uiState: StateFlow<UiState<Profile>> = _uiState.asStateFlow()

    private val _nearbyRacesUiState = MutableStateFlow<UiState<List<Race>>>(UiState.None)
    val nearbyRacesUiState: StateFlow<UiState<List<Race>>> = _nearbyRacesUiState.asStateFlow()

    private val _joinedRacesUiState = MutableStateFlow<UiState<List<Race>>>(UiState.None)
    val joinedRacesUiState: StateFlow<UiState<List<Race>>> = _joinedRacesUiState.asStateFlow()

    private val _raceDetailsUiState =
        MutableStateFlow<UiState<Triple<Profile, Race, RaceView>>>(UiState.Loading)
    val raceDetailsUiState: StateFlow<UiState<Triple<Profile, Race, RaceView>>> =
        _raceDetailsUiState.asStateFlow()

    private val _chaptersUiState = MutableStateFlow<UiState<List<Chapter>>>(UiState.Loading)
    val chaptersUiState: StateFlow<UiState<List<Chapter>>> = _chaptersUiState.asStateFlow()

    private val _chapterDetailsUiState = MutableStateFlow<UiState<Chapter>>(UiState.Loading)
    val chapterDetailsUiState: StateFlow<UiState<Chapter>> = _chapterDetailsUiState.asStateFlow()

    private val _homeChapterImageUiState = MutableStateFlow<UiState<String?>>(UiState.Loading)
    val homeChapterImageUiState: StateFlow<UiState<String?>> = _homeChapterImageUiState.asStateFlow()

    private val _chapterRacesUiState = MutableStateFlow<UiState<List<Race>>>(UiState.None)
    val chapterRacesUiState: StateFlow<UiState<List<Race>>> =
        _chapterRacesUiState.asStateFlow()

    private val _raceFeedOoption = MutableStateFlow(Pair(100.0, "mi"))
    val raceFeedOption: StateFlow<Pair<Double, String>> = _raceFeedOoption.asStateFlow()

    private val _aircraftsUiState = MutableStateFlow<UiState<List<Aircraft>>>(UiState.Loading)
    val aircraftsUiState: StateFlow<UiState<List<Aircraft>>> = _aircraftsUiState.asStateFlow()

    private val _joinRaceUiState = MutableStateFlow<UiState<Boolean>>(UiState.None)
    val joinRaceUiState: StateFlow<UiState<Boolean>> = _joinRaceUiState.asStateFlow()

    private val _resignRaceUiState = MutableStateFlow<UiState<Boolean>>(UiState.None)
    val resignRaceUiState: StateFlow<UiState<Boolean>> = _resignRaceUiState.asStateFlow()

    init {
        Log.d("viki", "Hello World")
        initializeUserProfile()
    }

    private fun initializeUserProfile(){
        viewModelScope.launch {
            try {
                useCases.getProfileUseCase()
                    .collect { profile ->
                        _uiState.value = UiState.Success(profile)
                        fetchPilotHomeChapter(profile.userName, profile.homeChapterId)
                    }
            }catch (exception: Exception){
                _uiState.value = UiState.Error(exception.localizedMessage ?: "Failed to fetch profile")
            }
        }
    }

    fun updateFCMToken(fcmToken: String){
        viewModelScope.launch {
            try{
                useCases.performLoginUseCase("create", fcmToken)
                    .collect{}
            }catch (_: Exception){
            }
        }
    }

    // In-memory caches — matches iOS's raceCollection dictionary.
    // Shown instantly on tab switch while the API refreshes in the background.
    private var joinedRacesCache: List<Race>? = null
    private var nearbyRacesCache: List<Race>? = null
    private var chapterRacesCache: List<Race>? = null

    // Tracks whether a pull-to-refresh is in flight so the UI can dismiss the spinner.
    // Using a counter avoids StateFlow deduplication issues with identical UiState values.
    private val _refreshComplete = MutableStateFlow(0)
    val refreshComplete: StateFlow<Int> = _refreshComplete.asStateFlow()

    /**
     * Fetches nearby races using the iOS "cache-first + background refresh" pattern:
     *
     * 1. If cached data exists, emit it immediately (instant tab switch)
     * 2. Fire the API call in the background
     * 3. When fresh data arrives, update the UI silently
     *
     * Only shows a loading spinner if there's no cache (first load).
     */
    fun fetchNearbyRaces() {
        viewModelScope.launch {
            // Step 1: Return cached data instantly (matches iOS completion(viewModels, true, nil))
            nearbyRacesCache?.let { cached ->
                _nearbyRacesUiState.value = UiState.Success(cached)
            } ?: run {
                // No cache — show loading spinner (only on first load)
                _nearbyRacesUiState.value = UiState.Loading
            }

            // Step 2: Fetch fresh data in background (matches iOS forceFetch:true)
            try {
                val result = useCases.getRacesUseCase.fetchNearbyRaces()
                if (result.coordinate == null) {
                    // Only show error if we have no cache to fall back on
                    if (nearbyRacesCache == null) {
                        _nearbyRacesUiState.value =
                            UiState.Error("Unable to determine your location")
                    }
                } else {
                    nearbyRacesCache = result.races
                    _nearbyRacesUiState.value = UiState.Success(result.races)
                }
            } catch (e: Exception) {
                if (nearbyRacesCache == null) {
                    _nearbyRacesUiState.value =
                        UiState.Error(e.localizedMessage ?: "Failed to load nearby races")
                }
            } finally {
                _refreshComplete.value++
            }
        }
    }

    /** Clears the nearby cache. Called when search radius changes. */
    fun invalidateNearbyCache() {
        nearbyRacesCache = null
    }

    /**
     * Fetches joined races using the iOS "cache-first + background refresh" pattern:
     *
     * 1. If cached data exists, emit it immediately (instant tab switch)
     * 2. Fire the API call in the background
     * 3. When fresh data arrives, update the UI silently
     *
     * Only shows a loading spinner if there's no cache (first load).
     */
    fun fetchJoinedRaces() {
        viewModelScope.launch {
            // Step 1: Return cached data instantly (matches iOS completion(viewModels, true, nil))
            joinedRacesCache?.let { cached ->
                _joinedRacesUiState.value = UiState.Success(cached)
            } ?: run {
                // No cache — show loading spinner (only on first load)
                _joinedRacesUiState.value = UiState.Loading
            }

            // Step 2: Fetch fresh data in background (matches iOS forceFetch:true)
            try {
                val races = useCases.getRacesUseCase.fetchJoinedRaces()
                joinedRacesCache = races
                _joinedRacesUiState.value = UiState.Success(races)
            } catch (e: Exception) {
                if (joinedRacesCache == null) {
                    _joinedRacesUiState.value =
                        UiState.Error(e.localizedMessage ?: "Failed to load joined races")
                }
            } finally {
                _refreshComplete.value++
            }
        }
    }

    /** Clears the joined cache. Called when join/resign changes the list. */
    fun invalidateJoinedCache() {
        joinedRacesCache = null
    }

    /**
     * Fetches chapter races using the iOS "cache-first + background refresh" pattern.
     */
    fun fetchChapterRaces() {
        viewModelScope.launch {
            chapterRacesCache?.let { cached ->
                _chapterRacesUiState.value = UiState.Success(cached)
            } ?: run {
                _chapterRacesUiState.value = UiState.Loading
            }

            try {
                val races = useCases.getRacesUseCase.fetchChapterRaces()
                chapterRacesCache = races
                _chapterRacesUiState.value = UiState.Success(races)
            } catch (e: Exception) {
                if (chapterRacesCache == null) {
                    _chapterRacesUiState.value =
                        UiState.Error(e.localizedMessage ?: "Failed to load chapter races")
                }
            } finally {
                _refreshComplete.value++
            }
        }
    }

    /** Clears the chapter cache. */
    fun invalidateChapterCache() {
        chapterRacesCache = null
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

    fun fetchPilotChapters(pilotUserName: String) {
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

    fun fetchChapter(chapterId: String) {
        viewModelScope.launch {
            useCases.getChaptersUseCase(chapterId).collect { chapter ->
                _chapterDetailsUiState.value = UiState.Success(chapter)
            }
        }
    }

    fun fetchPilotHomeChapter(pilotUserName: String, homeChapterId: String?) {
        viewModelScope.launch {
            fetchPilotChapters(pilotUserName)
            homeChapterId?.let { id ->
                useCases.getChaptersUseCase(id).collect { chapter ->
                    _homeChapterImageUiState.value = UiState.Success(chapter?.mainImageFileName ?: "")
                }
            } ?: run {
                _homeChapterImageUiState.value = UiState.Success("")
            }
        }
    }

    fun saveSearchRadius(radius: Double, unit: String) {
        viewModelScope.launch {
            useCases.getRacesUseCase.saveSearchRadius(radius, unit)
            invalidateNearbyCache()
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
                    invalidateJoinedCache()
                    fetchJoinedRaces()
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
                    invalidateJoinedCache()
                    fetchJoinedRaces()
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