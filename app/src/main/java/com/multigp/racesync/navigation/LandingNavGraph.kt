package com.multigp.racesync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multigp.racesync.screens.landing.DesignTrackScreen
import com.multigp.racesync.screens.landing.HomeScreen
import com.multigp.racesync.screens.pilot.PilotScreen

@Composable
fun LandingNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Landing.route,
        modifier = modifier
    ) {
        composable(route = Landing.route) {
            HomeScreen(
                onMenuClicked = onMenuClicked,
                navController = navController
            )
        }
        composable(route = TrackDesign.route) {
            DesignTrackScreen(
                onMenuClicked = onMenuClicked
            )
        }

        composable(route = PilotNav.route) {
            PilotScreen(
                navController = navController
            )
        }
    }
}