package com.multigp.racesync.screens.pilot

import MultipleEventsCutter
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomDialog
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.composables.topbars.HomeScreenTabs
import com.multigp.racesync.composables.topbars.PilotInfoTopBar
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.navigation.pilotInfoTabs
import com.multigp.racesync.viewmodels.PilotViewModel
import com.multigp.racesync.viewmodels.UiState
import get

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun PilotInfoContainerScreen(
    pilotUserName: String,
    modifier: Modifier = Modifier,
    viewModel: PilotViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {},
    onClickAircrafts: (String) -> Unit = {},
    onRaceSelected: (Race) -> Unit = {}
) {
    val pagerState = rememberPagerState()
    var showQRCodeDialog by remember { mutableStateOf(false) }
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPilotProfile(pilotUserName)
    }

    Scaffold(
        topBar = {
            (uiState as? UiState.Success)?.data?.let { (profile, userInfo) ->
                PilotInfoTopBar(
                    title = profile.userName,
                    countryCode = profile.country.capitalize(Locale.current),
                    isLoggedInUser = profile.id == userInfo.id,
                    onGoBack = { multipleEventsCutter.processEvent(onGoBack) },
                    onClickShowQRCode = {
                        showQRCodeDialog = true
                    }
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is UiState.Loading -> {
                ProgressHUD(
                    modifier = modifier,
                    text = R.string.progress_race_details
                )
            }

            is UiState.Success -> {
                val (profile, userInfo) = (uiState as UiState.Success).data
                Column(modifier = modifier.padding(paddingValues)) {
                    Box(
                        modifier = modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            modifier = modifier.aspectRatio(1.7778f),
                            painter = rememberAsyncImagePainter(
                                model = profile.profileBackgroundUrl,
                                placeholder = painterResource(id = R.drawable.pilot_profile_placeholder),
                                error = painterResource(id = R.drawable.pilot_profile_placeholder)
                            ),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Pilot profile background"
                        )
                        AsyncCircularImage(
                            modifier = modifier
                                .size(120.dp)
                                .offset(y = 30.dp),
                            url = profile.profilePictureUrl
                        )
                    }
                    Row(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        IconText(
                            text = "${profile.raceCount} Races",
                            icon = R.drawable.icn_race_small,
                            modifier = modifier.size(24.dp, 16.dp),
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconText(
                            text = "${profile.chapterCount} Chapters",
                            icon = R.drawable.icn_chapter_small,
                            modifier = modifier.size(24.dp, 16.dp),
                            color = Color.DarkGray
                        )
                    }
                    Text(
                        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = profile.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row(
                        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconText(
                            modifier = modifier.size(24.dp),
                            text = profile.getFormattedAddress(),
                            icon = R.drawable.ic_place,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { onClickAircrafts(profile.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0, 0, 128),
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(text = "Aircrafts")
                        }
                    }
                    HorizontalDivider(
                        modifier = modifier.padding(top = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                    HomeScreenTabs(tabs = pilotInfoTabs, pagerState = pagerState)
                    HorizontalPager(
                        state = pagerState,
                        count = pilotInfoTabs.size,
                        itemSpacing = 16.dp
                    ) { page ->
                        when (page) {
                            0 -> PilotRacesTabView(
                                pilotUserName,
                                viewModel,
                                onRaceSelected = {}
                            )

                            1 -> PilotChaptersTabView(pilotUserName, viewModel)
                        }
                    }
                }
                if (showQRCodeDialog){
                    CustomDialog(
                        onDismiss = {
                           showQRCodeDialog = false
                        },
                        onConfirm = {
                            //viewmodel.buyItem()
                        },
                        pilotID = profile.id
                    )
                }
            }

            is UiState.Error -> {
                val message = (uiState as UiState.Error).message
                PlaceholderScreen(
                    modifier = modifier,
                    title = stringResource(R.string.error_title_race_details),
                    message = message,
                    buttonTitle = stringResource(R.string.error_btn_title_retry),
                    isError = true,
                    canRetry = false
                )
            }

            else -> {}
        }
    }
}