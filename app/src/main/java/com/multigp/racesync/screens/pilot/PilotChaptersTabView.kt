package com.multigp.racesync.screens.pilot

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterCell
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.viewmodels.PilotViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun PilotChaptersTabView(
    pilotUserName: String,
    viewModel: PilotViewModel,
    modifier: Modifier = Modifier
) {
    val chaptersUiState by viewModel.chaptersUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchChapters(pilotUserName)
    }

    LazyColumn{
        when(chaptersUiState){
            is UiState.Loading -> {
                item {
                    ChapterLoadingCell()
                }
            }
            is UiState.Success -> {
                val races = (chaptersUiState as UiState.Success).data
                items(items = races, key = { it.id }) { race ->
                    ChapterCell(
                        race,
                        modifier = modifier,
                        onClick = {}
                    )
                }
            }
            is UiState.Error -> {
                val errorMessage = (chaptersUiState as UiState.Error).message
                item {
                    PlaceholderScreen(
                        modifier = modifier,
                        title = stringResource(R.string.error_title_loading_chapters),
                        message = errorMessage ?: "",
                        isError = true
                    )
                }
            }
            else -> {}
        }
    }
}