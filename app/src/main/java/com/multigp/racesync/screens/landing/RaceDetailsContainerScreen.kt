package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RaceDetailsContainerScreen(
    raceId: String,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState()

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
        HorizontalPager(
            state = pagerState,
            count = raceDetailTabs.size,
            itemSpacing = 16.dp,
            modifier = modifier.padding(paddingValues),
        ) { page ->
            when (page) {
                0 -> RaceDetailsScreen(raceId = raceId, modifier = modifier)
                1 -> RaceRosterScreen(modifier = modifier)
            }
        }
    }
}



