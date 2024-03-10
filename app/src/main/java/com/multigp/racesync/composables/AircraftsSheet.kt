package com.multigp.racesync.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.screens.allaircraft.AllAircraftGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AircraftsSheet(
    aircrafts: List<Aircraft>,
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
        AllAircraftGrid(
            aircraftList = aircrafts,
            modifier = modifier,
            onAircraftClick = onAircraftClick
        )
    }
}