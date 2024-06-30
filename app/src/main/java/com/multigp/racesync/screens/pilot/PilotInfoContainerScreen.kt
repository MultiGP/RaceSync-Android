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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.buttons.getContainerColor
import com.multigp.racesync.composables.buttons.getContentColor
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.composables.topbars.HomeScreenTabs
import com.multigp.racesync.composables.topbars.PilotInfoTopBar
import com.multigp.racesync.navigation.landingTabs
import com.multigp.racesync.navigation.pilotInfoTabs
import com.multigp.racesync.screens.landing.ChaptersScreen
import com.multigp.racesync.screens.landing.JoinedRacesScreen
import com.multigp.racesync.screens.landing.NearbyRacesScreen
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
    onGoBack: () -> Unit = {}
) {
    val pagerState = rememberPagerState()
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPilotProfile(pilotUserName)
    }

    Scaffold(
        topBar = {
            PilotInfoTopBar(
                title = "Farooq Zaman",
                countryName = "Pakistan",
                onGoBack = { multipleEventsCutter.processEvent(onGoBack) },
            )
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
                val data = (uiState as UiState.Success).data
                Column(modifier = modifier.padding(paddingValues)) {
                    Box(
                        modifier = modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            modifier = modifier.aspectRatio(1.7778f),
                            painter = rememberAsyncImagePainter(
                                model = data.profileBackgroundUrl,
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
                            url = data.profilePictureUrl
                        )
                    }
                    Row(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        IconText(
                            text = "${data.raceCount} Races",
                            icon = R.drawable.icn_race_small,
                            modifier = modifier.size(24.dp, 16.dp),
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconText(
                            text = "${data.chapterCount} Chapters",
                            icon = R.drawable.icn_chapter_small,
                            modifier = modifier.size(24.dp, 16.dp),
                            color = Color.DarkGray
                        )
                    }
                    Text(
                        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        text = data.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row(
                        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconText(
                            modifier = modifier.size(24.dp),
                            text = data.getFormattedAddress(),
                            icon = R.drawable.ic_place,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {},
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
                            0 -> PilotRacesTabView(pilotUserName, viewModel)
                            1 -> PilotChaptersTabView(pilotUserName, viewModel)
                        }
                    }
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