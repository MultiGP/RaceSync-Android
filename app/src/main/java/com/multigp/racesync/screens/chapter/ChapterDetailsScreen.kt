package com.multigp.racesync.screens.chapter

import MultipleEventsCutter
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomTabRow
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.PilotRaceCell
import com.multigp.racesync.composables.topbars.ChapterDetailsTopBar
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.navigation.chapterTabs
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState
import get

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChapterDetailsScreen(
    chapterId: String,
    modifier: Modifier = Modifier,
    onClickJoin: (String) -> Unit = {},
    onRaceSelected: (Race) -> Unit = {},
    onGoBack: () -> Unit = {},
    viewModel: LandingViewModel = hiltViewModel()
) {

    var currentTab by remember { mutableStateOf(0) }
    val uiState by viewModel.chapterDetailsUiState.collectAsState()
    val racesUiState by viewModel.joineChapterRacesUiState.collectAsState()
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }

    LaunchedEffect(Unit) {
        viewModel.fetchChapter(chapterId)
    }

    Scaffold(
        topBar = {
            (uiState as? UiState.Success)?.data?.let {
                ChapterDetailsTopBar(
                    title = it.name ?: "Chapter",
                    onGoBack = { multipleEventsCutter.processEvent(onGoBack) },
                )
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is UiState.Loading -> {
                ProgressHUD(
                    modifier = modifier,
                    text = R.string.progress_chapter_details
                )
            }

            is UiState.Success -> {
                val chapter = (uiState as UiState.Success).data
                LazyColumn(
                    modifier = modifier
                        .padding(paddingValues)
                ) {
                    item {
                        ChapterInformationView(chapter, modifier, onClickJoin)
                    }
                    stickyHeader {
                        CustomTabRow(tabs = chapterTabs, currentTab = currentTab, onClickTab = { index ->
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
                                        message = errorMessage,
                                        isError = true
                                    )
                                }
                            }
                            else -> {}
                        }
                    }

                    if(currentTab == 1){
                        when(uiState){
                            is UiState.Loading -> {
                                item {
                                    ChapterLoadingCell()
                                }
                            }
                            is UiState.Success -> {
//                                val chapters = (uiState as UiState.Success).data
//                                items(items = chapters, key = { chapter.id }) { chapter ->
//                                    PilotChapterCell(
//                                        chapter,
//                                        modifier = modifier,
//                                        onClick = {}
//                                    )
//                                }
                            }
                            is UiState.Error -> {
                                val errorMessage = (uiState as UiState.Error).message
                                item {
                                    PlaceholderScreen(
                                        modifier = modifier,
                                        title = stringResource(R.string.error_title_chapter_details),
                                        message = errorMessage ?: "",
                                        isError = true
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }

            is UiState.Error -> {
                val message = (uiState as UiState.Error).message
                PlaceholderScreen(
                    modifier = modifier,
                    title = stringResource(R.string.error_title_chapter_details),
                    message = message,
                    buttonTitle = stringResource(R.string.error_btn_title_retry),
                    isError = true,
                    canRetry = false
                )
            }

            else -> {}
        }
    }


//
//    when (uiState) {
//        is UiState.Success -> {
//            val chapter = (uiState as UiState.Success).data
//            Column {
//                Box(
//                    modifier = modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.BottomCenter
//                ) {
//                    Image(
//                        modifier = modifier.aspectRatio(1.7778f),
//                        painter = rememberAsyncImagePainter(
//                            model = chapter.backgroundFileName,
//                            placeholder = painterResource(id = R.drawable.pilot_profile_placeholder),
//                            error = painterResource(id = R.drawable.pilot_profile_placeholder)
//                        ),
//                        contentScale = ContentScale.Crop,
//                        contentDescription = "Pilot profile background"
//                    )
//                    AsyncCircularImage(
//                        modifier = modifier
//                            .size(120.dp)
//                            .offset(y = 30.dp),
//                        url = chapter.mainImageFileName
//                    )
//                }
//                Row(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
//                    IconText(
//                        //${profile.raceCount} Races
//                        text = "Races",
//                        icon = R.drawable.icn_race_small,
//                        modifier = modifier.size(24.dp, 16.dp),
//                        color = Color.DarkGray
//                    )
//                    Spacer(modifier = Modifier.weight(1f))
//                    IconText(
//                        //${profile.chapterCount} Chapters
//                        text = "Chapters",
//                        icon = R.drawable.icn_chapter_small,
//                        modifier = modifier.size(24.dp, 16.dp),
//                        color = Color.DarkGray
//                    )
//                }
//                androidx.compose.material3.Text(
//                    modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
//                    text = "Display Name",
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                Row(
//                    modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        modifier = modifier.size(24.dp),
//                        painter = painterResource(id = R.drawable.ic_place),
//                        tint = MaterialTheme.colorScheme.primary,
//                        contentDescription = null
//                    )
//                    androidx.compose.material3.Text(
//                        modifier = Modifier.padding(start = 8.dp).fillMaxWidth(0.6f),
//                        text = "addres",
//                        maxLines = 3,
//                        color = MaterialTheme.colorScheme.primary,
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//                    Spacer(modifier = Modifier.weight(1f))
//                    Button(
//                        onClick = {  },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0, 0, 128),
//                            contentColor = MaterialTheme.colorScheme.surface
//                        )
//                    ) {
//                        androidx.compose.material3.Text(text = "Aircrafts")
//                    }
//                }
//                HorizontalDivider(
//                    modifier = modifier.padding(top = 8.dp),
//                    thickness = 1.dp,
//                    color = MaterialTheme.colorScheme.surfaceVariant
//                )
//            }
//        }
//        else -> {}
//    }
}


@Preview
@Composable
fun ChapterDetailsScreenPreview() {
    RaceSyncTheme {
        ChapterDetailsScreen(chapterId = "2057")
    }
}