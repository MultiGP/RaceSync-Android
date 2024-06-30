package com.multigp.racesync.screens.pilot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multigp.racesync.viewmodels.PilotViewModel

@Composable
fun PilotChaptersTabView(
    pilotUserName: String,
    viewModel: PilotViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pilot Chapters go here")
    }
}