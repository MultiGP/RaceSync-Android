package com.multigp.racesync.composables.cells

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun RaceDetailsCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.weight(1.0f)
        )
        Text(
            text = value,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge,
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_forward),
            tint = Color.Gray,
            contentDescription = null
        )
    }
}


@Preview(showBackground = true, widthDp = 340)
@Composable
fun RaceDetailsCell() {
    RaceSyncTheme {
        RaceDetailsCell("Race Class", "Tiny Whoop Class")
    }
}
