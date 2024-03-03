package com.multigp.racesync.screens.landing

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.multigp.racesync.R
import com.multigp.racesync.composables.topbars.HomeScreenTopBar
import com.multigp.racesync.composables.PermissionDeniedContent
import com.multigp.racesync.composables.PermissionsHandler
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.navigation.landingTabs
import com.multigp.racesync.ui.theme.RaceSyncTheme

@OptIn(ExperimentalPagerApi::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
    onRaceSelected: (Race) -> Unit = {}

) {
    val pagerState = rememberPagerState()
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
        }
    )
    { paddingValues ->
        HorizontalPager(
            state = pagerState,
            count = landingTabs.size,
            modifier = modifier.padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> JoinedRacesScreen(onRaceSelected = onRaceSelected)
                1 -> NearbyRacesScreen(onRaceSelected = onRaceSelected)
                2 -> ChaptersScreen(onChapterSelected = onRaceSelected)
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