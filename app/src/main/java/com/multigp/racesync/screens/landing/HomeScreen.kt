package com.multigp.racesync.screens.landing

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomAlertDialog
import com.multigp.racesync.composables.DistanceConfigurationSheet
import com.multigp.racesync.composables.JoinRaceUI
import com.multigp.racesync.composables.PermissionDeniedContent
import com.multigp.racesync.composables.PermissionsHandler
import com.multigp.racesync.composables.ResignRaceUI
import com.multigp.racesync.composables.topbars.HomeScreenTabs
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.navigation.landingTabs
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalPermissionsApi::class
)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onRaceSelected: (Race) -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showResignRaceDialog by remember { mutableStateOf(false) }
    var selectedRace by remember { mutableStateOf<Race?>(null) }

    val joinRaceUiState by viewModel.joinRaceUiState.collectAsState()
    val resignRaceUiState by viewModel.resignRaceUiState.collectAsState()
    val gqYear by viewModel.gqYear.collectAsState()
    val raceClass by viewModel.raceClass.collectAsState()

    val onJoinRace: (Race) -> Unit = { race ->
        selectedRace = race
        if (!race.isJoined) {
            viewModel.joinRace(race.id)
        } else {
            showResignRaceDialog = true
        }
    }

    // Re-fetch data when user swipes to a tab (matches iOS viewDidAppear refresh)
    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> viewModel.fetchJoinedRaces()
            1 -> viewModel.fetchNearbyRaces()
            2 -> viewModel.fetchChapterRaces()
            3 -> viewModel.fetchGqRaces()
            4 -> viewModel.fetchRaceClassRaces()
        }
    }

    val gotoNearbyRaces: () -> Unit = {
        coroutineScope.launch {
            pagerState.scrollToPage(1)
        }
    }

    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            HomeScreenTabs(
                tabs = landingTabs,
                pagerState = pagerState,
                titleOverrides = mapOf(
                    3 to "GQ $gqYear",
                    4 to raceClass
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.fetchRaceFeedOptions()
                    showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            ) {
                Icon(
                    modifier = modifier.size(32.dp),
                    painter = painterResource(R.drawable.ic_fab_menu),
                    contentDescription = null
                )
            }
        }
    )
    { paddingValues ->
        HorizontalPager(
            state = pagerState,
            count = landingTabs.size,
            itemSpacing = 16.dp,
            modifier = modifier.padding(paddingValues),
        ) { page ->
            when (page) {
                0 -> JoinedRacesScreen(
                    onRaceSelected = onRaceSelected,
                    gotoNearbyRaces = gotoNearbyRaces,
                    onJoinRace = onJoinRace
                )

                1 -> NearbyRacesScreen(
                    onRaceSelected = onRaceSelected,
                    onJoinRace = onJoinRace
                )

                2 -> ChaptersScreen(
                    onChapterSelected = onRaceSelected,
                    gotoNearbyRaces = gotoNearbyRaces,
                    onJoinRace = onJoinRace
                )

                3 -> GqRacesScreen(
                    onRaceSelected = onRaceSelected,
                    onJoinRace = onJoinRace
                )

                4 -> RaceClassScreen(
                    onRaceSelected = onRaceSelected,
                    onJoinRace = onJoinRace
                )
            }
        }

        if (showBottomSheet) {
            val raceFeedOptions by viewModel.raceFeedOption.collectAsState()
            DistanceConfigurationSheet(
                initialRadius = raceFeedOptions.first,
                initialUnit = raceFeedOptions.second,
                initialGqYear = gqYear,
                initialRaceClass = raceClass,
                onBottomSheetDismiss = { showBottomSheet = false },
                onRadiusSelection = { radius, unit ->
                    viewModel.saveSearchRadius(radius, unit)
                },
                onGqYearSelected = { year ->
                    viewModel.saveGqYear(year)
                },
                onRaceClassSelected = { selectedClass ->
                    viewModel.saveRaceClass(selectedClass)
                }
            )
        }

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

    if (!permissionState.allPermissionsGranted) {
        Box(modifier = modifier.fillMaxSize()) {
            PermissionsHandler(
                permissionState = permissionState,
                deniedContent = { shouldShowRationale ->
                    PermissionDeniedContent(
                        alertTitle = R.string.permissions_location_title,
                        requestMessage = R.string.permissions_location_request,
                        rationaleMessage = R.string.permissions_location_rationale,
                        shouldShowRationale = shouldShowRationale
                    ) {
                        permissionState.launchMultiplePermissionRequest()
                    }
                },
                grantedContent = {
                    Box(modifier = modifier)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenView() {
    RaceSyncTheme {
        HomeScreen()
    }
}