package com.multigp.racesync.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race

interface NavDestination {
    val icon: ImageVector?
    val title: String?
    val route: String
}

object Login : NavDestination {
    override val icon = null
    override val title = null
    override val route = "login"
}

object Registration : NavDestination {
    override val icon = null
    override val title = null
    override val route = "registration"
}

object ForgotPassword : NavDestination {
    override val icon = null
    override val title = null
    override val route = "forgot_password"
}

object Landing : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Home"
    override val route = "landing"
}

object RaceDetails : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Race Details"
    override val route = "race_details"
    const val raceIdArg = "race"
    val routeWithArgs = "${route}/{${raceIdArg}}"
    val arguments = listOf(
        navArgument(raceIdArg) { type = NavType.StringType }
    )
}

object ChapterDetails : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Chapter Details"
    override val route = "chapter_details"
    const val chapterIdArg = "chapter"
    val routeWithArgs = "${route}/{${chapterIdArg}}"
    val arguments = listOf(
        navArgument(chapterIdArg) { type = NavType.StringType }
    )
}

object TrackDesign : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "MultiGP Track Designs"
    override val route = "track"
}


object ObstaclesBuildGuide : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Obstacles Build Guide"
    override val route = "track"
}

object RulesRegulation : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Season Rules & Regulations"
    override val route = "track"
}

object VisitMultiGPShop : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Visit the MultiGP Shop"
    override val route = "track"
}

object VisitMultiGP : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Go to MultiGP.com"
    override val route = "track"
}

object Logout : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Logout"
    override val route = "track"
}

object SendFeedback : NavDestination {
    override val icon = Icons.Default.Send
    override val title = "Send Feedback"
    override val route = "track"
}


val drawerMenu = listOf(
    Landing,
    TrackDesign,
    ObstaclesBuildGuide,
    RulesRegulation,
    VisitMultiGPShop,
    VisitMultiGP,
    SendFeedback,
    Logout
)