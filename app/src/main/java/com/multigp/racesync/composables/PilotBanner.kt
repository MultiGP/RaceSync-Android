package com.multigp.racesync.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PilotBanner(
    modifier: Modifier = Modifier,
    backgroundImage: String,
    profileImage: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart

    ) {
        AsyncImage(
            model = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .aspectRatio(1.9f)

        )
        AsyncImage(
            model = profileImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .padding(start = 25.dp)
                .size(95.dp)


                .border(
                    BorderStroke(3.dp, Color.White),
                    CircleShape
                )
                .clip(CircleShape)
                .shadow(1.dp)
        )
    }
}