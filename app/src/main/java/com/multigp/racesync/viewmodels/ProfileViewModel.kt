package com.multigp.racesync.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multigp.racesync.BuildConfig
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.useCase.RaceSyncUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val id: String = "",
    val profileBackgroundUrl: String= "",
    val profilePictureUrl: String= "",
    val userName: String= "",
    val displayName: String= "",
    val raceCount: String= "",
    val chapterCount: String= "",
    val city: String= "",

)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val useCases: RaceSyncUseCases
): ViewModel(){
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    var isDialogShown by mutableStateOf(false)
        private set

    fun onShowQrCode(){
        isDialogShown = true
    }

    fun onDismissDialog(){
        isDialogShown = false
    }


    init{
        Log.d("TAG", "Hello World")
        val apikey = BuildConfig.API_KEY

        viewModelScope.launch {
            useCases.getProfileUseCase(apikey)
                .collect{
                    val data = it.data!!
                    val name = data.displayName
                    _uiState.update { curr ->
                        curr.copy(
                            isLoading = false,
                            displayName = name,
                            profileBackgroundUrl = data.profileBackgroundUrl,
                            profilePictureUrl =  data.profilePictureUrl,
                            city = data.city,
                            chapterCount = data.chapterCount + " Chapters",
                            raceCount =  data.raceCount + " Races",
                            userName = data.userName,
                            id = data.id
                        )
                    }

                }
        }
    }



}