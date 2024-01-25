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


val drawerMenu = listOf(Landing, TrackDesign)