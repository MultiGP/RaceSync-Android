package com.multigp.racesync.screens.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel : ProfileViewModel = hiltViewModel(),
){

    val profileUiState by viewModel.uiState.collectAsState()

    Row (modifier.fillMaxSize()){
        Text(text = profileUiState.simpleText)
    }

}