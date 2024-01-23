package com.multigp.racesync.navigation

import androidx.compose.ui.graphics.vector.ImageVector

interface NavDestination {
    val icon: ImageVector?
    val route: String
}

object Login : NavDestination {
    override val icon = null
    override val route = "login"
}

object Registration : NavDestination {
    override val icon = null
    override val route = "registration"
}

object ForgotPassword : NavDestination {
    override val icon = null
    override val route = "forgot_password"
}