package com.multigp.racesync.screens.landing

import androidx.compose.foundation.clickable
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
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.ChaptersUiState
import com.multigp.racesync.viewmodels.LandingViewModel

@Composable
fun ChaptersScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onChapterSelected: (Chapter) -> Unit = {}
) {
    val uiState by remember { viewModel.chaptersUiState }.collectAsState()
    LaunchedEffect(key1 = true) {
        viewModel.fetchChapters()
    }

    when (val state = uiState) {
        is ChaptersUiState.Loading -> LazyColumn() {
            items(10) { _ ->
                ChapterLoadingCell(modifier)
            }
        }

        is ChaptersUiState.Success -> {
            if (state.chapters.isNotEmpty()) {
                LazyColumn() {
                    items(state.chapters) { chapter ->
                        ChapterCell(
                            chapter,
                            modifier = modifier,
                            onClick = onChapterSelected
                        )
                    }
                }
            } else {
                PlaceholderScreen(
                    modifier = modifier,
                    title = stringResource(R.string.placeholder_title_no_chapters),
                    message = stringResource(R.string.placeholder_message_no_chapters),
                    canRetry = false,
                )
            }
        }

        is ChaptersUiState.Error -> PlaceholderScreen(
            modifier = modifier,
            title = stringResource(R.string.error_title_loading_chapters),
            message = state.message,
            buttonTitle = stringResource(R.string.error_btn_title_retry),
            isError = true,
            canRetry = true,
            onButtonClick = {
                viewModel.fetchJoinedRaces()
            },
        )
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun ChaptersScreenPreview() {
    RaceSyncTheme {
        ChaptersScreen()
    }
}