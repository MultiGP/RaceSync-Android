package com.multigp.racesync.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race

interface NavDestination {
    val icon: ImageVector
    val title: String?
    val route: String
    val webUrl: String?
}

object Login : NavDestination {
    override val icon = Icons.Default.Home
    override val title = null
    override val route = "login"
    override val webUrl = null
}

object Registration : NavDestination {
    override val icon = Icons.Default.Home
    override val title = null
    override val route = "registration"
    override val webUrl = null
}

object ForgotPassword : NavDestination {
    override val icon = Icons.Default.Home
    override val title = null
    override val route = "forgot_password"
    override val webUrl = null
}

object Landing : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Home"
    override val route = "landing"
    override val webUrl = null
}

object ProfileDetails : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Pilot Profile"
    override val route = "profile"
    override val webUrl = null
}

object AllAircraft : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "My Aircraft"
    override val route = "allaircraft/{pilotId}"
    override val webUrl = null

}

object AircraftDetails : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "My Aircraft Details"
    override val route = "aircraft/{aircraftId}"
    override val webUrl = null

}

object RaceDetails : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "Race Details"
    override val route = "race_details"
    override val webUrl = null
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
    override val webUrl = null
    const val chapterIdArg = "chapter"
    val routeWithArgs = "${route}/{${chapterIdArg}}"
    val arguments = listOf(
        navArgument(chapterIdArg) { type = NavType.StringType }
    )
}

object TrackDesign : NavDestination {
    override val icon = Icons.Default.Edit
    override val title = "MultiGP Track Designs"
    override val webUrl = null
    override val route = "track"
}


object ObstaclesBuildGuide : NavDestination {
    override val icon = Icons.Default.Build
    override val title = "Obstacle Build Guide"
    override val route = "webobsguide"
    override val webUrl = "https://www.multigp.com/multigp-drone-race-course-obstacles/"
}

object RulesRegulation : NavDestination {
    override val icon = Icons.AutoMirrored.Default.List
    override val title = "Season Rules & Regulations"
    override val route = "rules-regulation"
    override val webUrl = "https://docs.google.com/document/d/1n1-TXigEqiD-yQ7-Vqb2lVP3wE4uIuuYnFGIam4vtsI/edit#heading=h.c7d32jvxr7wf"
}

object VisitMultiGPShop : NavDestination {
    override val icon = Icons.Default.ShoppingCart
    override val title = "Visit the MultiGP Shop"
    override val route = "webstore"
    override val webUrl = "https://www.multigp.com/webstore/"
}

object VisitMultiGP : NavDestination {
    override val icon = Icons.Default.Star
    override val title = "Go to MultiGP.com"
    override val route = "track"
    override val webUrl = "https://www.multigp.com/"
}

object Logout : NavDestination {
    override val icon = Icons.Default.ArrowBack
    override val title = "Logout"
    override val route = "landing"
    override val webUrl = null
}

object SendFeedback : NavDestination {
    override val icon = Icons.Default.Send
    override val title = "Send Feedback"
    override val route = "feedback"
    override val webUrl = "https://docs.google.com/forms/d/e/1FAIpQLSfY9qr-5I7JYtQ5s5UsVflMyXu-iW3-InzG03qAJOwGv9P1Tg/viewform?pli=1"
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