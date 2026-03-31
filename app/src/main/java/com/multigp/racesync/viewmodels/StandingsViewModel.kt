package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.domain.model.Standing
import com.multigp.racesync.domain.model.StandingSeason
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StandingsViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
) : ViewModel() {

    private val _standingsUiState = MutableStateFlow<UiState<List<Standing>>>(UiState.None)
    val standingsUiState: StateFlow<UiState<List<Standing>>> = _standingsUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _myUserId = MutableStateFlow<String?>(null)
    val myUserId: StateFlow<String?> = _myUserId.asStateFlow()

    private val _myStanding = MutableStateFlow<Standing?>(null)
    val myStanding: StateFlow<Standing?> = _myStanding.asStateFlow()

    private val _myProfilePictureUrl = MutableStateFlow<String?>(null)
    val myProfilePictureUrl: StateFlow<String?> = _myProfilePictureUrl.asStateFlow()

    private var allStandings: List<Standing> = emptyList()
    private var currentSeason: StandingSeason? = null

    init {
        fetchLoggedInUser()
    }

    private fun fetchLoggedInUser() {
        viewModelScope.launch {
            try {
                useCases.getLoginInfoUseCase().collect { (_, userInfo) ->
                    _myUserId.value = userInfo?.id
                }
            } catch (_: Exception) {}

            try {
                useCases.getProfileUseCase().collect { profile ->
                    _myProfilePictureUrl.value = profile.profilePictureUrl
                }
            } catch (_: Exception) {}
        }
    }

    fun fetchStandings(season: StandingSeason) {
        if (season == currentSeason && allStandings.isNotEmpty()) return

        currentSeason = season
        viewModelScope.launch {
            _standingsUiState.value = UiState.Loading
            try {
                useCases.getStandingsUseCase(season).collect { standings ->
                    allStandings = standings
                    // Find logged-in user's standing
                    val userId = _myUserId.value
                    _myStanding.value = if (userId != null) {
                        standings.find { it.userId == userId }
                    } else null
                    _standingsUiState.value = UiState.Success(applyFilter(standings))
                }
            } catch (exception: Exception) {
                _standingsUiState.value = UiState.Error(
                    exception.localizedMessage ?: "Failed to load standings"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (allStandings.isNotEmpty()) {
            _standingsUiState.value = UiState.Success(applyFilter(allStandings))
        }
    }

    fun refresh() {
        currentSeason?.let { season ->
            currentSeason = null
            allStandings = emptyList()
            fetchStandings(season)
        }
    }

    private fun applyFilter(standings: List<Standing>): List<Standing> {
        val query = _searchQuery.value.trim()
        if (query.length < 2) return standings

        val normalizedQuery = query.lowercase()

        return standings.filter { standing ->
            val searchableText = "${standing.firstName} ${standing.userName} ${standing.lastName} ${standing.country}".lowercase()
            val tokens = searchableText.split(Regex("[^\\p{L}\\p{N}]+"))
            tokens.any { it.startsWith(normalizedQuery) } || searchableText.contains(normalizedQuery)
        }
    }
}
