package com.multigp.racesync.screens.allaircraft

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.composables.cells.AircraftCell
import com.multigp.racesync.composables.cells.AircraftLoadingCell
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.navigation.AllAircraft
import com.multigp.racesync.viewmodels.AllAircraftViewModel
import get

@Composable
fun AllAircraftScreen(
    pilotId: String,
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: AllAircraftViewModel = hiltViewModel(),
    onAircraftClick: (Aircraft) -> Unit = {}
) {
    val allAircraftUiState by viewModel.uiState.collectAsState()
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    LaunchedEffect(Unit) {
        viewModel.fetchAllAircraft(pilotId)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        RaceDetailsTopBar(
            title = AllAircraft.title,
            onGoBack = {multipleEventsCutter.processEvent(onGoBack)}
        )
        AllAircraftGrid(
            aircraftList = allAircraftUiState.allAircraft,
            onAircraftClick = onAircraftClick
        )
    }
}

@Composable
fun AllAircraftGrid(
    modifier: Modifier = Modifier,
    aircraftList: List<Aircraft>,
    onAircraftClick: (Aircraft) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(25.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp),
        content = {
            items(aircraftList) { aircraft ->
                AircraftCell(
                    aircraft = aircraft,
                    modifier = modifier,
                    onAircraftClick = onAircraftClick
                )
            }
        })
}


@Composable
fun AircraftLoadingGrid(
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(25.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalArrangement = Arrangement.spacedBy(25.dp),
        content = {
            items(2) { _ ->
                AircraftLoadingCell(
                    modifier = modifier,
                )
            }
        })
}
