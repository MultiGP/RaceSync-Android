package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun DesignTrackScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Design Track")
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun DesignTrackScreenPreview() {
    RaceSyncTheme {
        DesignTrackScreen()
    }
}