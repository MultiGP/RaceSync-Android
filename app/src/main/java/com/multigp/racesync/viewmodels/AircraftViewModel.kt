package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


data class AircraftUiState(
    val isLoading: Boolean = false,


    )

@HiltViewModel
class AircraftViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
): ViewModel() {
}