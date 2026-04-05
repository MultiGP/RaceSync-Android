package com.multigp.racesync.composables.image

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun AsyncCircularImage(
    url: String?,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 2.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.25f)
            )
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.White, CircleShape)
                .border(
                    width = 0.5.dp,
                    color = Color.Black.copy(alpha = 0.08f),
                    CircleShape
                )
                .clipToBounds()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AsyncCircularLogoPreview(){
    RaceSyncTheme {
        AsyncCircularImage(url = "https://multigp-storage-new.s3.us-east-2.amazonaws.com/chapter/543/mainImage-67.png")
    }
}