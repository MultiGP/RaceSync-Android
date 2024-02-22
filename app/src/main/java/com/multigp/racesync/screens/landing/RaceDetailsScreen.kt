package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel

@Composable
fun RaceDetailsScreen(
    raceId: String,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel()
){
    val race by remember{ viewModel.fetchRace(raceId)}.collectAsState(initial = null)

    Surface {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(text = race?.name ?: "Unknown Race")
        }
    }
}


@Preview
@Composable
fun RaceDetailsScreenPreview(){
    RaceSyncTheme {
        RaceDetailsScreen(raceId = "26217")
    }
}