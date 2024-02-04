package com.multigp.racesync

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.multigp.racesync.navigation.OnboardingNavGraph
import com.multigp.racesync.ui.theme.RaceSyncTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaceSyncApp()
        }
    }
}


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun RaceSyncApp() {
    val navController = rememberNavController()
    RaceSyncTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            OnboardingNavGraph(navController = navController)
        }
    }
}
