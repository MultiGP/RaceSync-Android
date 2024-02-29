package com.multigp.racesync.screens.landing

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.multigp.racesync.domain.model.Aircraft

@Composable
fun AircraftDetailsScreen(
    aircraftId:String

){
    Text(text = aircraftId)
}