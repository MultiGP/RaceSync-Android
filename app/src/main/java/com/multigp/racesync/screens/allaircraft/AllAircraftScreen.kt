package com.multigp.racesync.screens.allaircraft

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.navigation.AllAircraft
import com.multigp.racesync.navigation.NavDestination
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.AllAircraftViewModel

@Composable
fun AllAircraftScreen(
    pilotId:String,
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: AllAircraftViewModel = hiltViewModel()
){
    val allAircraftUiState by viewModel.uiState.collectAsState()
    AllAircraftContent(
        pilotId = pilotId,
        onGoBack = onGoBack
    )
}

@Composable
fun AllAircraftContent(
    pilotId:String,
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},

){
    Column (
        modifier = modifier.fillMaxSize()
    ){
        RaceDetailsTopBar(
            title = AllAircraft.title,
            onGoBack = onGoBack
        )

        Text(text = pilotId, modifier = modifier.padding(25.dp))
    }

}
