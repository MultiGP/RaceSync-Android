package com.multigp.racesync.screens.racedetails

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.bottombars.RaceDetailsBottomBar
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.navigation.raceDetailTabs
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState
import get

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RaceDetailsContainerScreen(
    raceId: String,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onPilotSelected: (String) -> Unit = {},
    onGoBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    val uiState by viewModel.raceDetailsUiState.collectAsState()
    val joinRaceUiState by viewModel.joinRaceUiState.collectAsState()
    val resignRaceUiState by viewModel.resignRaceUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRaceView(raceId)
    }

    Scaffold(
        topBar = {
            RaceDetailsTopBar(
                title = stringResource(id = R.string.title_race_details),
                onGoBack = {multipleEventsCutter.processEvent(onGoBack)},
            )
        },
        bottomBar = {
            RaceDetailsBottomBar(raceDetailTabs, pagerState, modifier = modifier)
        }
    )
    { paddingValues ->
        when(uiState){
            is UiState.Loading -> {
                ProgressHUD(
                    modifier = modifier,
                    text = R.string.progress_race_details
                )
            }
            is UiState.Success ->{
                val data = (uiState as UiState.Success).data
                HorizontalPager(
                    state = pagerState,
                    count = raceDetailTabs.size,
                    itemSpacing = 16.dp,
                    modifier = modifier.padding(paddingValues),
                ) { page ->
                    when (page) {
                        0 -> RaceDetailsScreen(data, modifier, joinRaceUiState, resignRaceUiState)
                        1 -> RaceRosterScreen(data, modifier, onPilotSelected = onPilotSelected)
                    }
                }
            }
            is UiState.Error -> {
                val message = (uiState as UiState.Error).message
                PlaceholderScreen(
                    modifier = modifier,
                    title = stringResource(R.string.error_title_race_details),
                    message = message,
                    buttonTitle = stringResource(R.string.error_btn_title_retry),
                    isError = true,
                    canRetry = true,
                    onButtonClick = {
                        viewModel.fetchNearbyRaces()
                    })
            }
            else -> {}
        }
    }
}



