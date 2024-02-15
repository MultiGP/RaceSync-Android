package com.multigp.racesync.screens.pilot

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.multigp.racesync.R
import com.multigp.racesync.composables.CommonTopBar
import com.multigp.racesync.screens.landing.DesignTrackScreen
import com.multigp.racesync.ui.theme.RaceSyncTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onMenuClicked: () -> Unit = {}
){
    Column(modifier.fillMaxSize()){
        TopAppBar(
            actions = {
                      IconButton(onClick = { /*TODO*/ }) {
                          Icon(
                              imageVector = Icons.Default.Info,
                              contentDescription = "QR Code",
                              tint = Color.Black,
                              modifier = modifier.size(24.dp)
                          )
                          
                      }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.Black,
                            modifier = modifier.size(24.dp)
                        )

                    }


            },
            title = { Text(text = "Barracuda") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack()}) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = modifier.size(24.dp)
                    )
                    
                }
            })


    }
}

@Composable
fun TopBar(
    name: String,
    modifier: Modifier = Modifier
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =  Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ){
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.Black,
            modifier = modifier.size(24.dp)
            )
        Text(
            text = name,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

    }

}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun DesignPilotScreenPreview() {
    val nav = rememberNavController()
    RaceSyncTheme {
        PilotScreen(navController = nav )
    }
}