package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun ChapterDetailsScreen(
    chapterId: String,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel()
) {

    val uiState by viewModel.chapterDetailsUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchChapter(chapterId)
    }

    when (uiState) {
        is UiState.Loading -> {

        }

        is UiState.Success -> {
            val chapter = (uiState as UiState.Success).data
            Surface {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = chapter.name ?: "Unknown chapter")
                }
            }
        }

        is UiState.Error -> {

        }
    }
}


@Preview
@Composable
fun ChapterDetailsScreenPreview() {
    RaceSyncTheme {
        ChapterDetailsScreen(chapterId = "2057")
    }
}