package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomMap
import com.multigp.racesync.composables.buttons.JoinButton
import com.multigp.racesync.composables.buttons.ParticipantsButton
import com.multigp.racesync.composables.text.HtmlText
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.extensions.formatDate
import com.multigp.racesync.extensions.toDate
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun RaceDetailsScreen(
    raceId: String,
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {}
) {
    val uiState by viewModel.raceDetailsUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRace(raceId)
    }

    Scaffold(
        topBar = {
            RaceDetailsTopBar(
                title = stringResource(id = R.string.title_race_details),
                onGoBack = onGoBack,
            )
        }
    )
    { paddingValues ->
        when (uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                )
            }

            is UiState.Success -> {
                val race = (uiState as UiState.Success).data
                RaceContentsScreen(
                    race,
                    modifier = modifier.padding(paddingValues = paddingValues)
                )
            }

            is UiState.Error -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues = paddingValues)
                )
            }
        }
    }
}

@Composable
fun RaceContentsScreen(
    race: Race,
    modifier: Modifier = Modifier
) {
    Surface {
        Column {
            CustomMap(
                location = race.location,
                markerTitle = race.name ?: "Unknow Race",
                markerSnippet = "Snippet",
                modifier = Modifier.height(280.dp)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = race.name ?: "Unknown Race",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = race.raceClassString ?: "Unknown class",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1.0f)) {
                        IconText(
                            modifier = Modifier.padding(top = 12.dp),
                            text = race.startDate?.toDate()?.formatDate("EEE, MMM d\n@ h:mm a")
                                ?: "--",
                            icon = R.drawable.ic_race_start
                        )
                        IconText(
                            modifier = Modifier.padding(top = 12.dp),
                            text = race.endDate?.toDate()?.formatDate("EEE, MMM d\n@ h:mm a")
                                ?: "--",
                            icon = R.drawable.ic_race_stop
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        JoinButton(onClick = {})
                        ParticipantsButton(text = "" + race.participantCount, onClick = {})
                    }
                }
                IconText(
                    modifier = Modifier.padding(top = 16.dp),
                    text = race.getFormattedAddress(),
                    icon = R.drawable.ic_place,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    text = race.chapterName.uppercase(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                HtmlText(
                    modifier = Modifier.padding(top = 8.dp),
                    html = race.content ?: ""
                )
            }
        }
    }
}

@Preview
@Composable
fun RaceDetailsScreenPreview() {
    RaceSyncTheme {
        RaceContentsScreen(race = Race.testObject)
    }
}