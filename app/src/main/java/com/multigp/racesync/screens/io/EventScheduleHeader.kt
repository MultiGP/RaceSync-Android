package com.multigp.racesync.screens.io

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.domain.model.io.EventActivityCategory
import com.multigp.racesync.domain.model.io.EventTrack
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.domain.model.io.io26Dates
import com.multigp.racesync.domain.model.io.isSameDay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScheduleHeader(
    dates: List<Date>,
    selectedDate: Date?,
    selectedCategory: EventActivityCategory,
    tracks: List<EventTrack>,
    selectedTrackIds: Set<String>,
    onDateSelected: (Date) -> Unit,
    onCategorySelected: (EventActivityCategory) -> Unit,
    onToggleTrack: (String) -> Unit,
    onClearTracks: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var trackSheetOpen by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (dates.isNotEmpty()) {
            DateRow(
                dates = dates,
                selectedDate = selectedDate,
                enabled = enabled,
                onDateSelected = onDateSelected,
            )
        }
        FilterRow(
            selectedCategory = selectedCategory,
            trackFilterCount = selectedTrackIds.size,
            enabled = enabled,
            onCategorySelected = onCategorySelected,
            onTracksClicked = { trackSheetOpen = true },
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }

    if (trackSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = { trackSheetOpen = false },
            sheetState = sheetState,
        ) {
            TrackFilterSheet(
                tracks = tracks,
                selectedTrackIds = selectedTrackIds,
                onToggleTrack = onToggleTrack,
                onClearAll = onClearTracks,
                onDismiss = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) trackSheetOpen = false
                    }
                },
            )
        }
    }
}

@Composable
private fun DateRow(
    dates: List<Date>,
    selectedDate: Date?,
    enabled: Boolean,
    onDateSelected: (Date) -> Unit,
) {
    val zone = remember { TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID) }
    val dayFmt = remember { SimpleDateFormat("EEE", Locale.US).apply { timeZone = zone } }
    val dateFmt = remember { SimpleDateFormat("MMM d", Locale.US).apply { timeZone = zone } }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedDate, dates) {
        val idx = selectedDate?.let { sel -> dates.indexOfFirst { it.isSameDay(sel, zone) } } ?: -1
        if (idx >= 0) listState.animateScrollToItem(idx)
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(dates, key = { it.time }) { date ->
            val selected = selectedDate?.isSameDay(date, zone) == true
            DateButton(
                dayLabel = dayFmt.format(date),
                dateLabel = dateFmt.format(date),
                selected = selected,
                enabled = enabled,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateButton(
    dayLabel: String,
    dateLabel: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        selected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    Surface(
        modifier = Modifier
            .width(64.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(containerColor)
                .clickable(enabled = enabled, onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = dayLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FilterRow(
    selectedCategory: EventActivityCategory,
    trackFilterCount: Int,
    enabled: Boolean,
    onCategorySelected: (EventActivityCategory) -> Unit,
    onTracksClicked: () -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(EventActivityCategory.entries.toList(), key = { it.name }) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                enabled = enabled,
                label = { Text(category.title) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
        item(key = "__tracks__") {
            FilterChip(
                selected = trackFilterCount > 0,
                onClick = onTracksClicked,
                enabled = enabled,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
                label = {
                    Text(
                        if (trackFilterCount == 0) "Tracks"
                        else "Tracks · $trackFilterCount"
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    }
}

@Composable
private fun TrackFilterSheet(
    tracks: List<EventTrack>,
    selectedTrackIds: Set<String>,
    onToggleTrack: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Filter by track",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = onClearAll,
                enabled = selectedTrackIds.isNotEmpty(),
            ) { Text("Clear") }
            TextButton(onClick = onDismiss) { Text("Done") }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        LazyColumn {
            items(tracks, key = { it.id.orEmpty() }) { track ->
                val id = track.id.orEmpty()
                val isOn = id in selectedTrackIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = id.isNotEmpty()) { onToggleTrack(id) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(ioTrackColor(id))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = track.name.orEmpty().ifEmpty { id },
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    Checkbox(checked = isOn, onCheckedChange = { onToggleTrack(id) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventScheduleHeaderPreview() {
    val dates = io26Dates("2026-06-10", "2026-06-14")
    EventScheduleHeader(
        dates = dates,
        selectedDate = dates.getOrNull(2),
        selectedCategory = EventActivityCategory.WorldCup,
        tracks = listOf(
            EventTrack(id = "main_stage", name = "Main Stage"),
            EventTrack(id = "world_cup_1", name = "World Cup 1"),
        ),
        selectedTrackIds = setOf("main_stage"),
        onDateSelected = {},
        onCategorySelected = {},
        onToggleTrack = {},
        onClearTracks = {},
    )
}
