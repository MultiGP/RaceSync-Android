package com.multigp.racesync.screens.chapter

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.multigp.racesync.R
import com.multigp.racesync.composables.buttons.JoinButton
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.domain.model.Chapter

@Composable
fun ChapterInformationView(
    chapter: Chapter,
    modifier: Modifier = Modifier,
    onClickJoin: (String) -> Unit = {},
) {
    Column {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                modifier = modifier.aspectRatio(1.7778f),
                painter = rememberAsyncImagePainter(
                    model = chapter.backgroundFileName,
                    placeholder = painterResource(id = R.drawable.chapter_profile_placeholder),
                    error = painterResource(id = R.drawable.chapter_profile_placeholder)
                ),
                contentScale = ContentScale.Crop,
                contentDescription = "Chapter profile background"
            )
            AsyncCircularImage(
                modifier = modifier
                    .size(120.dp)
                    .offset(y = 30.dp),
                url = chapter.mainImageFileName
            )
        }
        Row(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            IconText(
                text = "${chapter.raceCount} Races",
                icon = R.drawable.icn_race_small,
                modifier = modifier.size(24.dp, 16.dp),
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.weight(1f))
            IconText(
                text = "${chapter.memberCount} Members",
                icon = R.drawable.icn_chapter_small,
                modifier = modifier.size(24.dp, 16.dp),
                color = Color.DarkGray
            )
        }
        Text(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            text = chapter.description ?: "description",
            style = MaterialTheme.typography.bodyLarge
        )
        Row(
            modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_place),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 8.dp).fillMaxWidth(0.6f),
                text = chapter.getFormattedAddress(),
                maxLines = 3,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            JoinButton(chapter.isJoined)
        }
        HorizontalDivider(
            modifier = modifier.padding(top = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}