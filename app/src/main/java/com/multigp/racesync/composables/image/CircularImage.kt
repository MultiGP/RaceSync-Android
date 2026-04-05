package com.multigp.racesync.composables.image

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun CircularImage(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    contentScale: ContentScale = ContentScale.Inside,
    colorFilter: ColorFilter? = null
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
        Image(
            painter = painterResource(id),
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.White, CircleShape)
                .border(
                    width = 0.5.dp,
                    color = Color.Black.copy(alpha = 0.08f),
                    CircleShape
                )
                .clipToBounds(),
            colorFilter = colorFilter
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CircularLogoPreview(){
    RaceSyncTheme {
        CircularImage(id = R.drawable.logo_powered_by)
    }
}