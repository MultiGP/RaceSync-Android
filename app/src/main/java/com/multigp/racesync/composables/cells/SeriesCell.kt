package com.multigp.racesync.composables.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.ui.theme.RaceCellBackground
import com.multigp.racesync.ui.theme.RaceCellDividerColor
import com.multigp.racesync.ui.theme.RaceCellSubtitleColor
import com.multigp.racesync.ui.theme.RaceCellTitleColor

@Composable
fun SeriesCell(
    series: Series,
    modifier: Modifier = Modifier,
    onClick: (Series) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(RaceCellBackground)
            .clickable { onClick(series) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = series.mainImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_series_small),
                error = painterResource(R.drawable.placeholder_series_small),
                fallback = painterResource(R.drawable.placeholder_series_small),
                modifier = Modifier
                    .size(width = 110.dp, height = 76.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEFEFF2))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = series.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 20.sp
                    ),
                    color = RaceCellTitleColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Text(
                    text = stringResource(
                        if (series.pilotCount == 1) R.string.series_pilot_participating
                        else R.string.series_pilots_participating,
                        series.pilotCount
                    ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = 0.sp
                    ),
                    color = RaceCellSubtitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = RaceCellSubtitleColor,
                modifier = Modifier.size(18.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 142.dp),
            thickness = 0.5.dp,
            color = RaceCellDividerColor
        )
    }
}
