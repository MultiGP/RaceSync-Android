package com.multigp.racesync.screens.landing

import android.content.Context
import android.location.LocationManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.RaceCell
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyRacesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onRaceSelected: (Race) -> Unit = {},
    onJoinRace: (Race) -> Unit = {}
) {
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled by remember { mutableStateOf(isLocationServiceEnabled(context, locationManager)) }
    val uiState by viewModel.nearbyRacesUiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    // Initial load
    LaunchedEffect(Unit) {
        viewModel.fetchNearbyRaces()
    }

    // Handle pull-to-refresh
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            isLocationEnabled = isLocationServiceEnabled(context, locationManager)
            viewModel.fetchNearbyRaces()
        }
    }

    // Stop refresh spinner when data arrives
    LaunchedEffect(uiState) {
        if (uiState !is UiState.Loading) {
            pullRefreshState.endRefresh()
        }
    }

    if (!isLocationEnabled) {
        PlaceholderScreen(
            modifier = modifier,
            title = stringResource(R.string.placeholder_title_loaction_service_off),
            message = stringResource(R.string.placeholder_message_loaction_service_off),
            buttonTitle = stringResource(R.string.error_btn_title_retry),
            isError = true,
            canRetry = true,
            onButtonClick = {
                isLocationEnabled = isLocationServiceEnabled(context, locationManager)
                if (isLocationEnabled) viewModel.fetchNearbyRaces()
            }
        )
        return
    }

    Box(modifier = modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
        when (uiState) {
            is UiState.Loading -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { ChapterLoadingCell() }
                }
            }

            is UiState.Success -> {
                val races = (uiState as UiState.Success<List<Race>>).data
                if (races.isEmpty()) {
                    PlaceholderScreen(
                        title = stringResource(R.string.placeholder_title_no_races),
                        message = stringResource(R.string.placeholder_message_no_nearby_races),
                        canRetry = false
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = races, key = { it.id }) { race ->
                            RaceCell(
                                race,
                                modifier = Modifier,
                                showDistance = true,
                                onClick = onRaceSelected,
                                onRaceAction = onJoinRace
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                val errorMessage = (uiState as UiState.Error).message
                PlaceholderScreen(
                    title = stringResource(R.string.error_title_loading_races),
                    message = errorMessage,
                    buttonTitle = stringResource(R.string.error_btn_title_retry),
                    isError = true,
                    canRetry = true,
                    onButtonClick = { viewModel.fetchNearbyRaces() }
                )
            }

            is UiState.None -> { /* Initial state — nothing to show yet */ }
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

private fun isLocationServiceEnabled(context: Context, locationManager: LocationManager): Boolean {
    val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return gpsEnabled || networkEnabled
}
