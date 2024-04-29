package com.multigp.racesync.composables.cells

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.image.CircularImage
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.RaceEntry
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun RosterCell(
    raceEntry: RaceEntry,
    modifier: Modifier = Modifier,
    onClick: (RaceEntry) -> Unit = {}
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
                .clickable(
                    indication = LocalIndication.current,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onClick(raceEntry) },
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncCircularImage(
                url = raceEntry.profilePictureUrl,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(48.dp)
            )

            Column(
                modifier = modifier
                    .padding(start = 12.dp)
                    .weight(1.0f)
            ) {
                Text(
                    text = raceEntry.userName ?: "—",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "${raceEntry.firstName} ${raceEntry.lastName}",
                    color = Color.Gray
                )
            }

            if(raceEntry.bandChannel.isNotEmpty()) {
                Text(
                    text = raceEntry.bandChannel,
                    color = raceEntry.channelTextColor,
                    modifier = modifier
                        .background(raceEntry.color, shape = MaterialTheme.shapes.large)
                        .padding(vertical = 2.dp, horizontal = 12.dp)
                )
            }

            IconButton(onClick = {}) {
                Image(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
    }
}