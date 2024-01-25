package com.multigp.racesync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class LandingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaceSyncTheme {
                LandingRoot()
            }
        }
    }
}

@Composable
fun LandingRoot() {
    val navController = rememberNavController()
    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    RaceSyncTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LandingScreen(
                navController = navController,
                drawerState = drawerState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RaceSyncTheme {
        LandingRoot()
    }
}