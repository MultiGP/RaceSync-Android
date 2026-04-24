package com.multigp.racesync.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.multigp.racesync.ui.theme.SeriesCarouselDotActive
import com.multigp.racesync.ui.theme.SeriesCarouselDotInactive
import com.multigp.racesync.ui.theme.SeriesPlaceholderTint
import kotlinx.coroutines.delay

private const val AUTO_SCROLL_INTERVAL_MS = 5000L
private val CarouselHeight = 200.dp
private val CarouselSidePadding = 30.dp
private val CarouselItemSpacing = 12.dp
private val CarouselCornerRadius = 12.dp
private val DotSize = 6.dp

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
            contentPadding = PaddingValues(horizontal = CarouselSidePadding),
            itemSpacing = CarouselItemSpacing,
            modifier = Modifier
                .fillMaxWidth()
                .height(CarouselHeight)
        ) { page ->
            val item = series[page]
            SeriesCarouselItem(
                series = item,
                onClick = { onSeriesSelected(item) }
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(CarouselCornerRadius),
                ambientColor = Color.Black.copy(alpha = 0.35f),
                spotColor = Color.Black.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(CarouselCornerRadius))
            .background(SeriesPlaceholderTint)
            .clickable { onClick() }
    ) {
        val placeholder = painterResource(R.drawable.placeholder_series_medium)
        AsyncImage(
            model = series.mainImageUrl,
            contentDescription = series.name,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
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
            val color = if (index == currentPage) SeriesCarouselDotActive else SeriesCarouselDotInactive
            Box(
                modifier = Modifier
                    .size(DotSize)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
