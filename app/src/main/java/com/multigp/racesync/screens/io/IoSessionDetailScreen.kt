package com.multigp.racesync.screens.io

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.io.EventSession
import com.multigp.racesync.domain.model.io.EventTrack
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.domain.model.io.endInstant
import com.multigp.racesync.domain.model.io.parsedDate
import com.multigp.racesync.domain.model.io.startInstant
import com.multigp.racesync.viewmodels.IoScheduleViewModel
import com.multigp.racesync.viewmodels.UiState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoSessionDetailScreen(
    sessionId: String,
    onGoBack: () -> Unit,
    viewModel: IoScheduleViewModel = hiltViewModel(),
) {
    val eventState by viewModel.eventUiState.collectAsState()
    val bucketedIds by viewModel.bucketedIds.collectAsState()
    val pendingAlertActivity by viewModel.pendingAlertActivity.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.io_session_detail_title)) },
            navigationIcon = {
                IconButton(onClick = onGoBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.io_action_back)
                    )
                }
            }
        )

        when (val state = eventState) {
            is UiState.Loading, UiState.None -> Centered { CircularProgressIndicator() }

            is UiState.Error -> Centered {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is UiState.Success -> {
                val session = remember(state, sessionId) {
                    state.data.sessions.firstOrNull { it.id == sessionId }
                }
                val track = remember(state, session) {
                    session?.let { s -> state.data.tracks.firstOrNull { it.id == s.trackId } }
                }
                if (session == null) {
                    Centered { Text(stringResource(R.string.io_session_not_found)) }
                } else {
                    DetailContent(
                        session = session,
                        track = track,
                        isAttending = session.id in bucketedIds,
                        onToggleAttending = { viewModel.toggleBucket(session) }
                    )
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
private fun DetailContent(
    session: EventSession,
    track: EventTrack?,
    isAttending: Boolean,
    onToggleAttending: () -> Unit,
) {
    val timeFmt = remember {
        SimpleDateFormat("h:mm a", Locale.US).apply {
            timeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
        }
    }
    val longDateFmt = remember {
        SimpleDateFormat("EEEE, MMM d, yyyy", Locale.US).apply {
            timeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
        }
    }
    val trackColor = ioTrackColor(session.trackId.orEmpty())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = trackColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = track?.name.orEmpty().ifEmpty { session.trackId.orEmpty() },
                style = MaterialTheme.typography.labelLarge,
                color = trackColor,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = session.activity.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow(
                    label = stringResource(R.string.io_detail_label_day),
                    value = session.parsedDate()?.let(longDateFmt::format) ?: session.dayName.orEmpty()
                )
                InfoRow(
                    label = stringResource(R.string.io_detail_label_time),
                    value = formatTimeRange(session, timeFmt)
                )
                InfoRow(
                    label = stringResource(R.string.io_detail_label_track),
                    value = track?.name.orEmpty().ifEmpty { session.trackId.orEmpty() }
                )
                durationLabel(session)?.let { dur ->
                    InfoRow(
                        label = stringResource(R.string.io_detail_label_duration),
                        value = dur
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isAttending) {
            OutlinedButton(
                onClick = onToggleAttending,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.io_detail_button_going))
            }
        } else {
            Button(
                onClick = onToggleAttending,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(imageVector = Icons.Outlined.StarBorder, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.io_detail_button_im_going))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier
                .width(80.dp)
                .padding(end = 16.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) { content() }
}

private fun formatTimeRange(session: EventSession, fmt: SimpleDateFormat): String {
    val start = session.startInstant()?.let(fmt::format) ?: "—"
    val end = session.endInstant()?.let(fmt::format) ?: "—"
    return "$start – $end"
}

private fun durationLabel(session: EventSession): String? {
    val start = session.startInstant() ?: return null
    val end = session.endInstant() ?: return null
    val totalMin = TimeUnit.MILLISECONDS.toMinutes(end.time - start.time)
    if (totalMin <= 0) return null
    val hours = totalMin / 60
    val mins = totalMin % 60
    return when {
        hours == 0L -> "${mins} min"
        mins == 0L -> "${hours}h"
        else -> "${hours}h ${mins}m"
    }
}
