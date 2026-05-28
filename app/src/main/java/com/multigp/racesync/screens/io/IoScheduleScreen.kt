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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.PlaceholderScreen
import com.multigp.racesync.domain.model.io.Event
import com.multigp.racesync.domain.model.io.EventActivityCategory
import com.multigp.racesync.domain.model.io.EventSession
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.domain.model.io.endInstant
import com.multigp.racesync.domain.model.io.startInstant
import com.multigp.racesync.extensions.shimmerLoadingAnimation
import com.multigp.racesync.viewmodels.IoScheduleViewModel
import com.multigp.racesync.viewmodels.UiState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: IoScheduleViewModel = hiltViewModel(),
    onRaceSelected: (raceId: String) -> Unit = {},
) {
    val eventState by viewModel.eventUiState.collectAsState()
    val dates by viewModel.dates.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTrackIds by viewModel.selectedTrackIds.collectAsState()
    val sessions by viewModel.displayedSessions.collectAsState()
    val bucketedIds by viewModel.bucketedIds.collectAsState()
    val pendingAlertActivity by viewModel.pendingAlertActivity.collectAsState()
    val tracks = (eventState as? UiState.Success)?.data?.tracks.orEmpty()

    val pullState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(eventState) {
        if (eventState !is UiState.Loading) isRefreshing = false
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.load()
        },
        state = pullState,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            EventScheduleHeader(
                dates = dates,
                selectedDate = selectedDate,
                selectedCategory = selectedCategory,
                tracks = tracks,
                selectedTrackIds = selectedTrackIds,
                onDateSelected = viewModel::selectDate,
                onCategorySelected = viewModel::selectCategory,
                onToggleTrack = viewModel::toggleTrack,
                onClearTracks = viewModel::clearTracks,
                enabled = eventState is UiState.Success,
            )

            when (val state = eventState) {
                is UiState.Loading, UiState.None -> SessionListSkeleton()

                is UiState.Error -> PlaceholderScreen(
                    title = stringResource(R.string.io_error_title_loading),
                    message = state.message,
                    buttonTitle = stringResource(R.string.error_btn_title_retry),
                    isError = true,
                    canRetry = true,
                    onButtonClick = { viewModel.load() }
                )

                is UiState.Success -> {
                    val event = state.data
                    if (sessions.isEmpty()) {
                        EmptyView(category = selectedCategory, trackFilterActive = selectedTrackIds.isNotEmpty())
                    } else {
                        SessionList(
                            event = event,
                            sessions = sessions,
                            bucketedIds = bucketedIds,
                            onRaceSelected = onRaceSelected,
                            onToggleStar = viewModel::toggleBucket,
                        )
                    }
                }
            }
        }
    }

    pendingAlertActivity?.let { activity ->
        IoSchedulerAlertDialog(
            activity = activity,
            onDismiss = viewModel::dismissSchedulerAlert,
            onDontShowAgain = viewModel::acceptDontShowSchedulerAlerts,
        )
    }
}

@Composable
private fun SessionList(
    event: Event,
    sessions: List<EventSession>,
    bucketedIds: Set<String>,
    onRaceSelected: (raceId: String) -> Unit,
    onToggleStar: (EventSession) -> Unit,
) {
    val trackById = remember(event) {
        event.tracks.mapNotNull { t -> t.id?.let { it to t } }.toMap()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        items(sessions, key = { it.id }) { session ->
            SessionRow(
                session = session,
                trackName = trackById[session.trackId]?.name.orEmpty().ifEmpty { session.trackId.orEmpty() },
                isAttending = session.id in bucketedIds,
                onClick = { session.raceId?.let(onRaceSelected) },
                onToggleStar = { onToggleStar(session) },
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun SessionRow(
    session: EventSession,
    trackName: String,
    isAttending: Boolean,
    onClick: () -> Unit,
    onToggleStar: () -> Unit,
) {
    val timeFmt = remember {
        SimpleDateFormat("h:mm a", Locale.US).apply {
            timeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
        }
    }
    val trackColor = ioTrackColor(session.trackId.orEmpty())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            // Only the rows that actually link to a MultiGP race are tappable.
            .clickable(enabled = session.raceId != null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 4 dp colored rail — strong visual anchor for "all the X-track rows".
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(trackColor)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.width(60.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = session.startInstant()?.let(timeFmt::format) ?: "—",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = session.endInstant()?.let(timeFmt::format) ?: "—",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.activity.orEmpty().uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = trackColor,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trackName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = trackColor,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        IconButton(onClick = onToggleStar) {
            Icon(
                imageVector = if (isAttending) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = stringResource(
                    if (isAttending) R.string.io_star_remove else R.string.io_star_add
                ),
                tint = if (isAttending) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun SessionListSkeleton() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(8) {
            SessionRowSkeleton()
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun SessionRowSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.width(60.dp)) {
            Box(
                Modifier
                    .height(12.dp).width(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .shimmerLoadingAnimation()
            )
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .height(12.dp).width(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .shimmerLoadingAnimation()
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                Modifier
                    .height(16.dp).fillMaxWidth(0.7f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .shimmerLoadingAnimation()
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .height(12.dp).fillMaxWidth(0.4f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .shimmerLoadingAnimation()
            )
        }
        Spacer(Modifier.width(16.dp))
        Box(
            Modifier
                .size(24.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .shimmerLoadingAnimation()
        )
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
private fun EmptyView(category: EventActivityCategory, trackFilterActive: Boolean) {
    val message = when {
        category == EventActivityCategory.MySchedule ->
            stringResource(R.string.io_empty_message_my_schedule)
        category == EventActivityCategory.All && !trackFilterActive ->
            stringResource(R.string.io_empty_message_all)
        category == EventActivityCategory.All && trackFilterActive ->
            stringResource(R.string.io_empty_message_tracks_only)
        else ->
            stringResource(R.string.io_empty_message_filter, category.title)
    }
    PlaceholderScreen(
        title = stringResource(R.string.io_empty_title),
        message = message,
        canRetry = false,
    )
}

