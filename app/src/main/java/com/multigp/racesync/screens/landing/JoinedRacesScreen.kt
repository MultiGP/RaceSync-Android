package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.RaceCell
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinedRacesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onRaceSelected: (Race) -> Unit = {},
    gotoNearbyRaces: () -> Unit = {},
    onJoinRace: (Race) -> Unit = {}
) {
    val uiState by viewModel.joinedRacesUiState.collectAsState()
    val refreshComplete by viewModel.refreshComplete.collectAsState()
    val loadingRaceId by viewModel.loadingRaceId.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    // Initial load
    LaunchedEffect(Unit) {
        viewModel.fetchJoinedRaces()
    }

    // Handle pull-to-refresh
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.invalidateJoinedCache()
            viewModel.fetchJoinedRaces()
        }
    }

    // Stop refresh spinner when fetch completes (counter avoids StateFlow deduplication)
    LaunchedEffect(refreshComplete) {
        pullRefreshState.endRefresh()
    }

    Box(modifier = modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
        when (uiState) {
            is UiState.Loading -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { ChapterLoadingCell() }
                }
            }

            is UiState.Success -> {
                val races = (uiState as UiState.Success<List<Race>>).data
                if (races.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            PlaceholderScreen(
                                modifier = modifier,
                                title = stringResource(R.string.placeholder_title_no_races),
                                message = stringResource(R.string.placeholder_message_no_races),
                                buttonTitle = stringResource(R.string.placeholder_btn_title_search_nearby),
                                canRetry = true,
                                onButtonClick = gotoNearbyRaces
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(items = races, key = { it.id }) { race ->
                            RaceCell(
                                race,
                                modifier = Modifier,
                                isLoading = loadingRaceId == race.id,
                                onClick = onRaceSelected,
                                onRaceAction = onJoinRace
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                val errorMessage = (uiState as UiState.Error).message
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.error_title_loading_races),
                            message = errorMessage,
                            buttonTitle = stringResource(R.string.error_btn_title_retry),
                            isError = true,
                            canRetry = true,
                            onButtonClick = { viewModel.fetchJoinedRaces() }
                        )
                    }
                }
            }

            is UiState.None -> { /* Initial state — nothing to show yet */ }
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
