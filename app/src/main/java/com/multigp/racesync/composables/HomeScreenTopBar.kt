package com.multigp.racesync.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.multigp.racesync.R
import com.multigp.racesync.navigation.TabItem
import com.multigp.racesync.ui.theme.RaceSyncTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreenTopBar(
    tabs: List<TabItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {}
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
                        contentDescription = null)
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
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
    ) {
        tabs.forEachIndexed { index, tab ->
            // OR Tab()
            LeadingIconTab(
                icon = {
                    if (index == pagerState.currentPage)
                        Icon(
                            painter = painterResource(id = tab.iconSelected),
                            contentDescription = ""
                        )
                    else
                        Icon(
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
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenTopBarPreview() {
    val tabs = listOf(
        TabItem.Joined,
        TabItem.Nearby,
        TabItem.Chapters
    )
    val pagerState = rememberPagerState()
    RaceSyncTheme {
        HomeScreenTopBar(tabs, pagerState = pagerState)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun HomeScreenTabsPreview() {
    val tabs = listOf(
        TabItem.Joined,
        TabItem.Nearby,
        TabItem.Chapters
    )
    val pagerState = rememberPagerState()
    HomeScreenTabs(tabs = tabs, pagerState = pagerState)
}