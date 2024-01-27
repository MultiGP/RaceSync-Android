package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.composables.cells.ChapterCell
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun NearbyRacesScreen(modifier: Modifier = Modifier) {
    val items: List<Int> = List(10) { it + 1 }
    LazyColumn(){
        items(items){
            ChapterCell()
        }
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun NearbyRacesScreenPreview() {
    RaceSyncTheme {
        NearbyRacesScreen()
    }
}