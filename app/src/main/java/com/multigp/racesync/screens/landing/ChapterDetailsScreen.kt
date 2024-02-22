package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun ChapterDetailsScreen(chapterId: String, modifier: Modifier = Modifier) {

    Surface {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Chapter Details")
        }
    }
}


@Preview
@Composable
fun ChapterDetailsScreenPreview() {
    RaceSyncTheme {
        ChapterDetailsScreen(chapterId = "2057")
    }
}