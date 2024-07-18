package com.multigp.racesync.screens.pilot

import MultipleEventsCutter
import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.multigp.racesync.composables.CustomTabRow
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.PilotChapterCell
import com.multigp.racesync.composables.cells.PilotRaceCell
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.composables.topbars.HomeScreenTabs
import com.multigp.racesync.composables.topbars.PilotInfoTopBar
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.navigation.pilotInfoTabs
import com.multigp.racesync.viewmodels.PilotViewModel
import com.multigp.racesync.viewmodels.UiState
import get

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PilotInfoContainerScreen(
    pilotUserName: String,
    modifier: Modifier = Modifier,
    viewModel: PilotViewModel = hiltViewModel(),
    onGoBack: () -> Unit = {},
    onClickAircrafts: (String) -> Unit = {},
    onRaceSelected: (Race) -> Unit = {}
) {
    var currentTab by remember { mutableStateOf(0) }
    var showQRCodeDialog by remember { mutableStateOf(false) }
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    val uiState by viewModel.uiState.collectAsState()
    val racesUiState by viewModel.racesUiState.collectAsState()
    val chaptersUiState by viewModel.chaptersUiState.collectAsState()

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
                LazyColumn(
                    modifier = modifier
                        .padding(paddingValues)
                ) {
                    item {
                        PilotInformationView(profile, modifier, onClickAircrafts)
                    }
                    stickyHeader {
                        CustomTabRow(tabs = pilotInfoTabs, currentTab = currentTab, onClickTab = {index ->
                            currentTab = index
                        })
                    }
                    if (currentTab == 0){
                        when(racesUiState){
                            is UiState.Loading -> {
                                item {
                                    ChapterLoadingCell()
                                }
                            }
                            is UiState.Success -> {
                                val races = (racesUiState as UiState.Success).data
                                items(items = races, key = { it.id }) { race ->
                                    PilotRaceCell(
                                        race,
                                        modifier = modifier,
                                        onClick = onRaceSelected,
                                        onRaceAction = {}
                                    )
                                }
                            }
                            is UiState.Error -> {
                                val errorMessage = (racesUiState as UiState.Error).message
                                item {
                                    PlaceholderScreen(
                                        modifier = modifier,
                                        title = stringResource(R.string.error_title_loading_races),
                                        message = errorMessage ?: "",
                                        isError = true
                                    )
                                }
                            }
                            else -> {}
                        }
                    }

                    if(currentTab == 1){
                        when(chaptersUiState){
                            is UiState.Loading -> {
                                item {
                                    ChapterLoadingCell()
                                }
                            }
                            is UiState.Success -> {
                                val chapters = (chaptersUiState as UiState.Success).data
                                items(items = chapters, key = { it.id }) { chapter ->
                                    PilotChapterCell(
                                        chapter,
                                        modifier = modifier,
                                        onClick = {}
                                    )
                                }
                            }
                            is UiState.Error -> {
                                val errorMessage = (chaptersUiState as UiState.Error).message
                                item {
                                    PlaceholderScreen(
                                        modifier = modifier,
                                        title = stringResource(R.string.error_title_loading_chapters),
                                        message = errorMessage ?: "",
                                        isError = true
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
                if (showQRCodeDialog) {
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