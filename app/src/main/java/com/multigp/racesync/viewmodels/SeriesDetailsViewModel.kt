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

    fun load(seriesId: String) {
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
}
