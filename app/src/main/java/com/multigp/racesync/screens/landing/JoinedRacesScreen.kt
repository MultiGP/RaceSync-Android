package com.multigp.racesync.screens.landing

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterCell
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.RaceCell
import com.multigp.racesync.viewmodels.ChaptersUiState
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.RaceUiState

@Composable
fun JoinedRacesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel()
) {
    val uiState by remember { viewModel.JoinedUiState }.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.fetchJoinedRaces()
    }

    when (val state = uiState) {
        is RaceUiState.Loading -> LazyColumn() {
            items(10) { _ ->
                ChapterLoadingCell(modifier)
            }
        }

        is RaceUiState.Success -> {
            if (state.races.isNotEmpty()) {
                LazyColumn() {
                    items(state.races) { race ->
                        RaceCell(race, modifier = modifier)
                    }
                }
            } else {
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

        is RaceUiState.Error -> PlaceholderScreen(
            modifier = modifier,
            title = stringResource(R.string.error_title_loading_races),
            message = state.message,
            buttonTitle = stringResource(R.string.error_btn_title_retry),
            isError = true,
            canRetry = true,
            onButtonClick = {
                viewModel.fetchJoinedRaces()
            }
        )
    }
}

//@Preview(showBackground = true, heightDp = 200)
//@Composable
//fun JoinedRacesScreenPreview() {
//    RaceSyncTheme {
//        JoinedRacesScreen()
//    }
//}