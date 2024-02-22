package com.multigp.racesync.screens.landing

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.ChapterCell
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LandingViewModel

@Composable
fun ChaptersScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onChapterSelected: (Chapter) -> Unit = {}
) {
    val chapterPagingItems = viewModel.chaptersPagingData.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        viewModel.fetchChapters()
    }

    LazyColumn {
        items(
            items = chapterPagingItems,
            key = { chapter ->
                chapter.id
            }
        ) { race ->
            race?.let {
                ChapterCell(
                    it,
                    modifier = modifier,
                    onClick = onChapterSelected
                )
            }
        }

        chapterPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { ChapterLoadingCell() }
                }

                loadState.append is LoadState.Loading -> {
                    item { ChapterLoadingCell() }
                }

                loadState.refresh is LoadState.Error -> {
                    val errorMessage = (loadState.refresh as LoadState.Error).error.message
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.error_title_loading_chapters),
                            message = errorMessage ?: "",
                            buttonTitle = stringResource(R.string.error_btn_title_retry),
                            isError = true,
                            canRetry = true,
                            onButtonClick = {
                                viewModel.fetchChapters()
                            }
                        )
                    }
                }

                loadState.append is LoadState.Error -> {
                    val errorMessage = (loadState.append as LoadState.Error).error.message
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.error_title_loading_chapters),
                            message = errorMessage ?: "",
                            buttonTitle = stringResource(R.string.error_btn_title_retry),
                            isError = true,
                            canRetry = true,
                            onButtonClick = {
                                viewModel.fetchChapters()
                            }
                        )
                    }
                }

                loadState.append.endOfPaginationReached && chapterPagingItems.itemCount == 0 -> {
                    item {
                        PlaceholderScreen(
                            modifier = modifier,
                            title = stringResource(R.string.placeholder_title_no_chapters),
                            message = "",
                            canRetry = false,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 200)
@Composable
fun ChaptersScreenPreview() {
    RaceSyncTheme {
        ChaptersScreen()
    }
}