package com.multigp.racesync.screens.series

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
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.bottombars.RaceDetailsBottomBar
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.navigation.seriesDetailTabs
import com.multigp.racesync.viewmodels.SeriesDetailsViewModel
import com.multigp.racesync.viewmodels.UiState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SeriesDetailsContainerScreen(
    seriesId: String,
    modifier: Modifier = Modifier,
    viewModel: SeriesDetailsViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(seriesId) {
        viewModel.load(seriesId)
    }

    Scaffold(
        topBar = {
            RaceDetailsTopBar(
                title = stringResource(R.string.series_details_title),
                onGoBack = onGoBack
            )
        },
        bottomBar = {
            RaceDetailsBottomBar(seriesDetailTabs, pagerState, modifier = modifier)
        }
    ) { paddingValues ->
        when (val current = state) {
            is UiState.Loading,
            is UiState.None -> ProgressHUD(
                modifier = modifier,
                text = R.string.series_details_loading
            )

            is UiState.Error -> PlaceholderScreen(
                modifier = modifier,
                title = stringResource(R.string.series_details_error_title),
                message = current.message,
                buttonTitle = stringResource(R.string.error_btn_title_retry),
                isError = true,
                canRetry = true,
                onButtonClick = { viewModel.load(seriesId) }
            )

            is UiState.Success -> SeriesDetailsPager(
                series = current.data,
                pagerState = pagerState,
                modifier = modifier.padding(paddingValues)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SeriesDetailsPager(
    series: Series,
    pagerState: com.google.accompanist.pager.PagerState,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        count = seriesDetailTabs.size,
        itemSpacing = 16.dp,
        modifier = modifier
    ) { page ->
        when (page) {
            0 -> SeriesDetailsTab(series = series)
            1 -> SeriesRacesTab(series = series)
            2 -> SeriesLeaderboardTab(series = series)
        }
    }
}
