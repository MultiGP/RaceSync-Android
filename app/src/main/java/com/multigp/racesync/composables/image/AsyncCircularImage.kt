package com.multigp.racesync.composables.image

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun AsyncCircularImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                CircleShape
            )
            .clipToBounds()
    )
}

@Preview(showBackground = true)
@Composable
fun AsyncCircularLogoPreview(){
    RaceSyncTheme {
        AsyncCircularImage(url = "https://multigp-storage-new.s3.us-east-2.amazonaws.com/chapter/543/mainImage-67.png")
    }
}