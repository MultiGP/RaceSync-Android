package com.multigp.racesync.composables.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multigp.racesync.R
import com.multigp.racesync.composables.buttons.ApproveButton
import com.multigp.racesync.composables.buttons.JoinButton
import com.multigp.racesync.composables.buttons.ParticipantsButton
import com.multigp.racesync.composables.buttons.RemoveButton
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.image.CircularImage
import com.multigp.racesync.domain.extensions.formatDate
import com.multigp.racesync.domain.extensions.toDate
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.domain.model.RaceApprovalState
import com.multigp.racesync.ui.theme.RaceCellBackground
import com.multigp.racesync.ui.theme.RaceCellDateColor
import com.multigp.racesync.ui.theme.RaceCellDividerColor
import com.multigp.racesync.ui.theme.RaceCellSubtitleColor
import com.multigp.racesync.ui.theme.RaceCellTitleColor

/**
 * Trailing-action area for a series-race row. The row layout itself stays the same
 * regardless of role — only this content swaps.
 */
sealed interface SeriesRaceAction {
    /** Standard "Join / Joined / Closed" + participant count, for non-owners. */
    data object Join : SeriesRaceAction

    /** Owner-only Approve + (when [RaceApprovalState.NotApproved]) Remove. */
    data class Approve(val state: RaceApprovalState) : SeriesRaceAction
}

/**
 * Series-races list row. Mirrors [RaceCell] visually but supports an approver-mode
 * trailing action area in addition to the normal join button.
 */
@Composable
fun SeriesRaceCell(
    race: Race,
    action: SeriesRaceAction,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: (Race) -> Unit = {},
    onJoinClick: (Race) -> Unit = {},
    onApproveClick: (Race) -> Unit = {},
    onRemoveClick: (Race) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(RaceCellBackground)
            .clickable { onClick(race) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (race.chapterImageFileName != null) {
                AsyncCircularImage(url = race.chapterImageFileName)
            } else {
                CircularImage(id = R.drawable.logo_powered_by)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = race.startDate?.toDate()?.formatDate() ?: "—",
                    style = TextStyle(fontSize = 13.sp, letterSpacing = 0.1.sp),
                    color = RaceCellDateColor
                )
                Text(
                    text = race.name ?: "",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 20.sp
                    ),
                    color = RaceCellTitleColor,
                    modifier = Modifier.padding(end = 12.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Text(
                    text = race.chapterName,
                    style = TextStyle(fontSize = 14.sp),
                    color = RaceCellSubtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ActionContent(
                race = race,
                action = action,
                isLoading = isLoading,
                onJoinClick = onJoinClick,
                onApproveClick = onApproveClick,
                onRemoveClick = onRemoveClick
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 82.dp),
            thickness = 0.5.dp,
            color = RaceCellDividerColor
        )
    }
}

@Composable
private fun ActionContent(
    race: Race,
    action: SeriesRaceAction,
    isLoading: Boolean,
    onJoinClick: (Race) -> Unit,
    onApproveClick: (Race) -> Unit,
    onRemoveClick: (Race) -> Unit
) {
    when (action) {
        SeriesRaceAction.Join -> Column(
            horizontalAlignment = Alignment.End
        ) {
            JoinButton(
                isJoined = race.isJoined,
                status = race.status,
                isLoading = isLoading,
                onClick = { onJoinClick(race) }
            )
            Spacer(modifier = Modifier.height(2.dp))
            ParticipantsButton(text = race.participantCount.toString(), onClick = {})
        }

        is SeriesRaceAction.Approve -> Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ApproveButton(
                state = action.state,
                isLoading = isLoading,
                onClick = { onApproveClick(race) }
            )
            if (action.state == RaceApprovalState.NotApproved) {
                RemoveButton(
                    isLoading = isLoading,
                    onClick = { onRemoveClick(race) }
                )
            }
        }
    }
}
