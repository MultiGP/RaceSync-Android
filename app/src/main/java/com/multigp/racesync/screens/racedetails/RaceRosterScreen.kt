package com.multigp.racesync.screens.racedetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun RaceRosterScreen(
    race: Race,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel()
) {
    val pilotsUiState by viewModel.racePilotsUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getPilotsForRace(race.id)
    }

    when(pilotsUiState){
        is UiState.Success -> {
            val (profile, pilots) = (pilotsUiState as UiState.Success).data
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Roster data goes here - ${profile.displayName} - ${pilots.count()}")
            }
        }
        else -> {

        }
    }

}