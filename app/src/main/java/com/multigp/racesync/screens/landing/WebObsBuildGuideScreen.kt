package com.multigp.racesync.screens.landing

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import com.multigp.racesync.R
import com.multigp.racesync.composables.topbars.CommonTopBar
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun DesignWebObsBuildGuideScreen(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = stringResource(id = R.string.title_screen_obs_build_guide),
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
            val state = rememberWebViewState("https://www.multigp.com/multigp-drone-race-course-obstacles/")
            WebView(
                state = state,
                onCreated = { it.settings.javaScriptEnabled = true }
            )
        }
    }

}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun DesignWebObsBuildGuideScreenPreview() {
    RaceSyncTheme {
        DesignWebObsBuildGuideScreen()
    }
}