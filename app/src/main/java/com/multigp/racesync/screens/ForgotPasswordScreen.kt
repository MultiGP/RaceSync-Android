package com.multigp.racesync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multigp.racesync.composables.OnboardingTopBar
import com.multigp.racesync.composables.RSWebView


@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {}
) {
    OnboardingTopBar(
        title = "Recover Password",
        modifier = modifier,
        onClickLeftIcon = onClickBack
    ) {
        RSWebView(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = it),
            url = "https://www.multigp.com/initiatepasswordreset/"
        )
    }
}

