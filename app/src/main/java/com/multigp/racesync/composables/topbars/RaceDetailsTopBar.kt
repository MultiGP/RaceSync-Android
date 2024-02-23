package com.multigp.racesync.composables.topbars

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaceDetailsTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit = {},
    onClickAction1: () -> Unit = {},
    onClickAction2: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = { onGoBack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Go Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        actions = {
            IconButton(onClick = { onClickAction1() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add_to_calendar),
                    contentDescription = "Save to calendar"
                )
            }
            IconButton(onClick = { onClickAction2() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = "Share Race"
                )
            }
        }
    )
}


@Preview(showBackground = true, heightDp = 48)
@Composable
fun RaceDetailsTopBarPreview(){
    RaceSyncTheme {
        RaceDetailsTopBar(title = "Race Details")
    }
}