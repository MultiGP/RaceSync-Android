package com.multigp.racesync.screens.landing

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
import com.multigp.racesync.R
import com.multigp.racesync.composables.topbars.CommonTopBar
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun DesignTrackScreen(
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = stringResource(id = R.string.title_screen_design_tracks),
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
            Text(text = "Design Track")
        }
    }

}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun DesignTrackScreenPreview() {
    RaceSyncTheme {
        DesignTrackScreen()
    }
}