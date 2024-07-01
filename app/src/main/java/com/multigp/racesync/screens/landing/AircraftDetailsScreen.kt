package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.composables.PilotBanner
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.AircraftViewModel

@Composable
fun AircraftDetailsScreen(
    aircraftId:String,
    viewModel: AircraftViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {},

){
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ) {
        item{
            RaceDetailsTopBar(
                title = aircraftId,
                onGoBack = onGoBack,
            )
            val foreground = "https://multigp-storage-new.s3.us-east-2.amazonaws.com/drone/56429/mainImage-281.png"
            val background = "https://multigp-storage-new.s3.us-east-2.amazonaws.com/drone/57222/backgroundImage-883.png"
            PilotBanner(backgroundImage = background, profileImage = foreground)
            DetailCell(title = "Aircraft Name *", content = "test")
            DetailCell(title = "Type", content = "test")
            DetailCell(title = "Size", content = "test")
            DetailCell(title = "Battery", content = "test")
            DetailCell(title = "Propeller Size", content = "test")
            DetailCell(title = "Video TX *", content = "test")
            DetailCell(title = "Antenna *", content = "test")
        }
    }
}

@Composable
fun DetailCell(
    modifier: Modifier = Modifier,
    title:String,
    content:String
){
    Column(
        modifier
            .fillMaxWidth()
            .padding(20.dp)
    ){

        Row(modifier.fillMaxWidth()) {
            Text(text = title, modifier.fillMaxWidth(0.5f))
            Text(text = content, modifier.fillMaxWidth(1f), textAlign = TextAlign.End)
        }


    }
}


@Preview(showBackground = true)
@Composable
fun CommonTopBarPreview() {
    RaceSyncTheme {
        DetailCell(title = "test", content = "hello")
    }
}