package com.multigp.racesync.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun ForgotPasswordScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Forgot Password Screen here")
    }
}

@Preview(device = "id:Nexus One")
@Composable
fun ForgotPasswordScreenPreview() {
    RaceSyncTheme {
        ForgotPasswordScreen()
    }
}