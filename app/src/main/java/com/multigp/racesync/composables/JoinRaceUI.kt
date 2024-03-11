package com.multigp.racesync.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.viewmodels.UiState

@Composable
fun JoinRaceUI(
    uiState: UiState<Boolean>,
    modifier: Modifier = Modifier,
    onProcessComplete: (Boolean) -> Unit = {}
){
    when (uiState) {
        is UiState.Loading -> {
            ProgressHUD(
                modifier = modifier,
                text = R.string.progress_join_race
            )
        }
        is UiState.Success -> {
            onProcessComplete(true)
        }
        is UiState.Error -> {
            val errorMessage = uiState.message
            CustomAlertDialog(
                title = "Error",
                body = errorMessage,
                confirmButtonTitle = "OK",
                onConfirm = { onProcessComplete(true) }
            )
        }
        else -> {}
    }
}