package com.multigp.racesync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multigp.racesync.screens.ForgotPasswordScreen
import com.multigp.racesync.screens.LoginScreen
import com.multigp.racesync.screens.RegistrationScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Login.route,
        modifier = modifier
    ) {
        composable(route = Login.route) {
            LoginScreen(
                onClickRecoverPassword = {
                    navController.navigateSingleTopTo(ForgotPassword.route)
                },
                onClickRegisterAccount = {
                    navController.navigateSingleTopTo(Registration.route)
                }
            )
        }
        composable(route = Registration.route) {
            RegistrationScreen(
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = ForgotPassword.route) {
            ForgotPasswordScreen(
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) {
    this.navigate(route = route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}