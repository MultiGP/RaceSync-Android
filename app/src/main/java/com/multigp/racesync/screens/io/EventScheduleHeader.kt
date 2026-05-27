package com.multigp.racesync.screens.io

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.domain.model.io.EventSessionFilter
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.domain.model.io.io26Dates
import com.multigp.racesync.domain.model.io.isSameDay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun EventScheduleHeader(
    dates: List<Date>,
    selectedDate: Date?,
    selectedFilter: EventSessionFilter,
    onDateSelected: (Date) -> Unit,
    onFilterSelected: (EventSessionFilter) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
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
            selectedFilter = selectedFilter,
            enabled = enabled,
            onFilterSelected = onFilterSelected,
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
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

    // Keep the selected button visible as the user (or the VM) changes the date.
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
    selectedFilter: EventSessionFilter,
    enabled: Boolean,
    onFilterSelected: (EventSessionFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(EventSessionFilter.entries.toList(), key = { it.name }) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                enabled = enabled,
                label = { Text(filter.title) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                    selectedLabelColor = MaterialTheme.colorScheme.primary,
                )
            )
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
        selectedFilter = EventSessionFilter.MySchedule,
        onDateSelected = {},
        onFilterSelected = {},
    )
}
