package com.multigp.racesync.navigation

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.multigp.racesync.LandingActivity
import com.multigp.racesync.LandingActivity.Companion.EXTRA_SESSION_ID
import com.multigp.racesync.LandingActivity.Companion.EXTRA_USER_INFO
import com.multigp.racesync.screens.onboarding.ForgotPasswordScreen
import com.multigp.racesync.screens.onboarding.LoginScreen
import com.multigp.racesync.screens.onboarding.RegistrationScreen

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun OnboardingNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Login.route,
        modifier = modifier
    ) {
        composable(route = Login.route) {
            LoginScreen(
                onLoginComplete = { sessionId, userInfo ->
                    val intent = Intent(context, LandingActivity::class.java)
                    intent.putExtra(EXTRA_SESSION_ID, sessionId)
                    intent.putExtra(EXTRA_USER_INFO, userInfo)
                    context.startActivity(intent)
                },
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