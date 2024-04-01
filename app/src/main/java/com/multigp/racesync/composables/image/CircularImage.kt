package com.multigp.racesync.composables.image

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun CircularImage(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Inside,
    colorFilter: ColorFilter? = null
) {
    Image(
        painter = painterResource(id),
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                CircleShape
            )
            .clipToBounds(),
        colorFilter = colorFilter
    )
}

@Preview(showBackground = true)
@Composable
fun CircularLogoPreview(){
    RaceSyncTheme {
        CircularImage(id = R.drawable.logo_powered_by)
    }
}