package com.multigp.racesync.screens.landing

import android.Manifest
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.multigp.racesync.R
import com.multigp.racesync.composables.AircraftsSheet
import com.multigp.racesync.composables.DistanceConfigurationSheet
import com.multigp.racesync.composables.PermissionDeniedContent
import com.multigp.racesync.composables.PermissionsHandler
import com.multigp.racesync.composables.topbars.HomeScreenTopBar
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.navigation.landingTabs
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalPagerApi::class, ExperimentalPermissionsApi::class
)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onMenuClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onRaceSelected: (Race) -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAircraftSheet by remember { mutableStateOf(false) }
    val raceFeedOptions by viewModel.raceFeedOption.collectAsState()

    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)
    Scaffold(
        topBar = {
            HomeScreenTopBar(
                tabs = landingTabs,
                pagerState = pagerState,
                onMenuClicked = onMenuClicked,
                onProfileClicked = onProfileClicked
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
                    gotoNearbyRaces = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(1)
                        }
                    },
                    onJoinRace = { showAircraftSheet = true }
                )

                1 -> NearbyRacesScreen(
                    onRaceSelected = onRaceSelected,
                    onJoinRace = { showAircraftSheet = true }
                )

                2 -> ChaptersScreen(
                    onChapterSelected = onRaceSelected,
                    gotoNearbyRaces = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(1)
                        }
                    },
                    onJoinRace = { showAircraftSheet = true }
                )
            }
        }

        if (showBottomSheet) {
            DistanceConfigurationSheet(
                initialRadius = raceFeedOptions.first,
                initialUnit = raceFeedOptions.second,
                onBottomSheetDismiss = { showBottomSheet = false },
                onRadiusSelection = { radius, unit ->
                    viewModel.saveSearchRadius(radius, unit)
                }
            )
        }

        if (showAircraftSheet) {
            val aircraftUiState by viewModel.aircraftsUiState.collectAsState()
            LaunchedEffect(Unit) {
                viewModel.fetchAircrafts()
            }
            when (aircraftUiState) {
                is UiState.Success -> {
                    val aircrafts = (aircraftUiState as UiState.Success).data
                    AircraftsSheet(
                        aircrafts = aircrafts,
                        modifier = modifier,
                        onAircraftClick = {},
                        onSheetDissmissed = {showAircraftSheet = false}
                    )
                }
                else -> {}
            }
        }

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
}


@Preview(showBackground = true)
@Composable
fun LandingScreenView() {
    RaceSyncTheme {
        HomeScreen()
    }
}