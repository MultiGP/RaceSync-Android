package com.multigp.racesync.screens.pilot

import android.content.res.Resources.Theme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.multigp.racesync.R
import com.multigp.racesync.composables.CommonTopBar
import com.multigp.racesync.screens.landing.DesignTrackScreen
import com.multigp.racesync.ui.theme.RaceSyncTheme
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun PilotScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    Column(modifier.fillMaxSize()){
        TopBar(name = "Barracuda", navController = navController)
        PilotBanner()
        PilotInformation( chapterCount = 8, raceCount = 4, name = "Viki (Barracuda) Baarathi")
        PilotLocation()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    name: String,
    modifier: Modifier = Modifier,
    navController: NavHostController,
){
    TopAppBar(
        title = {
            Text(text = name)
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack()}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = modifier.size(24.dp)
                )
            }
        },
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
        }

    )

}

@Composable
fun PilotBanner(
    modifier: Modifier = Modifier
){
    Box (
        modifier = modifier
            .height(150.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart

    ) {
        AsyncImage(
            model = "https://multigp-storage-new.s3.us-east-2.amazonaws.com/user/35533/backgroundImage-204.png",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .background(Color.LightGray)

        )
        AsyncImage(
            model = "https://multigp-storage-new.s3.us-east-2.amazonaws.com/user/35533/profileImage-37.png",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .padding(start = 25.dp)
                .size(95.dp)


                .border(
                    BorderStroke(3.dp, Color.White),
                    CircleShape
                )
                .clip(CircleShape)
                .shadow(1.dp)
        )
    }
}

@Composable
fun PilotInformation(
    chapterCount: Int,
    raceCount: Int,
    name: String,
    modifier: Modifier = Modifier

){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ){
        Column {
            Text(text = "Viki (Barracuda) Baarathi")
        }
        Column(modifier.fillMaxWidth()) {
            Column(modifier.fillMaxWidth()) {
                Text(text = "2 Chapters", modifier.fillMaxWidth(), textAlign = TextAlign.End)
                Text(text = "8 Races", modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
fun PilotLocation(){
    Row(modifier = Modifier
        .fillMaxWidth()
    ){
        Column (
            modifier = Modifier

                .padding(end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Back",
                    tint = Color.Red

                    )
                Text(text = "Puchong, Selangor", color = Color.Red)
            }
        }

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
            
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "My Aircrafts")
            }
        }
    }


}


@Preview(showBackground = true, heightDp = 500)
@Composable
fun DesignPilotScreenPreview() {
    val nav = rememberNavController()
    RaceSyncTheme {
        PilotScreen(navController = nav )
    }
}