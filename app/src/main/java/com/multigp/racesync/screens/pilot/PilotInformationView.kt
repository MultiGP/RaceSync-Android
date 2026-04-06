package com.multigp.racesync.screens.pilot

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.multigp.racesync.R
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.domain.model.Profile
import com.multigp.racesync.ui.theme.RaceCellDividerColor
import com.multigp.racesync.ui.theme.RaceCellSubtitleColor
import com.multigp.racesync.ui.theme.RaceCellTitleColor

@Composable
fun PilotInformationView(
    profile: Profile,
    modifier: Modifier = Modifier,
) {
    Column {
        // ── Background + Avatar ──
        // iOS banner: 1100×360 crop (~3:1 ratio), avatar 170pt with 85% overlap
        // Android: 3:1 ratio gives ~131dp on typical phone, avatar 100dp
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = modifier.aspectRatio(3f),
                painter = rememberAsyncImagePainter(
                    model = profile.profileBackgroundUrl,
                    placeholder = painterResource(id = R.drawable.pilot_profile_placeholder),
                    error = painterResource(id = R.drawable.pilot_profile_placeholder)
                ),
                contentScale = ContentScale.Crop,
                contentDescription = "Pilot profile background"
            )
            AsyncCircularImage(
                modifier = modifier.offset(y = 16.dp),
                url = profile.profilePictureUrl,
                size = 100.dp
            )
        }

        // ── Stats row (Race count + Chapter count) ──
        Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            IconText(
                text = "${profile.raceCount} Races",
                icon = R.drawable.icn_race_small,
                modifier = modifier.size(24.dp, 16.dp),
                color = RaceCellSubtitleColor
            )
            Spacer(modifier = Modifier.weight(1f))
            IconText(
                text = "${profile.chapterCount} Chapters",
                icon = R.drawable.icn_chapter_small,
                modifier = modifier.size(24.dp, 16.dp),
                color = RaceCellSubtitleColor
            )
        }

        // ── Display name (iOS: 16pt regular) ──
        Text(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            text = profile.displayName,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = RaceCellTitleColor
            )
        )

        // ── Location ──
        Row(
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_place),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = profile.getFormattedAddress(),
                maxLines = 3,
                color = MaterialTheme.colorScheme.primary,
                style = TextStyle(fontSize = 16.sp)
            )
        }

        // ── Bottom divider ──
        HorizontalDivider(
            modifier = modifier.padding(top = 4.dp),
            thickness = 0.5.dp,
            color = RaceCellDividerColor
        )
    }
}
