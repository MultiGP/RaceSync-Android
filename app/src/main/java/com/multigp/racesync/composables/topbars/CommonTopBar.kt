package com.multigp.racesync.composables.topbars

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun CommonTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onMenuClicked: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier.align(Alignment.Center)
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonTopBarPreview() {
    RaceSyncTheme {
        CommonTopBar(title = stringResource(id = R.string.title_screen_design_tracks))
    }
}