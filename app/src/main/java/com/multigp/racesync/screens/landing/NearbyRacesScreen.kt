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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterCell
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.ChaptersUiState
import com.multigp.racesync.viewmodels.LandingViewModel

@Composable
fun NearbyRacesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel()
) {
    val uiState by remember { viewModel.nearbyUiState }.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.fetchNearbyChapters()
    }
    when (val state = uiState) {
        is ChaptersUiState.Loading -> LazyColumn() {
            items(10) { _ ->
                ChapterLoadingCell()
            }
        }

        is ChaptersUiState.Success -> {
            if (state.chapters.isNotEmpty()) {
                LazyColumn() {
                    items(state.chapters) { chapter ->
                        ChapterCell(chapter)
                    }
                }
            } else {
                PlaceholderScreen(
                    modifier = modifier,
                    title = stringResource(R.string.placeholder_title_no_races),
                    message = stringResource(R.string.placeholder_message_no_nearby_races),
                    canRetry = false
                )
            }
        }

        is ChaptersUiState.Error -> PlaceholderScreen(
            modifier = modifier,
            title = stringResource(R.string.error_title_loading_races),
            message = state.message,
            buttonTitle = stringResource(R.string.error_btn_title_retry),
            isError = true,
            canRetry = true,
            onButtonClick = {
                viewModel.fetchNearbyChapters()
            }
        )
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun NearbyRacesScreenPreview() {
    RaceSyncTheme {
        NearbyRacesScreen()
    }
}