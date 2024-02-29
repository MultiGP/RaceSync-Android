package com.multigp.racesync.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.BuildConfig
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        Log.d("TAG", "Fetching all aircraft for this user.")
        val apikey = BuildConfig.API_KEY


    }

    fun fetchAllAircraft(pilotId: String) {
        Log.d("T", "Fetching aircraft for $pilotId")

        viewModelScope.launch {
            val apikey = BuildConfig.API_KEY
            val piltoIdInt = pilotId.toInt()
            useCases.getAllAircraftUseCase(apikey, piltoIdInt)
                .collect() {
                    val status = it.status


                    if (it.data != null && it.status) {
                        val data = it.data!!
                        _uiState.update { curr ->
                            curr.copy(isLoading = false, allAircraft = data)

                        }
                    }
                }
        }
    }
}