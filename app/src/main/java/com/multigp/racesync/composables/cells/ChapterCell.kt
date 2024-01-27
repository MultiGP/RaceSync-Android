package com.multigp.racesync.composables.cells

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun ChapterCell(
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_powered_by),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    )
                    .clipToBounds()
            )
            Spacer(modifier = modifier.padding(start = 8.dp))
            Column(modifier = modifier.weight(1.0f)) {
                Text(
                    text = "Sat, Jan 27 @ 8:00 AM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "RippinAZ Winter Race S...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Rippin' AZ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = modifier.padding(top = 8.dp)
                )
            }
            Column {
                OutlinedButton(
                    onClick = { }
                ) {
                    Text(
                        text = "Join",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {},
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_group),
                        contentDescription = null
                    )
                    Spacer(modifier = modifier.width(8.dp))
                    Text(
                        text = "10",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    }
}


@Preview(showBackground = true)
@Composable
fun ChapterCellPerview() {
    RaceSyncTheme {
        ChapterCell()
    }
}