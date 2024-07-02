package com.multigp.racesync.screens.racedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomMap
import com.multigp.racesync.domain.model.Race

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsBottomSheet(
    race: Race,
    modifier: Modifier = Modifier,
    onSheetDissmissed: () -> Unit = {},
    onSelectMapOption: () -> Unit = {}
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp

    val bottomSheetHeightPx = with(density) { (screenHeightDp * 0.9f).toPx() }
    val bottomSheetHeightDp = with(density) { bottomSheetHeightPx.toDp() }

    ModalBottomSheet(
        onDismissRequest = onSheetDissmissed,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .height(bottomSheetHeightDp)
        ) {
            Row(modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 4.dp)) {
                IconButton(onClick = onSheetDissmissed) {
                    Icon(
                        modifier = modifier.size(32.dp),
                        painter = painterResource(id = R.drawable.icn_button_closed),
                        contentDescription = null
                    )
                }
                Spacer(modifier = modifier.weight(1.0f))
                IconButton(onClick = onSelectMapOption) {
                    Icon(
                        modifier = modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.icn_navigation),
                        contentDescription = null
                    )
                }
            }
            CustomMap(
                location = race.location,
                markerTitle = race.name ?: "Unknow Race",
                markerSnippet = race.snippet,
                modifier = Modifier.weight(1.0f),
                onMapClick = {
                    launchMap(race, context)
                }
            )
        }
    }
}