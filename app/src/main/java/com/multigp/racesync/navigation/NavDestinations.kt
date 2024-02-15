package com.multigp.racesync.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

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

object TrackDesign : NavDestination {
    override val icon = Icons.Default.Home
    override val title = "MultiGP Track Designs"
    override val route = "track"
}

object PilotNav : NavDestination {
    override val icon = null
    override val title = "Pilot"
    override val route = "pilot"
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
    override val icon = Icons.Default.Home
    override val title = "Send Feedback"
    override val route = "track"
}




val drawerMenu = listOf(Landing, TrackDesign, ObstaclesBuildGuide, RulesRegulation, VisitMultiGPShop, VisitMultiGP, SendFeedback, Logout)