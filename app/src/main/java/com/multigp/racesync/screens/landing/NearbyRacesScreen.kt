package com.multigp.racesync.screens.landing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.RaceCell
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import kotlinx.coroutines.launch

@Composable
fun NearbyRacesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onRaceSelected: (Race) -> Unit = {},
    onJoinRace: (Race) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val racePagingItems = viewModel.nearbyRacesPagingData.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.fetchNearbyRaces()
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(
            items = racePagingItems,
            key = { race ->
                race.id
            }
        ) { race ->
            race?.let {
                RaceCell(
                    it,
                    modifier = modifier,
                    showDistance=true,
                    onClick = onRaceSelected,
                    onRaceAction = onJoinRace,
                    getDistance = { race ->
                        scope.launch {
                            viewModel.getRaceDistance(race)
                        }
                    }
                )
            }
        }

        racePagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { ChapterLoadingCell() }
                }

                loadState.append is LoadState.Loading -> {
                    item { ChapterLoadingCell() }
                }

                loadState.refresh is LoadState.Error  -> {
                    val errorMessage = (loadState.refresh as LoadState.Error).error.message
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.error_title_loading_races),
                            message = errorMessage ?: "",
                            buttonTitle = stringResource(R.string.error_btn_title_retry),
                            isError = true,
                            canRetry = true,
                            onButtonClick = {
                                viewModel.fetchNearbyRaces()
                            })
                    }
                }
                loadState.append is LoadState.Error -> {
                    val errorMessage = (loadState.append as LoadState.Error).error.message
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.error_title_loading_races),
                            message = errorMessage ?: "",
                            buttonTitle = stringResource(R.string.error_btn_title_retry),
                            isError = true,
                            canRetry = true,
                            onButtonClick = {
                                viewModel.fetchNearbyRaces()
                            })
                    }
                }
                loadState.append.endOfPaginationReached && racePagingItems.itemCount == 0 -> {
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.placeholder_title_no_races),
                            message = stringResource(R.string.placeholder_message_no_nearby_races),
                            canRetry = false
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, heightDp = 200)
@Composable
fun NearbyRacesScreenPreview() {
    RaceSyncTheme {
        NearbyRacesScreen()
    }
}