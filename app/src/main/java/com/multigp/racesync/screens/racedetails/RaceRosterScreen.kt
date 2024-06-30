package com.multigp.racesync.screens.racedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ProfileRosterCell
import com.multigp.racesync.composables.cells.RosterCell
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceEntry
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun RaceRosterScreen(
    data: Triple<Profile, Race, RaceView>,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onPilotSelected: (String) -> Unit = {}
) {
    val (profile, race, raceView) = data
    if (raceView.entries.isNotEmpty()) {
        RosterScreenContens(
            profile = profile,
            entries = raceView.entries.sortedBy { it.userName },
            modifier = modifier,
            onPilotSelected = onPilotSelected)
    } else {
        PlaceholderScreen(
            title = stringResource(R.string.placeholder_title_no_pilots),
            message = stringResource(R.string.placeholder_message_no_pilots)
        )
    }

}

@Composable
fun RosterScreenContens(
    profile: Profile,
    entries: List<RaceEntry>,
    modifier: Modifier = Modifier,
    onPilotSelected: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        entries.firstOrNull() { it.pilotId == profile.id }?.let {
            item {
                ProfileRosterCell(
                    profile = profile,
                    raceEntry = entries.first { it.pilotId == profile.id })
            }
        }
        item {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 8.dp),
                text = "REGISTERED PILOTS",
                textAlign = TextAlign.Start,
                color = Color.Gray,
                lineHeight = 48.sp,
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(entries.filter { it.pilotId != profile.id }) { entry ->
            RosterCell(raceEntry = entry, onClick = {onPilotSelected(entry.userName!!) }, modifier = modifier)
        }
    }
}