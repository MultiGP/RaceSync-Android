package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.multigp.racesync.composables.topbars.CommonTopBar

@Composable
fun DesignGenericWebViewScreen(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {},
    statWebUrl: String,
    title: String
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = title,
                onMenuClicked = onMenuClicked,
            )
        }
    )
    { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            val state = rememberWebViewState(statWebUrl)
            WebView(
                state = state,
                onCreated = { it.settings.javaScriptEnabled = true }
            )
        }
    }

}