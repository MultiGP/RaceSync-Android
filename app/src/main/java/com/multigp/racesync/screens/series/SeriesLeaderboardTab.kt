package com.multigp.racesync.screens.series

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.SeriesResultCell
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.SeriesResult
import com.multigp.racesync.domain.model.SeriesResultLabels
import com.multigp.racesync.domain.model.SeriesScoreType
import com.multigp.racesync.domain.model.seriesResultLabels

private enum class ResultMode { Pilots, Chapters }

@Composable
fun SeriesLeaderboardTab(
    series: Series,
    modifier: Modifier = Modifier,
    onPilotSelected: (SeriesResult) -> Unit = {},
    onChapterSelected: (SeriesResult) -> Unit = {}
) {
    val pilotResults = series.pilotResults.orEmpty()
    val chapterResults = series.chapterResults.orEmpty()

    if (pilotResults.isEmpty() && chapterResults.isEmpty()) {
        PlaceholderScreen(
            modifier = modifier,
            title = stringResource(R.string.series_leaderboard_empty_title),
            message = stringResource(R.string.series_leaderboard_empty_message)
        )
        return
    }

    // Toggle is only shown when both lists have content.
    val showToggle = pilotResults.isNotEmpty() && chapterResults.isNotEmpty()
    var mode by rememberSaveable {
        val initial = if (pilotResults.isNotEmpty()) ResultMode.Pilots else ResultMode.Chapters
        mutableStateOf(initial)
    }

    val scoreType = remember(series.scoreType) { SeriesScoreType.fromRaw(series.scoreType) }
    val rows: List<SeriesResult> = when (mode) {
        ResultMode.Pilots -> pilotResults
        ResultMode.Chapters -> chapterResults
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (showToggle) {
            ResultModeBar(
                selected = mode,
                onSelected = { mode = it }
            )
        }
        LeaderboardList(
            rows = rows,
            scoreType = scoreType,
            onClick = { result ->
                when (result.type) {
                    com.multigp.racesync.domain.model.SeriesResultType.Pilot -> onPilotSelected(result)
                    com.multigp.racesync.domain.model.SeriesResultType.Chapter -> onChapterSelected(result)
                }
            }
        )
    }
}

@Composable
private fun LeaderboardList(
    rows: List<SeriesResult>,
    scoreType: SeriesScoreType,
    onClick: (SeriesResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val labels = remember(rows, scoreType) {
        rows.map { seriesResultLabels(it, scoreType) }
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(items = rows, key = { _, item -> resultKey(item) }) { index, result ->
            SeriesResultCell(
                rank = index + 1,
                result = result,
                labels = labels[index],
                onClick = onClick
            )
        }
    }
}

private fun resultKey(result: SeriesResult): String =
    result.pilotId ?: result.chapterId ?: result.resolvedDisplayName

@Composable
private fun ResultModeBar(
    selected: ResultMode,
    onSelected: (ResultMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val entries = ResultMode.entries
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
        modifier = modifier.fillMaxWidth()
    ) {
        entries.forEachIndexed { index, mode ->
            val isSelected = index == selectedIndex
            Tab(
                selected = isSelected,
                onClick = { onSelected(mode) },
                selectedContentColor = MaterialTheme.colorScheme.secondary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary,
                text = {
                    Text(
                        text = stringResource(
                            when (mode) {
                                ResultMode.Pilots -> R.string.series_leaderboard_filter_pilots
                                ResultMode.Chapters -> R.string.series_leaderboard_filter_chapters
                            }
                        ),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}
