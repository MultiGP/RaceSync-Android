package com.multigp.racesync.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.multigp.racesync.composables.CustomDialog
import com.multigp.racesync.viewmodels.ProfileViewModel

//@Composable
//fun ProfileScreen(
//    modifier: Modifier = Modifier,
//    viewModel : ProfileViewModel = hiltViewModel(),
//){
//
//    val profileUiState by viewModel.uiState.collectAsState()
//
//    Row (modifier.fillMaxSize()){
//        Text(text = profileUiState.simpleText)
//    }
//
//}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel : ProfileViewModel = hiltViewModel(),
){
    val profileUiState by viewModel.uiState.collectAsState()
    Column(modifier.fillMaxSize()){
        TopBar(name = "Barracuda", navController = navController, viewModel = viewModel)
        PilotBanner(profileImage = profileUiState.profilePictureUrl, backgroundImage = profileUiState.profileBackgroundUrl)
        PilotInformation( chapterCount = profileUiState.chapterCount, raceCount = profileUiState.raceCount, name = profileUiState.displayName)
        PilotLocation(city = profileUiState.city)
    }

    if(viewModel.isDialogShown){
        CustomDialog(
            onDismiss = {
                viewModel.onDismissDialog()
            },
            onConfirm = {
                //viewmodel.buyItem()
            },
            pilotID = profileUiState.id
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    name: String,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ProfileViewModel
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
            IconButton(onClick = { viewModel.onShowQrCode()}) {
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
    modifier: Modifier = Modifier,
    backgroundImage: String,
    profileImage: String
){
    Box (
        modifier = modifier
            .height(150.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart

    ) {
        AsyncImage(
            model = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .background(Color.LightGray)

        )
        AsyncImage(
            model = profileImage,
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
    chapterCount: String,
    raceCount: String,
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
            Text(text = name)
        }
        Column(modifier.fillMaxWidth()) {
            Column(modifier.fillMaxWidth()) {
                Text(text = chapterCount, modifier.fillMaxWidth(), textAlign = TextAlign.End)
                Text(text = raceCount, modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
fun PilotLocation(
    city: String,

){
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
                Text(text = city, color = Color.Red)
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