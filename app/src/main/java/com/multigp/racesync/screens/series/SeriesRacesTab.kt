package com.multigp.racesync.screens.series

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.composables.cells.SeriesRaceAction
import com.multigp.racesync.composables.cells.SeriesRaceCell
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.approvalState
import com.multigp.racesync.domain.model.canBeEditedBy
import com.multigp.racesync.domain.model.sortedByStartDateAscending

@Composable
fun SeriesRacesTab(
    series: Series,
    modifier: Modifier = Modifier,
    myUserId: String? = null,
    loadingRaceId: String? = null,
    onRaceSelected: (Race) -> Unit = {},
    onJoinRace: (Race) -> Unit = {},
    onApproveRace: (Race) -> Unit = {},
    onRemoveRace: (Race) -> Unit = {}
) {
    val races = remember(series) {
        series.races.orEmpty().sortedByStartDateAscending()
    }
    val canEdit = remember(series, myUserId) { series.canBeEditedBy(myUserId) }

    if (races.isEmpty()) {
        PlaceholderScreen(
            modifier = modifier,
            title = stringResource(R.string.series_races_empty_title),
            message = stringResource(R.string.series_races_empty_message)
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items = races, key = { it.id }) { race ->
            val action = if (canEdit) {
                SeriesRaceAction.Approve(state = race.approvalState())
            } else {
                SeriesRaceAction.Join
            }
            SeriesRaceCell(
                race = race,
                action = action,
                isLoading = loadingRaceId == race.id,
                onClick = onRaceSelected,
                onJoinClick = onJoinRace,
                onApproveClick = onApproveRace,
                onRemoveClick = onRemoveRace
            )
        }
    }
}
