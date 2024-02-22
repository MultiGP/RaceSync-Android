package com.multigp.racesync.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.screens.landing.ChapterDetailsScreen
import com.multigp.racesync.screens.landing.DesignTrackScreen
import com.multigp.racesync.screens.landing.HomeScreen
import com.multigp.racesync.screens.landing.RaceDetailsScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                onRaceSelected = { race ->
                    navController.navigate("${RaceDetails.route}/${race.id}")
                },
                onChapterSelected = { chapter ->
                    navController.navigate("${ChapterDetails.route}/${chapter.id}")
                }
            )
        }
        composable(route = TrackDesign.route) {
            DesignTrackScreen(
                onMenuClicked = onMenuClicked
            )
        }
        composable(
            route = RaceDetails.routeWithArgs,
            arguments = RaceDetails.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(RaceDetails.raceIdArg)
                ?.let {raceId ->
                    RaceDetailsScreen(raceId)
                }
        }
        composable(
            route = ChapterDetails.routeWithArgs,
            arguments = ChapterDetails.arguments
        ) { navBackStackEntry ->
            navBackStackEntry.arguments?.getString(ChapterDetails.chapterIdArg)
                ?.let {chapterId ->
                    ChapterDetailsScreen(chapterId)
                }
        }
    }
}