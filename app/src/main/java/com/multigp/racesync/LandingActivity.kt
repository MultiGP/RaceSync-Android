package com.multigp.racesync

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.multigp.racesync.screens.landing.LandingScreen
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun LandingRoot(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LandingScreen(
            navController = navController,
            drawerState = drawerState,
            onLogout = onLogout
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RaceSyncTheme {
        LandingRoot()
    }
}