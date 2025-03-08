package com.multigp.racesync.composables.topbars

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.multigp.racesync.R
import com.multigp.racesync.navigation.TabItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreenTopBar(
    tabs: List<TabItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {},
    onChapterClicked: () -> Unit = {},
    chapterImage: String?,
    onProfileClicked: () -> Unit = {},
    profileImage: String?
) {


    Surface(
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            Box(modifier = modifier.fillMaxWidth()) {
                Image(
                    modifier = modifier
                        .height(48.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.racesync_logo),
                    contentScale = ContentScale.Inside,
                    contentDescription = null,
                )
                IconButton(
                    modifier = modifier.align(Alignment.CenterStart),
                    onClick = onMenuClicked
                ) {
                    Image(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    AsyncImage(
                        model = chapterImage,
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .clickable { onChapterClicked() }
                            .padding(10.dp)
                            .size(25.dp)
                            .clip(CircleShape)
                    )
                    AsyncImage(
                        model = profileImage,
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier
                            .clickable { onProfileClicked() }
                            .padding(10.dp)
                            .size(25.dp)
                            .clip(CircleShape)
                    )
                }
            }

            HomeScreenTabs(
                tabs = tabs,
                pagerState = pagerState,
                modifier = modifier
            )
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreenTabs(
    tabs: List<TabItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.secondary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                color = MaterialTheme.colorScheme.secondary
            )
        },
        divider = {
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = {
                    if (index == pagerState.currentPage)
                        Icon(
                            modifier = modifier.size(20.dp),
                            painter = painterResource(id = tab.iconSelected),
                            contentDescription = ""
                        )
                    else
                        Icon(
                            modifier = modifier.size(20.dp),
                            painter = painterResource(id = tab.iconDefault),
                            contentDescription = ""
                        )
                },
                text = { Text(stringResource(id = tab.title)) },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.secondary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary

            )
        }
    }
}

//@OptIn(ExperimentalPagerApi::class)
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenTopBarPreview() {
//    val tabs = listOf(
//        TabItem.Joined,
//        TabItem.Nearby,
//        TabItem.Chapters
//    )
//    val pagerState = rememberPagerState()
//    RaceSyncTheme {
//        HomeScreenTopBar(tabs, pagerState = pagerState)
//    }
//}
//
//@OptIn(ExperimentalPagerApi::class)
//@Preview(showBackground = true)
//@Composable
//fun HomeScreenTabsPreview() {
//    val tabs = listOf(
//        TabItem.Joined,
//        TabItem.Nearby,
//        TabItem.Chapters
//    )
//    val pagerState = rememberPagerState()
//    HomeScreenTabs(tabs = tabs, pagerState = pagerState)
//}