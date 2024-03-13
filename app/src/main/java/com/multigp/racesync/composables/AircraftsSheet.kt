package com.multigp.racesync.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.screens.allaircraft.AircraftLoadingGrid
import com.multigp.racesync.screens.allaircraft.AllAircraftGrid
import com.multigp.racesync.viewmodels.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AircraftsSheet(
    uiState: UiState<List<Aircraft>>,
    modifier: Modifier = Modifier,
    onAircraftClick: (Aircraft) -> Unit = {},
    onSheetDissmissed: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onSheetDissmissed,
        sheetState = sheetState
    ) {

        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = stringResource(R.string.aircraft_sheet_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        when (uiState) {
            is UiState.Loading -> {
                AircraftLoadingGrid(modifier = modifier)
            }

            is UiState.Success -> {
                if (uiState.data.isNotEmpty()) {
                    AllAircraftGrid(
                        aircraftList = uiState.data,
                        modifier = modifier,
                        onAircraftClick = onAircraftClick
                    )
                } else {
                    Column(
                        modifier = modifier
                            .padding(horizontal = 16.dp)
                            .height(300.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.title_empty_aircrafts),
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            modifier = modifier.padding(top = 8.dp),
                            text = stringResource(R.string.message_empty_aircrafts),
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        TextButton(modifier = modifier.padding(top = 8.dp), onClick = { }) {
                            Text(
                                text = stringResource(R.string.btn_title_add_aircraft),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(300.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.message,
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {}
        }
    }
}