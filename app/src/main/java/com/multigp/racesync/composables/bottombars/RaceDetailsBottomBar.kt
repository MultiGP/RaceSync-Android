package com.multigp.racesync.composables.bottombars

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.multigp.racesync.navigation.TabItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RaceDetailsBottomBar(
    tabs: List<TabItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        tabs.forEachIndexed { index, tabItem ->
            BottomNavigationItem(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                icon = {
                    Icon(
                        modifier = modifier.size(24.dp),
                        painter = painterResource(tabItem.iconDefault),
                        contentDescription = null,
                        tint = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                },
                label = {
                    Text(
                        modifier = modifier.padding(top = 4.dp),
                        text = stringResource(id = tabItem.title),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                }
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RaceDetailsBottomBarPreview() {
//    RaceSyncTheme {
//        RaceDetailsBottomBar()
//    }
//}