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

    private var allStandings: List<Standing> = emptyList()
    private var currentSeason: StandingSeason? = null

    fun fetchStandings(season: StandingSeason) {
        // Don't re-fetch if we already have this season loaded
        if (season == currentSeason && allStandings.isNotEmpty()) return

        currentSeason = season
        viewModelScope.launch {
            _standingsUiState.value = UiState.Loading
            try {
                useCases.getStandingsUseCase(season).collect { standings ->
                    allStandings = standings
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
        val currentState = _standingsUiState.value
        if (currentState is UiState.Success || allStandings.isNotEmpty()) {
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
