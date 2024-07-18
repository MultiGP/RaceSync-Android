package com.multigp.racesync.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.multigp.racesync.navigation.TabItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CustomTabRow(
    tabs: List<TabItem>,
    currentTab: Int = 0,
    modifier: Modifier = Modifier,
    onClickTab: (Int) -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    // OR ScrollableTabRow()
    TabRow(
        selectedTabIndex = currentTab,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.secondary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = modifier.tabIndicatorOffset(tabPositions[currentTab]),
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
                    if (index == currentTab)
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
                selected = currentTab == index,
                onClick = {
                    scope.launch {
                        onClickTab(index)
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.secondary,
                unselectedContentColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}