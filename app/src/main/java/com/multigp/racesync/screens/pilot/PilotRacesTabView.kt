package com.multigp.racesync.screens.pilot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.PilotRaceCell
import com.multigp.racesync.composables.cells.RaceCell
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.viewmodels.PilotViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun PilotRacesTabView(
    pilotUserName: String,
    viewModel: PilotViewModel,
    modifier: Modifier = Modifier,
    onRaceSelected: (Race) -> Unit = {},
) {
    val racesUiState by viewModel.racesUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRaces(pilotUserName)
    }
    LazyColumn{
        when(racesUiState){
            is UiState.Loading -> {
                item {
                    ChapterLoadingCell()
                }
            }
            is UiState.Success -> {
                val races = (racesUiState as UiState.Success).data
                items(items = races, key = { it.id }) { race ->
                    PilotRaceCell(
                        race,
                        modifier = modifier,
                        onClick = onRaceSelected,
                        onRaceAction = {}
                    )
                }
            }
            is UiState.Error -> {
                val errorMessage = (racesUiState as UiState.Error).message
                item {
                    PlaceholderScreen(
                        modifier = modifier,
                        title = stringResource(R.string.error_title_loading_races),
                        message = errorMessage ?: "",
                        isError = true
                    )
                }
            }
            else -> {}
        }
    }
}