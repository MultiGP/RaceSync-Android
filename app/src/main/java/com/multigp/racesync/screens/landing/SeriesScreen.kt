package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.SeriesCarousel
import com.multigp.racesync.composables.cells.ChapterLoadingCell
import com.multigp.racesync.composables.cells.SeriesCell
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.SeriesFilter
import com.multigp.racesync.domain.model.filteredAndSorted
import com.multigp.racesync.viewmodels.LandingViewModel
import com.multigp.racesync.viewmodels.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = hiltViewModel(),
    onSeriesSelected: (Series) -> Unit = {}
) {
    val uiState by viewModel.seriesUiState.collectAsState()
    val refreshComplete by viewModel.refreshComplete.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    var selectedFilter by rememberSaveable { mutableStateOf(SeriesFilter.Default) }

    LaunchedEffect(Unit) { viewModel.fetchSeries() }
    LaunchedEffect(refreshComplete) { isRefreshing = false }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.invalidateSeriesCache()
            viewModel.fetchSeries()
        },
        state = pullRefreshState,
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SeriesFilterBar(
                selected = selectedFilter,
                onSelected = { selectedFilter = it }
            )

            when (val state = uiState) {
                is UiState.Loading -> LoadingList()
                is UiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = viewModel::fetchSeries
                )
                is UiState.Success -> SuccessContent(
                    series = state.data,
                    selectedFilter = selectedFilter,
                    onSeriesSelected = onSeriesSelected
                )
                is UiState.None -> Unit
            }
        }
    }
}

@Composable
private fun LoadingList(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(6) { ChapterLoadingCell() }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        modifier = modifier,
        title = stringResource(R.string.error_title_loading_series),
        message = message,
        buttonTitle = stringResource(R.string.error_btn_title_retry),
        isError = true,
        canRetry = true,
        onButtonClick = onRetry
    )
}

@Composable
private fun SuccessContent(
    series: List<Series>,
    selectedFilter: SeriesFilter,
    onSeriesSelected: (Series) -> Unit,
    modifier: Modifier = Modifier
) {
    val filtered = remember(series, selectedFilter) {
        series.filteredAndSorted(selectedFilter)
    }
    // The banner always shows Regionals regardless of the active filter.
    val banner = remember(series) {
        series.filteredAndSorted(SeriesFilter.Regionals)
    }

    if (banner.isEmpty() && filtered.isEmpty()) {
        EmptyState(selectedFilter, modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        if (banner.isNotEmpty()) {
            item(key = "carousel") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SeriesCarousel(series = banner, onSeriesSelected = onSeriesSelected)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        if (filtered.isEmpty()) {
            item(key = "empty") { EmptyState(selectedFilter) }
        } else {
            items(items = filtered, key = { it.id }) { item ->
                SeriesCell(series = item, onClick = onSeriesSelected)
            }
        }
    }
}

@Composable
private fun EmptyState(filter: SeriesFilter, modifier: Modifier = Modifier) {
    val messageRes = when (filter) {
        SeriesFilter.Joined -> R.string.placeholder_message_no_joined_series
        SeriesFilter.Regionals -> R.string.placeholder_message_no_regional_series
        SeriesFilter.All -> R.string.placeholder_message_no_series
    }
    PlaceholderScreen(
        modifier = modifier,
        title = stringResource(R.string.placeholder_title_no_series),
        message = stringResource(messageRes)
    )
}

@Composable
private fun SeriesFilterBar(
    selected: SeriesFilter,
    onSelected: (SeriesFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = SeriesFilter.entries
    val selectedIndex = entries.indexOf(selected)
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.secondary,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = MaterialTheme.colorScheme.secondary
            )
        },
        divider = {
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        },
        modifier = modifier
    ) {
        entries.forEachIndexed { index, filter ->
            val isSelected = index == selectedIndex
            Tab(
                selected = isSelected,
                onClick = { onSelected(filter) },
                selectedContentColor = MaterialTheme.colorScheme.secondary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary,
                text = {
                    Text(
                        text = filter.title,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
