package com.multigp.racesync.screens.racedetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.R
import com.multigp.racesync.composables.bottombars.RaceDetailsBottomBar
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.navigation.raceDetailTabs
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RaceDetailsContainerScreen(
    raceId: String,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val uiState by viewModel.raceDetailsUiState.collectAsState()
    val joinRaceUiState by viewModel.joinRaceUiState.collectAsState()
    val resignRaceUiState by viewModel.resignRaceUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRace(raceId)
    }

    Scaffold(
        topBar = {
            RaceDetailsTopBar(
                title = stringResource(id = R.string.title_race_details),
                onGoBack = onGoBack,
            )
        },
        bottomBar = {
            RaceDetailsBottomBar(raceDetailTabs, pagerState, modifier = modifier)
        }
    )
    { paddingValues ->
        when(uiState){
            is UiState.Success ->{
                val race = (uiState as UiState.Success).data
                HorizontalPager(
                    state = pagerState,
                    count = raceDetailTabs.size,
                    itemSpacing = 16.dp,
                    modifier = modifier.padding(paddingValues),
                ) { page ->
                    when (page) {
                        0 -> RaceDetailsScreen(race, modifier, joinRaceUiState, resignRaceUiState)
                        1 -> RaceRosterScreen(race, modifier)
                    }
                }
            }
            else -> {}
        }
    }
}



