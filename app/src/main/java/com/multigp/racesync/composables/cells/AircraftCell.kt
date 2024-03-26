package com.multigp.racesync.composables.cells

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.multigp.racesync.domain.model.Aircraft

@Composable
fun AircraftCell(
    aircraft: Aircraft,
    modifier: Modifier = Modifier,
    onAircraftClick: (Aircraft) -> Unit = {}
) {
    Column(
        modifier = modifier.clickable(
            indication = LocalIndication.current,
            interactionSource = remember { MutableInteractionSource() },
            onClick = { onAircraftClick(aircraft) },
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            model = aircraft.mainImageFileName,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(0.9f)
                .clip(shape = RoundedCornerShape(10.dp))
        )
        Spacer(modifier = modifier.height(8.dp))
        Text(
            text = aircraft.name ?: "\u2014",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}