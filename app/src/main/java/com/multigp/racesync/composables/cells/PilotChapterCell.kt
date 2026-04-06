package com.multigp.racesync.composables.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multigp.racesync.R
import com.multigp.racesync.composables.buttons.ParticipantsButton
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.composables.image.CircularImage
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.ui.theme.RaceCellBackground
import com.multigp.racesync.ui.theme.RaceCellDividerColor
import com.multigp.racesync.ui.theme.RaceCellTitleColor


@Composable
fun PilotChapterCell(
    chapter: Chapter,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(RaceCellBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Avatar (50dp) ──
            if (chapter.mainImageFileName != null) {
                AsyncCircularImage(url = chapter.mainImageFileName)
            } else {
                CircularImage(id = R.drawable.logo_powered_by)
            }
            Spacer(modifier = Modifier.width(16.dp))

            // ── Text column ──
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = chapter.name ?: "",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 20.sp
                    ),
                    color = RaceCellTitleColor,
                    modifier = Modifier.padding(end = 12.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                ParticipantsButton(
                    text = "${chapter.memberCount}",
                    onClick = {}
                )
            }
        }
        // Indented divider matching RaceCell
        HorizontalDivider(
            modifier = Modifier.padding(start = 82.dp),
            thickness = 0.5.dp,
            color = RaceCellDividerColor
        )
    }
}
