package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeriesDetailsViewModel @Inject constructor(
    private val useCases: RaceSyncUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<UiState<Series>>(UiState.None)
    val state: StateFlow<UiState<Series>> = _state.asStateFlow()

    /** Current user id — drives the owner-only approver UI. */
    private val _myUserId = MutableStateFlow<String?>(null)
    val myUserId: StateFlow<String?> = _myUserId.asStateFlow()

    private val _joinRaceUiState = MutableStateFlow<UiState<Boolean>>(UiState.None)
    val joinRaceUiState: StateFlow<UiState<Boolean>> = _joinRaceUiState.asStateFlow()

    private val _resignRaceUiState = MutableStateFlow<UiState<Boolean>>(UiState.None)
    val resignRaceUiState: StateFlow<UiState<Boolean>> = _resignRaceUiState.asStateFlow()

    /** Result of approve / unapprove / remove. Surfaces error dialogs only — success is silent. */
    private val _approverActionUiState = MutableStateFlow<UiState<Unit>>(UiState.None)
    val approverActionUiState: StateFlow<UiState<Unit>> = _approverActionUiState.asStateFlow()

    /** ID of the race whose action is currently in flight (drives the row spinner). */
    private val _loadingRaceId = MutableStateFlow<String?>(null)
    val loadingRaceId: StateFlow<String?> = _loadingRaceId.asStateFlow()

    private var loadedSeriesId: String? = null

    init {
        loadProfile()
    }

    fun load(seriesId: String) {
        loadedSeriesId = seriesId
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val series = useCases.getSeriesUseCase.detail(seriesId)
                _state.value = UiState.Success(series)
            } catch (e: Exception) {
                _state.value = UiState.Error(
                    e.localizedMessage ?: "Failed to load series"
                )
            }
        }
    }

    // ── Join / Resign ──

    fun joinRace(raceId: String) {
        viewModelScope.launch {
            _loadingRaceId.value = raceId
            try {
                useCases.getRacesUseCase.joinRace(raceId).collect {
                    _joinRaceUiState.value = UiState.Success(it)
                    refreshSeries()
                }
            } catch (e: Exception) {
                _joinRaceUiState.value = UiState.Error(
                    e.localizedMessage ?: "Failed to join the race"
                )
            } finally {
                _loadingRaceId.value = null
            }
        }
    }

    fun resignFromRace(raceId: String) {
        viewModelScope.launch {
            _loadingRaceId.value = raceId
            try {
                useCases.getRacesUseCase.resignFromRace(raceId).collect {
                    _resignRaceUiState.value = UiState.Success(it)
                    refreshSeries()
                }
            } catch (e: Exception) {
                _resignRaceUiState.value = UiState.Error(
                    e.localizedMessage ?: "Failed to resign from the race"
                )
            } finally {
                _loadingRaceId.value = null
            }
        }
    }

    fun acknowledgeJoinRaceUi() { _joinRaceUiState.value = UiState.None }
    fun acknowledgeResignRaceUi() { _resignRaceUiState.value = UiState.None }

    // ── Owner-only approver actions ──

    fun approveRace(raceId: String) = runApproverAction(raceId, "approve") { seriesId ->
        useCases.getSeriesUseCase.approveRace(seriesId, raceId)
    }

    fun unapproveRace(raceId: String) = runApproverAction(raceId, "unapprove") { seriesId ->
        useCases.getSeriesUseCase.unapproveRace(seriesId, raceId)
    }

    fun removeRaceFromSeries(raceId: String) = runApproverAction(raceId, "remove") { seriesId ->
        useCases.getSeriesUseCase.removeRaceFromSeries(seriesId, raceId)
    }

    fun acknowledgeApproverActionUi() { _approverActionUiState.value = UiState.None }

    // ── Internals ──

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                useCases.getProfileUseCase().collect { profile ->
                    _myUserId.value = profile.id
                }
            } catch (_: Exception) {
                // Without a profile we just hide the owner UI — no need to surface this.
            }
        }
    }

    private fun runApproverAction(
        raceId: String,
        action: String,
        block: suspend (seriesId: String) -> Unit
    ) {
        val seriesId = loadedSeriesId ?: return
        viewModelScope.launch {
            _loadingRaceId.value = raceId
            try {
                block(seriesId)
                _approverActionUiState.value = UiState.Success(Unit)
                refreshSeries()
            } catch (e: Exception) {
                _approverActionUiState.value = UiState.Error(
                    e.localizedMessage ?: "Failed to $action this race"
                )
            } finally {
                _loadingRaceId.value = null
            }
        }
    }

    /**
     * Re-fetches the series so the races list (and participant counts) reflect the
     * server state after a join/resign or approver action. Falls back silently if
     * the series id was lost or the refresh fails — the user's action already succeeded.
     */
    private fun refreshSeries() {
        val id = loadedSeriesId ?: return
        viewModelScope.launch {
            try {
                val series = useCases.getSeriesUseCase.detail(id)
                _state.value = UiState.Success(series)
            } catch (_: Exception) {
                // Keep the current state on refresh failure.
            }
        }
    }
}
