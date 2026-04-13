package com.multigp.racesync.screens.landing

import android.content.Context
import android.location.LocationManager
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    val refreshComplete by viewModel.refreshComplete.collectAsState()
    val loadingRaceId by viewModel.loadingRaceId.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    // Initial load
    LaunchedEffect(Unit) {
        viewModel.fetchNearbyRaces()
    }

    // Stop refresh spinner when fetch completes
    LaunchedEffect(refreshComplete) {
        isRefreshing = false
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

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            isLocationEnabled = isLocationServiceEnabled(context, locationManager)
            viewModel.fetchNearbyRaces()
        },
        state = pullRefreshState,
        modifier = modifier
    ) {
        when (uiState) {
            is UiState.Loading -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { ChapterLoadingCell() }
                }
            }

            is UiState.Success -> {
                val races = (uiState as UiState.Success<List<Race>>).data
                if (races.isEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            PlaceholderScreen(
                                title = stringResource(R.string.placeholder_title_no_races),
                                message = stringResource(R.string.placeholder_message_no_nearby_races),
                                canRetry = false
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(items = races, key = { it.id }) { race ->
                            RaceCell(
                                race,
                                modifier = Modifier,
                                showDistance = true,
                                isLoading = loadingRaceId == race.id,
                                onClick = onRaceSelected,
                                onRaceAction = onJoinRace
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                val errorMessage = (uiState as UiState.Error).message
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        PlaceholderScreen(
                            title = stringResource(R.string.error_title_loading_races),
                            message = errorMessage,
                            buttonTitle = stringResource(R.string.error_btn_title_retry),
                            isError = true,
                            canRetry = true,
                            onButtonClick = { viewModel.fetchNearbyRaces() }
                        )
                    }
                }
            }

            is UiState.None -> { /* Initial state — nothing to show yet */ }
        }

    }
}

private fun isLocationServiceEnabled(context: Context, locationManager: LocationManager): Boolean {
    val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return gpsEnabled || networkEnabled
}
