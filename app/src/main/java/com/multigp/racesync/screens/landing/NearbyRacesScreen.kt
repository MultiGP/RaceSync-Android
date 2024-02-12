package com.multigp.racesync.screens.landing

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomAlertDialog
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.cells.ChapterCell
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.ChaptersUiState
import com.multigp.racesync.viewmodels.LandingViewModel

@Composable
fun NearbyRacesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel()
) {
    val uiState by remember { viewModel.nearbyUiState }.collectAsState()
    LaunchedEffect(key1 = true){
        viewModel.fetchNearbyChapters()
    }
    when (val state = uiState) {
        is ChaptersUiState.Loading -> ProgressHUD(
            modifier = modifier,
            text = R.string.landing_loading_chapters
        )

        is ChaptersUiState.Success -> LazyColumn() {
            items(state.chapters) {chapter ->
                ChapterCell(chapter)
            }
        }

        is ChaptersUiState.Error -> CustomAlertDialog(
            title = "Error loading Chapters",
            body = state.message,
            onDismiss = {}
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