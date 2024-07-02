package com.multigp.racesync.screens.racedetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.AircraftsSheet
import com.multigp.racesync.composables.CustomAlertDialog
import com.multigp.racesync.composables.CustomMap
import com.multigp.racesync.composables.JoinRaceUI
import com.multigp.racesync.composables.ResignRaceUI
import com.multigp.racesync.composables.buttons.JoinButton
import com.multigp.racesync.composables.buttons.ParticipantsButton
import com.multigp.racesync.composables.cells.RaceDetailsCell
import com.multigp.racesync.composables.text.HtmlText
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.domain.extensions.formatDate
import com.multigp.racesync.domain.extensions.toDate
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceView
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@Composable
fun RaceDetailsScreen(
    data: Triple<Profile, Race, RaceView>,
    modifier: Modifier = Modifier,
    joinRaceUiState: UiState<Boolean>,
    resignRaceUiState: UiState<Boolean>,
    viewModel: LandingViewModel = hiltViewModel()
) {
    var showAircraftSheet by remember { mutableStateOf(false) }
    var showJoinRaceConfirmationDialog by remember { mutableStateOf(false) }
    var showResignRaceDialog by remember { mutableStateOf(false) }
    var showRaceMap by remember { mutableStateOf(false) }
    var showMapOptionsSheet by remember { mutableStateOf(false) }
    var selectedRace by remember { mutableStateOf<Race?>(null) }
    var selectedAircraft by remember { mutableStateOf<Aircraft?>(null) }

    val (profile, race, raceView) = data

    val onJoinRace: (Race) -> Unit = { raceToJoin ->
        selectedRace = raceToJoin
        if (!race.isJoined) {
            showAircraftSheet = true
        } else {
            showResignRaceDialog = true
        }
    }

    RaceContentsScreen(
        race,
        modifier = modifier,
        onJoinRace = onJoinRace,
        onShowMap = { showRaceMap = true }
    )

    if (showAircraftSheet) {
        val aircraftUiState by viewModel.aircraftsUiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.fetchAircrafts()
        }
        AircraftsSheet(
            uiState = aircraftUiState,
            modifier = modifier,
            onAircraftClick = { aircraft ->
                selectedAircraft = aircraft
                showJoinRaceConfirmationDialog = true
            },
            onSheetDissmissed = { showAircraftSheet = false }
        )
    }

    if (showRaceMap) {
        MapsBottomSheet(
            race = race,
            modifier = modifier,
            onSheetDissmissed = { showRaceMap = false },
            onSelectMapOption = {
                showMapOptionsSheet = true
            }
        )
    }

    if (showMapOptionsSheet) {
        MapOptionsBottomSheet(
            race = race,
            modifier = modifier,
            onSheetDissmissed = {
                showMapOptionsSheet = false
            },
        )
    }

    if (showJoinRaceConfirmationDialog) {
        CustomAlertDialog(
            title = stringResource(R.string.alert_join_race_title),
            body = stringResource(
                R.string.alert_join_race_message,
                selectedAircraft?.name ?: ""
            ),
            confirmButtonTitle = stringResource(R.string.alert_join_race_lbl_btn_confirm),
            dismissButtonTitle = stringResource(R.string.lbl_btn_cancel),
            onConfirm = {
                viewModel.joinRace((selectedRace?.id)!!, (selectedAircraft?.id)!!)
                showJoinRaceConfirmationDialog = false
                showAircraftSheet = false
            },
            onDismiss = {
                showJoinRaceConfirmationDialog = false
                showAircraftSheet = false
            },
            onDismissRequest = {
                showJoinRaceConfirmationDialog = false
                showAircraftSheet = false
            }
        )
    }

    JoinRaceUI(
        uiState = joinRaceUiState,
        modifier = modifier,
        onProcessComplete = { viewModel.updateJoinRaceUiState(true) })

    if (showResignRaceDialog) {
        CustomAlertDialog(
            title = stringResource(R.string.alert_resign_race_title),
            body = stringResource(R.string.alert_resign_race_message),
            confirmButtonTitle = stringResource(R.string.alert_resign_race_lbl_btn_confirm),
            dismissButtonTitle = stringResource(R.string.lbl_btn_cancel),
            onConfirm = {
                viewModel.resignFromRace((selectedRace?.id)!!)
                showResignRaceDialog = false
            },
            onDismiss = {
                showResignRaceDialog = false
            },
            onDismissRequest = {
                showResignRaceDialog = false
            }
        )
    }

    ResignRaceUI(
        uiState = resignRaceUiState,
        modifier = modifier,
        onProcessComplete = { viewModel.updateResignRaceUiState(true) })
}

@Composable
fun RaceDetailsActions(race: Race, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(top = 8.dp)) {
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell("Race Class", race.raceClassString ?: "—")
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell("Coordinator", race.ownerUserName ?: "—")
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell("Chapter", race.chapterName ?: "—")
        HorizontalDivider(color = Color.LightGray)
        RaceDetailsCell("Season", race.seasonName ?: "—")
        HorizontalDivider(color = Color.LightGray)
    }
}

@Composable
fun RaceContentsScreen(
    race: Race,
    modifier: Modifier = Modifier,
    onJoinRace: (Race) -> Unit = {},
    onShowMap: () -> Unit = {},
) {
    val state = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(state)
    ) {
        CustomMap(
            location = race.location,
            markerTitle = race.name ?: "Unknow Race",
            markerSnippet = race.snippet,
            modifier = Modifier.height(280.dp),
            onMapClick = { onShowMap() }
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                if (race.officialStatus == 2) {
                    Image(
                        modifier = modifier
                            .size(28.dp)
                            .padding(end = 8.dp, top = 8.dp),
                        painter = painterResource(R.drawable.ic_tropy),
                        contentDescription = null
                    )
                }
                Text(
                    text = race.name ?: "Unknown Race",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
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
                            ?: "—",
                        icon = R.drawable.ic_race_start
                    )
                    IconText(
                        modifier = Modifier.padding(top = 12.dp),
                        text = race.endDate?.toDate()?.formatDate("EEE, MMM d\n@ h:mm a")
                            ?: "—",
                        icon = R.drawable.ic_race_stop
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    JoinButton(race.isJoined, race.status, onClick = { onJoinRace(race) })
                    ParticipantsButton(text = "" + race.participantCount, onClick = {})
                }
            }
            IconText(
                modifier = Modifier.padding(top = 16.dp),
                text = race.getFormattedAddress(),
                icon = R.drawable.ic_place,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onShowMap() }
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
            RaceDetailsActions(race)
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