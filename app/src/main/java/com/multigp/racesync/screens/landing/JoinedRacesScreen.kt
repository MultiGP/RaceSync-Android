package com.multigp.racesync.screens.landing


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.RaceCell
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun JoinedRacesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onRaceSelected: (Race) -> Unit = {}
) {
    val racePagingItems = viewModel.joinedRacesPagingData.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        viewModel.fetchJoinedRaces()
    }
    LazyColumn {
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
                    onClick = onRaceSelected
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

                loadState.refresh is LoadState.Error -> {
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
                                viewModel.fetchJoinedRaces()
                            }
                        )
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
                                viewModel.fetchJoinedRaces()
                            }
                        )
                    }
                }

                loadState.append.endOfPaginationReached && racePagingItems.itemCount == 0 -> {
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.placeholder_title_no_races),
                            message = stringResource(R.string.placeholder_message_no_races),
                            buttonTitle = stringResource(R.string.placeholder_btn_title_search_nearby),
                            canRetry = true,
                            onButtonClick = {}
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true, heightDp = 200)
//@Composable
//fun JoinedRacesScreenPreview() {
//    RaceSyncTheme {
//        JoinedRacesScreen()
//    }
//}