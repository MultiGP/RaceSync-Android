package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.navigation.LandingTopBar
import com.multigp.racesync.navigation.landingTabs
import com.multigp.racesync.ui.theme.RaceSyncTheme

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LandingScreen(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState()
    Scaffold(
        topBar = { LandingTopBar(tabs = landingTabs, pagerState = pagerState) }
    )
    { paddingValues ->
        HorizontalPager(
            state = pagerState,
            count = landingTabs.size,
            modifier = modifier.padding(paddingValues)
        ) { page ->
            landingTabs[page].screen()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LandingScreenView() {
    RaceSyncTheme {
        LandingScreen()
    }
}