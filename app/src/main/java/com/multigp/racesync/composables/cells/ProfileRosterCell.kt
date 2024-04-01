package com.multigp.racesync.composables.cells

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.domain.model.Pilot
import com.multigp.racesync.domain.model.Profile

@Composable
fun ProfileRosterCell(
    profile: Profile,
    pilot: Pilot,
    modifier: Modifier = Modifier,
    onPilotClick: (Pilot) -> Unit = {}
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
                .clickable(
                    indication = LocalIndication.current,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onPilotClick(pilot) },
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncCircularImage(
                url = profile.profilePictureUrl,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(48.dp)
            )

            Column {
                Text(
                    text = "Band",
                    color = Color.Gray
                )
                Text(
                    text = "Race Band",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = "Channel",
                    color = Color.Gray
                )
                Text(
                    text = "5 (5806)",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = "Polarization",
                    color = Color.Gray
                )
                Text(
                    text = "Both",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
    }
}