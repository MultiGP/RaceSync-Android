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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun RaceDetailsBottomBar(
    modifier: Modifier = Modifier,
    onClickDetails: () -> Unit = {},
    onClickRoster: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(0) }

    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.background
    ) {
        BottomNavigationItem(
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                onClickDetails()
            },
            icon = {
                Icon(
                    modifier = modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_bottom_bar_details),
                    contentDescription = null,
                    tint = if (selectedItem == 0) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            },
            label = {
                Text(
                    modifier = modifier.padding(top = 4.dp),
                    text = "Details",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selectedItem == 0) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            }
        )
        BottomNavigationItem(
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                onClickRoster()
            },
            icon = {
                Icon(
                    modifier = modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_bottom_roster),
                    contentDescription = null,
                    tint = if (selectedItem == 1) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            },
            label = {
                Text(
                    modifier = modifier.padding(top = 4.dp),
                    text = "Roster",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selectedItem == 1) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RaceDetailsBottomBarPreview() {
    RaceSyncTheme {
        RaceDetailsBottomBar()
    }
}