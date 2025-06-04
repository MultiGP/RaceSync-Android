package com.multigp.racesync.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrawerContentViewModel @Inject constructor (
    val useCases: RaceSyncUseCases,
): ViewModel() {

    private val _notificationPreference = MutableStateFlow(false)
    val notificationPreference: StateFlow<Boolean> = _notificationPreference

    init {
        viewModelScope.launch {
            useCases.performLoginUseCase.getNotificationPreference()
                .collectLatest { preference ->
                    _notificationPreference.value = preference
                }
        }
    }

    fun updateFCMToken(fcmToken: String){
        viewModelScope.launch {
            val action = if (_notificationPreference.value) "delete" else "create"
            useCases.performLoginUseCase(action, fcmToken).collect { }
        }
    }
}
