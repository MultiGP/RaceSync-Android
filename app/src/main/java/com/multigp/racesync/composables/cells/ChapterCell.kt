package com.multigp.racesync.composables.cells

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.Chapter


@Composable
fun ChapterCell(
    chapter: Chapter,
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = chapter.mainImageFileName,
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        CircleShape
                    )
                    .clipToBounds()
            )
            Spacer(modifier = modifier.padding(start = 8.dp))
            Column(modifier = modifier.weight(1.0f)) {
                Text(
                    text = chapter.dateAdded,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                )
                Text(
                    text = chapter.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = modifier.padding(top = 8.dp, end = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "Rippin' AZ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    modifier = modifier.padding(top = 8.dp)
                )
            }
            Column {
                OutlinedButton(
                    onClick = { },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.tertiary
                    ),
                    border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(
                        text = "Join",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = {},
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_group),
                        contentDescription = null
                    )
                    Spacer(modifier = modifier.width(4.dp))
                    Text(
                        text = "" + chapter.memberCount,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ChapterCellPerview() {
//    RaceSyncTheme {
//        ChapterCell()
//    }
//}