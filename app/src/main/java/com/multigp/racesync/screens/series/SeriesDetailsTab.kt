package com.multigp.racesync.screens.series

import android.text.Spanned
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.multigp.racesync.R
import com.multigp.racesync.domain.extensions.formatDate
import com.multigp.racesync.domain.model.Series
import com.multigp.racesync.domain.model.parseHexColorOrNull
import com.multigp.racesync.ui.theme.RaceCellSubtitleColor
import com.multigp.racesync.ui.theme.RaceCellTitleColor
import com.multigp.racesync.ui.theme.SeriesPlaceholderTint

private val BannerHeight = 220.dp
private val ColorStripHeight = 8.dp
private val CountBadgeHeight = 36.dp

@Composable
fun SeriesDetailsTab(
    series: Series,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SeriesBanner(imageUrl = series.mainImageUrl, contentDescription = series.name)
        ColorStrip(hex = series.color)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SeriesHeading(series = series)
            CountsRow(series = series)
            DescriptionSection(description = series.description)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SeriesBanner(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BannerHeight)
            .background(SeriesPlaceholderTint)
    ) {
        val placeholder = painterResource(R.drawable.placeholder_series_medium)
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ColorStrip(hex: String?, modifier: Modifier = Modifier) {
    val argb = parseHexColorOrNull(hex) ?: return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ColorStripHeight)
            .background(Color(argb))
    )
}

@Composable
private fun SeriesHeading(series: Series, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = series.name,
            style = MaterialTheme.typography.headlineSmall,
            color = RaceCellTitleColor,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3
        )
        val subtitle = buildSubtitle(series)
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = RaceCellSubtitleColor
            )
        }
    }
}

@Composable
private fun buildSubtitle(series: Series): String? {
    val dateRange = formatDateRange(series) ?: return series.scoreTypeString
    val scoreType = series.scoreTypeString
    return if (scoreType.isNullOrBlank()) dateRange else "$scoreType  ·  $dateRange"
}

@Composable
private fun formatDateRange(series: Series): String? {
    val start = series.startDate?.formatDate(DATE_FORMAT)
    val end = series.endDate?.formatDate(DATE_FORMAT)
    return when {
        start != null && end != null ->
            stringResource(R.string.series_details_date_range, start, end)
        start != null -> start
        end != null -> end
        else -> null
    }
}

private const val DATE_FORMAT = "MMM d, yyyy"

@Composable
private fun CountsRow(series: Series, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val raceCount = series.raceApprovedCount.takeIf { it > 0 } ?: series.raceCount
        CountBadge(
            iconRes = R.drawable.ic_tab_nearby_outlined,
            label = stringResource(
                if (raceCount == 1) R.string.series_details_race_count
                else R.string.series_details_races_count,
                raceCount
            )
        )
        CountBadge(
            iconRes = R.drawable.ic_tab_join_outlined,
            label = stringResource(
                if (series.pilotCount == 1) R.string.series_details_pilot_count
                else R.string.series_details_pilots_count,
                series.pilotCount
            )
        )
    }
}

@Composable
private fun CountBadge(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(CountBadgeHeight / 2))
            .background(SeriesPlaceholderTint)
            .padding(horizontal = 14.dp)
            .height(CountBadgeHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = RaceCellTitleColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = RaceCellTitleColor
        )
    }
}

@Composable
private fun DescriptionSection(description: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.series_details_description_title),
            style = MaterialTheme.typography.titleMedium,
            color = RaceCellTitleColor
        )
        if (description.isNullOrBlank()) {
            Text(
                text = stringResource(R.string.series_details_no_description),
                style = MaterialTheme.typography.bodyMedium,
                color = RaceCellSubtitleColor
            )
        } else {
            HtmlText(
                html = description,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Renders legacy HTML from the API (paragraphs, line breaks, basic formatting) using the
 * platform's `HtmlCompat` parser. Kept in an AndroidView rather than converting to AnnotatedString
 * so the full range of tags the API emits (lists, links, `<br>`, etc.) renders without custom code.
 */
@Composable
private fun HtmlText(html: String, modifier: Modifier = Modifier) {
    val bodyColor = RaceCellTitleColor.toArgb()
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                setTextColor(bodyColor)
                textSize = 14f
                setLineSpacing(4f, 1f)
            }
        },
        update = { view ->
            val parsed: Spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
            view.text = parsed
        }
    )
}

private fun Color.toArgb(): Int =
    android.graphics.Color.argb(
        (alpha * 255).toInt(),
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
