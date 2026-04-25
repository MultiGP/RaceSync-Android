package com.multigp.racesync.composables.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multigp.racesync.composables.image.AsyncCircularImage
import com.multigp.racesync.domain.extensions.countryToFlag
import com.multigp.racesync.domain.model.SeriesResult
import com.multigp.racesync.domain.model.SeriesResultLabels
import com.multigp.racesync.ui.theme.ParticipantBadgeBackground
import com.multigp.racesync.ui.theme.RaceCellBackground
import com.multigp.racesync.ui.theme.RaceCellDividerColor
import com.multigp.racesync.ui.theme.RaceCellSubtitleColor
import com.multigp.racesync.ui.theme.RaceCellTitleColor

private val RowHeight = 86.dp
private val RankWidth = 40.dp
private val AvatarSize = 50.dp

@Composable
fun SeriesResultCell(
    rank: Int,
    result: SeriesResult,
    labels: SeriesResultLabels,
    modifier: Modifier = Modifier,
    onClick: (SeriesResult) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(RaceCellBackground)
            .clickable { onClick(result) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(RowHeight)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RankBadge(rank = rank)
            AsyncCircularImage(url = result.avatarUrl, size = AvatarSize)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatTitle(result),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    color = RaceCellTitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (labels.subtitle.isNotEmpty()) {
                    Text(
                        text = labels.subtitle,
                        style = TextStyle(fontSize = 13.sp),
                        color = RaceCellSubtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (labels.hasScore) {
                ScorePill(text = labels.score)
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp + RankWidth + AvatarSize),
            thickness = 0.5.dp,
            color = RaceCellDividerColor
        )
    }
}

@Composable
private fun RankBadge(rank: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(RankWidth)
            .padding(end = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = RaceCellTitleColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScorePill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(ParticipantBadgeBackground)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = RaceCellTitleColor
        )
    }
}

private fun formatTitle(result: SeriesResult): String {
    val flag = countryToFlag(result.country)
    val name = result.resolvedDisplayName
    return if (flag.isEmpty()) name else "$flag $name"
}
