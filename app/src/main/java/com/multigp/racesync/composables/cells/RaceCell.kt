package com.multigp.racesync.composables.cells

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.buttons.JoinButton
import com.multigp.racesync.composables.buttons.ParticipantsButton
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.image.CircularImage
import com.multigp.racesync.domain.extensions.formatDate
import com.multigp.racesync.domain.extensions.toDate
import com.multigp.racesync.domain.model.Race


@Composable
fun RaceCell(
    race: Race,
    modifier: Modifier = Modifier,
    showDistance:Boolean = false,
    onClick: (Race) -> Unit = {},
    onRaceAction: (Race) -> Unit = {}
) {
    Column(
        modifier = modifier.clickable(
            indication = LocalIndication.current,
            interactionSource = remember { MutableInteractionSource() },
            onClick = { onClick(race) },
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (race.chapterImageFileName != null) {
                AsyncCircularImage(url = race.chapterImageFileName)
            } else {
                CircularImage(id = R.drawable.logo_powered_by)
            }
            Spacer(modifier = modifier.padding(start = 8.dp))
            Column(modifier = modifier.weight(1.0f)) {
                Text(
                    text = race.startDate?.toDate()?.formatDate() ?: "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                )
                Text(
                    text = race.name!!,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = modifier.padding(top = 4.dp, end = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = if(!showDistance) race.chapterName else race.distance ?: "—",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    modifier = modifier.padding(top = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column {
                JoinButton(race.isJoined, race.status, onClick = {onRaceAction(race)})
                ParticipantsButton(text = "" + race.participantCount, onClick = {})
            }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ChapterCellPerview() {
//    RaceSyncTheme {
//        ChapterCell()
//    }
//}