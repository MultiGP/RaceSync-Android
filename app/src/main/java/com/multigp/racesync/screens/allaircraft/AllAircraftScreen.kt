package com.multigp.racesync.screens.allaircraft

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.multigp.racesync.composables.topbars.RaceDetailsTopBar
import com.multigp.racesync.domain.model.Aircraft
import com.multigp.racesync.navigation.AllAircraft
import com.multigp.racesync.viewmodels.AllAircraftViewModel

@Composable
fun AllAircraftScreen(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    viewModel: AllAircraftViewModel = hiltViewModel(),
    onAircraftDetailsClick: (String) -> Unit = {}
){
    val allAircraftUiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.fetchAllAircraft()
    }

    AllAircraftContent(
        onGoBack = onGoBack,
        aircraftList = allAircraftUiState.allAircraft,
        onAircraftDetailsClick = onAircraftDetailsClick
    )
}

@Composable
fun AllAircraftContent(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    aircraftList: List<Aircraft>,
    onAircraftDetailsClick: (String) -> Unit = {}

){
    Column (
        modifier = modifier.fillMaxSize()
    ){
        RaceDetailsTopBar(
            title = AllAircraft.title,
            onGoBack = onGoBack,

        )

        LazyVerticalGrid(columns = GridCells.Fixed(2), content = {
            val size = aircraftList.size
            items(size){ index ->
                val aircraft: Aircraft = aircraftList[index]

                Column (
                    modifier = modifier
                        .height(250.dp)
                        .width(250.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    AsyncImage(

                        model = aircraft.mainImageFileName,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = modifier
                            .size(200.dp)
                            .padding(25.dp)
                            .clip(shape = RoundedCornerShape(10.dp))
                            .clickable(onClick = {onAircraftDetailsClick("test")})

                    )
                    Spacer(modifier = modifier.height(8.dp))

                    Text(text = aircraft.name ?: "\u2014")

                }


            }
        })
    }

}
