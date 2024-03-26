package com.multigp.racesync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.multigp.racesync.screens.allaircraft.AllAircraftScreen
import com.multigp.racesync.screens.landing.AircraftDetailsScreen
import com.multigp.racesync.screens.landing.ChapterDetailsScreen
import com.multigp.racesync.screens.landing.DesignGenericWebViewScreen
import com.multigp.racesync.screens.landing.DesignTrackScreen
import com.multigp.racesync.screens.landing.HomeScreen
import com.multigp.racesync.screens.landing.RaceDetailsContainerScreen
import com.multigp.racesync.screens.profile.ProfileScreen

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
                onProfileClicked = {
                    navController.navigate(ProfileDetails.route)
                },
                onRaceSelected = { race ->
                    navController.navigate("${RaceDetails.route}/${race.id}")
                }
            )
        }
        composable(route = TrackDesign.route) {
            DesignTrackScreen(
                onMenuClicked = onMenuClicked
            )
        }

        composable(route = ObstaclesBuildGuide.route) {
            DesignGenericWebViewScreen(
                onMenuClicked = onMenuClicked,
                statWebUrl = ObstaclesBuildGuide.webUrl,
                title = ObstaclesBuildGuide.title
            )
        }

        composable(route = RulesRegulation.route) {
            DesignGenericWebViewScreen(
                onMenuClicked = onMenuClicked,
                statWebUrl = RulesRegulation.webUrl,
                title = RulesRegulation.title
            )
        }

        composable(route = VisitMultiGPShop.route) {
            DesignGenericWebViewScreen(
                onMenuClicked = onMenuClicked,
                statWebUrl = VisitMultiGPShop.webUrl,
                title = VisitMultiGPShop.title
            )
        }

        composable(route = SendFeedback.route) {
            DesignGenericWebViewScreen(
                onMenuClicked = onMenuClicked,
                statWebUrl = SendFeedback.webUrl,
                title = SendFeedback.title
            )
        }

        composable(route = VisitMultiGP.route) {
            DesignGenericWebViewScreen(
                onMenuClicked = onMenuClicked,
                statWebUrl = VisitMultiGP.webUrl,
                title = VisitMultiGP.title
            )
        }

        composable(route = ProfileDetails.route) {
            ProfileScreen(
                onGoBack = {
                    navController.popBackStack()
                },
                onAircraftClick = {
                    navController.navigate(route = "allaircraft")
                })
        }

        composable(route = AllAircraft.route) { navBackStackEntry ->
            AllAircraftScreen(
                onAircraftClick = { aircraft ->
                    navController.navigate(route = "aircraft/${aircraft.id}")
                },
                onGoBack = {
                    navController.popBackStack()
                })
        }

        composable(
            route = AircraftDetails.route,
            arguments = listOf(
                navArgument(name = "aircraftId") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val aircraftId = navBackStackEntry.arguments?.getString("aircraftId")!!
            AircraftDetailsScreen(
                aircraftId = aircraftId,
                onGoBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = RaceDetails.routeWithArgs,
            arguments = RaceDetails.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(RaceDetails.raceIdArg)
                ?.let { raceId ->
                    RaceDetailsContainerScreen(raceId, onGoBack = {
                        navController.popBackStack()
                    })
                }
        }
        composable(
            route = ChapterDetails.routeWithArgs,
            arguments = ChapterDetails.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(ChapterDetails.chapterIdArg)
                ?.let { chapterId ->
                    ChapterDetailsScreen(chapterId)
                }
        }
    }
}