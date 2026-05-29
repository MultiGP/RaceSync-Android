package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.data.prefs.DataStoreManager
import com.multigp.racesync.domain.model.io.Event
import com.multigp.racesync.domain.model.io.EventActivityCategory
import com.multigp.racesync.domain.model.io.EventSession
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.domain.model.io.byCategory
import com.multigp.racesync.domain.model.io.byTracks
import com.multigp.racesync.domain.model.io.forDay
import com.multigp.racesync.domain.model.io.initialDate
import com.multigp.racesync.domain.model.io.io26Dates
import com.multigp.racesync.domain.model.io.merged
import com.multigp.racesync.domain.model.io.parsedDate
import com.multigp.racesync.domain.model.io.withActivity
import com.multigp.racesync.domain.repositories.EventSessionBucketlist
import com.multigp.racesync.domain.repositories.IoScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class IoScheduleViewModel @Inject constructor(
    private val repository: IoScheduleRepository,
    private val bucketlist: EventSessionBucketlist,
    private val prefs: DataStoreManager,
) : ViewModel() {

    private val _eventUiState = MutableStateFlow<UiState<Event>>(UiState.None)
    val eventUiState: StateFlow<UiState<Event>> = _eventUiState.asStateFlow()

    private val _selectedDate = MutableStateFlow<Date?>(null)
    val selectedDate: StateFlow<Date?> = _selectedDate.asStateFlow()

    private val _selectedCategory = MutableStateFlow(EventActivityCategory.All)
    val selectedCategory: StateFlow<EventActivityCategory> = _selectedCategory.asStateFlow()

    private val _selectedTrackIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedTrackIds: StateFlow<Set<String>> = _selectedTrackIds.asStateFlow()

    val bucketedIds: StateFlow<Set<String>> = bucketlist.bucketedIds

    val hideSchedulerAlerts: StateFlow<Boolean> = prefs.getHideIoSchedulerAlerts
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** Non-null when the UI should show the "we'll notify you 1h before" dialog. */
    private val _pendingAlertActivity = MutableStateFlow<String?>(null)
    val pendingAlertActivity: StateFlow<String?> = _pendingAlertActivity.asStateFlow()

    /** All event dates in order. Empty until the first fetch succeeds. */
    val dates: StateFlow<List<Date>> = _eventUiState
        .map { state -> if (state is UiState.Success) io26Dates(EVENT_START, EVENT_END) else emptyList() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Sessions for the selected day, merged then filtered by category AND track.
     *
     * Pipeline runs on [Dispatchers.Default] (off the main thread). The StateFlow's
     * initial value is `null`, which the screen uses as a "not yet computed" signal so
     * it can keep the cheap loader on screen until the first real emission lands.
     * `WhileSubscribed(5s)` defers the work to when the screen actually subscribes and
     * keeps it warm for short tab-out-and-back hops.
     */
    val displayedSessions: StateFlow<List<EventSession>?> = combine(
        _eventUiState,
        _selectedDate,
        _selectedCategory,
        _selectedTrackIds,
        bucketedIds,
    ) { state, date, category, trackIds, ids ->
        val event = (state as? UiState.Success)?.data ?: return@combine null
        val day = date ?: return@combine null
        event.sessions
            .forDay(day, eventZone)
            .withActivity()
            .merged()
            .byCategory(category, ids)
            .byTracks(trackIds)
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch {
            // Load the persisted bucket first so the schedule's star state is correct
            // on the very first render, not just after the user toggles something.
            bucketlist.warmUp()
            _selectedCategory.value = prefs.getSelectedIoCategory.first()
            _selectedTrackIds.value = prefs.getSelectedIoTrackIds.first()
        }
        load()
    }

    fun load() {
        if (_eventUiState.value is UiState.Loading) return
        // On pull-to-refresh we already have a Success — don't downgrade to Loading,
        // it would tear down the schedule UI and trigger the full-screen loader.
        val alreadyLoaded = _eventUiState.value is UiState.Success
        viewModelScope.launch {
            if (!alreadyLoaded) _eventUiState.value = UiState.Loading
            try {
                repository.fetchEvent().collect { event ->
                    _eventUiState.value = UiState.Success(event)
                    if (_selectedDate.value == null) {
                        _selectedDate.value = io26Dates(EVENT_START, EVENT_END).initialDate(eventZone)
                    }
                }
            } catch (t: Throwable) {
                if (_eventUiState.value !is UiState.Success) {
                    _eventUiState.value =
                        UiState.Error(t.localizedMessage ?: "Failed to load IO schedule")
                }
            }
        }
    }

    fun selectDate(date: Date) {
        _selectedDate.value = date
    }

    fun selectCategory(category: EventActivityCategory) {
        if (_selectedCategory.value == category) return
        _selectedCategory.value = category
        viewModelScope.launch { prefs.setSelectedIoCategory(category) }
    }

    fun toggleTrack(trackId: String) {
        val next = _selectedTrackIds.value.toMutableSet().apply {
            if (!add(trackId)) remove(trackId)
        }
        _selectedTrackIds.value = next
        viewModelScope.launch { prefs.setSelectedIoTrackIds(next) }
    }

    fun clearTracks() {
        if (_selectedTrackIds.value.isEmpty()) return
        _selectedTrackIds.value = emptySet()
        viewModelScope.launch { prefs.setSelectedIoTrackIds(emptySet()) }
    }

    /** Toggles bucket membership for [session]. Shows the "see you at" alert on add. */
    fun toggleBucket(session: EventSession) {
        val day = session.parsedDate() ?: return
        val wasAttending = session.id in bucketedIds.value
        viewModelScope.launch {
            if (wasAttending) {
                bucketlist.remove(session, day)
            } else {
                bucketlist.add(session, day)
                if (!hideSchedulerAlerts.value) {
                    _pendingAlertActivity.value = session.activity.orEmpty()
                }
            }
        }
    }

    fun dismissSchedulerAlert() {
        _pendingAlertActivity.value = null
    }

    fun acceptDontShowSchedulerAlerts() {
        _pendingAlertActivity.value = null
        viewModelScope.launch { prefs.setHideIoSchedulerAlerts(true) }
    }

    private val eventZone: TimeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)

    companion object {
        // Hardcoded to match iOS EventsController.ios26Dates. Update here when the event window moves.
        const val EVENT_START = "2026-06-10"
        const val EVENT_END = "2026-06-14"
    }
}
