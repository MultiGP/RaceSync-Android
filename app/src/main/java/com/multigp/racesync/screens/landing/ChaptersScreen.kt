package com.multigp.racesync.screens.landing

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.RaceCell
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun ChaptersScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onChapterSelected: (Race) -> Unit = {},
    gotoNearbyRaces: () -> Unit = {},
    onJoinRace: (Race) -> Unit = {}
) {
    val uiState by viewModel.joineChapterRacesUiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchJoinedChapterRaces()
    }

    when (uiState) {
        is UiState.Loading -> {
            LazyColumn {
                items(1) {
                    ChapterLoadingCell()
                }
            }

        }

        is UiState.Success -> {
            val races = (uiState as UiState.Success).data
            if (races.count() > 0) {
                LazyColumn {
                    items(items = races, key = { it.id }) { race ->
                        RaceCell(
                            race, modifier = modifier,
                            onClick = onChapterSelected,
                            onRaceAction = onJoinRace
                        )
                    }
                }
            } else {
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

        is UiState.Error -> {
            val errorMessage = (uiState as UiState.Error).message
            PlaceholderScreen(modifier = modifier,
                title = stringResource(R.string.error_title_loading_chapters),
                message = errorMessage,
                buttonTitle = stringResource(R.string.error_btn_title_retry),
                isError = true,
                canRetry = true,
                onButtonClick = {
                    viewModel.fetchJoinedChapterRaces()
                })
        }
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun ChaptersScreenPreview() {
    RaceSyncTheme {
        ChaptersScreen()
    }
}