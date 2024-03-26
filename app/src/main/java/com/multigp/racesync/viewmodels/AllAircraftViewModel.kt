package com.multigp.racesync.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.BuildConfig
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AllAircraftUiState(
    val isLoading: Boolean = false,
    val title: String = "My Aircraft",
    val allAircraft: List<Aircraft> = emptyList()

)

@HiltViewModel
class AllAircraftViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow(AllAircraftUiState())
    val uiState: StateFlow<AllAircraftUiState> = _uiState.asStateFlow()

    fun fetchAllAircraft() {
        Log.d("T", "Fetching aircraft for current user")

        viewModelScope.launch {
            delay(5000)
            useCases.getAllAircraftUseCase()
                .collect { aircrafts ->
                    _uiState.update { curr ->
                        curr.copy(isLoading = false, allAircraft = aircrafts)
                    }
                }
        }
    }
}