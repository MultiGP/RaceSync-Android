package com.multigp.racesync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multigp.racesync.screens.onboarding.ForgotPasswordScreen
import com.multigp.racesync.screens.onboarding.LoginScreen
import com.multigp.racesync.screens.onboarding.RegistrationScreen
import com.multigp.racesync.viewmodels.LoginUiState
import com.multigp.racesync.viewmodels.LoginViewModel

@Composable
fun OnboardingNavGraph(
    loginUiState: LoginUiState,
    loginViewModel: LoginViewModel,
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
                loginUiState = loginUiState,
                loginViewModel = loginViewModel,
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