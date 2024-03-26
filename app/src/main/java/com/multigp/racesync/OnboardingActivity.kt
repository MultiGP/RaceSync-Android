package com.multigp.racesync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.multigp.racesync.navigation.OnboardingNavGraph
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LoginUiState
import com.multigp.racesync.viewmodels.LoginViewModel
import com.multigp.racesync.viewmodels.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaceSyncApp()
        }
    }
}


@Composable
fun RaceSyncApp(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val loginUiState by viewModel.loginUiState.collectAsState()
    val context = LocalContext.current

    RaceSyncTheme {
        when (loginUiState) {
            is LoginUiState.Initializing -> {
                Box(
                    modifier
                        .fillMaxSize()
                        .padding(horizontal = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.racesync_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Inside
                    )
                }
            }

            is LoginUiState.Success -> {
                LandingRoot(onLogout = {
                    viewModel.logout()
                    val intent = Intent(context, OnboardingActivity::class.java)
                    context.startActivity(intent)
                })
            }

            else -> {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OnboardingNavGraph(
                        loginUiState = loginUiState,
                        loginViewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}
