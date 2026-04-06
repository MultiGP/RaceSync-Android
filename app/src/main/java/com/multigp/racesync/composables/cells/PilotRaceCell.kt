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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multigp.racesync.R
import com.multigp.racesync.composables.buttons.ParticipantsButton
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.image.CircularImage
import com.multigp.racesync.domain.extensions.formatDate
import com.multigp.racesync.domain.extensions.toDate
import com.multigp.racesync.domain.model.Race
import com.multigp.racesync.ui.theme.JoinButtonGreen
import com.multigp.racesync.ui.theme.RaceCellBackground
import com.multigp.racesync.ui.theme.RaceCellDateColor
import com.multigp.racesync.ui.theme.RaceCellDividerColor
import com.multigp.racesync.ui.theme.RaceCellTitleColor


@Composable
fun PilotRaceCell(
    race: Race,
    modifier: Modifier = Modifier,
    onClick: (Race) -> Unit = {}
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
            // ── Avatar (50dp, matches iOS cellHeight avatar) ──
            if (race.chapterImageFileName != null) {
                AsyncCircularImage(url = race.chapterImageFileName)
            } else {
                CircularImage(id = R.drawable.logo_powered_by)
            }
            Spacer(modifier = Modifier.width(16.dp))

            // ── Text column ──
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = race.startDate?.toDate()?.formatDate() ?: "—",
                    style = TextStyle(
                        fontSize = 13.sp,
                        letterSpacing = 0.1.sp
                    ),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ParticipantsButton(
                        text = "${race.participantCount}",
                        onClick = {}
                    )
                    // Pilot profile only shows joined races, so every race here is joined.
                    // Closed = gray ✗, otherwise = green ✓ (matches iOS compact join indicator)
                    val isClosed = race.status.equals("Closed", ignoreCase = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(
                            id = if (isClosed) R.drawable.ic_pilot_not_joined
                            else R.drawable.ic_pilot_joined_race
                        ),
                        contentDescription = if (isClosed) "Closed" else "Joined",
                        tint = if (isClosed) RaceCellTitleColor else JoinButtonGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        // Indented divider matching RaceCell (avatar 50dp + spacer 16dp + padding 16dp = 82dp)
        HorizontalDivider(
            modifier = Modifier.padding(start = 82.dp),
            thickness = 0.5.dp,
            color = RaceCellDividerColor
        )
    }
}
