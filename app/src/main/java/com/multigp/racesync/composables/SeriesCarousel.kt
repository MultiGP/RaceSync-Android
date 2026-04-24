package com.multigp.racesync.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Series
import kotlinx.coroutines.delay

private const val AUTO_SCROLL_INTERVAL_MS = 5000L

/**
 * iOS SliderTableViewHeaderView analogue:
 *   - 70% width card, aspect-fill image, 12dp corner radius + subtle shadow
 *   - Page indicator dots under the card
 *   - Auto-advances every 5s, pauses while the user drags
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SeriesCarousel(
    series: List<Series>,
    modifier: Modifier = Modifier,
    onSeriesSelected: (Series) -> Unit = {}
) {
    if (series.isEmpty()) return

    val pagerState = rememberPagerState(initialPage = 0)
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Auto-advance every 5s, but only while the user isn't dragging.
    LaunchedEffect(pagerState, isDragged, series.size) {
        if (series.size <= 1 || isDragged) return@LaunchedEffect
        while (true) {
            delay(AUTO_SCROLL_INTERVAL_MS)
            val next = (pagerState.currentPage + 1) % series.size
            pagerState.animateScrollToPage(next)
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(
            count = series.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 30.dp),
            itemSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            SeriesCarouselItem(
                series = series[page],
                onClick = { onSeriesSelected(series[page]) }
            )
        }

        PageIndicator(
            pageCount = series.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun SeriesCarouselItem(
    series: Series,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEFEFF2))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = series.mainImageUrl,
            contentDescription = series.name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_series_medium),
            error = painterResource(R.drawable.placeholder_series_medium),
            fallback = painterResource(R.drawable.placeholder_series_medium),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    if (pageCount <= 1) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (selected) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) Color(0xFF6D6D77) else Color(0xFFCACACF)
                    )
            )
            if (index < pageCount - 1) {
                Spacer(modifier = Modifier.size(0.dp))
            }
        }
    }
}
