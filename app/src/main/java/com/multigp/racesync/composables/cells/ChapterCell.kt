package com.multigp.racesync.composables.cells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.image.AsyncCircularLogo
import com.multigp.racesync.composables.image.CircularLogo
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.extensions.formatDate


@Composable
fun ChapterCell(
    chapter: Chapter,
    modifier: Modifier = Modifier,
    onClick: (Chapter) -> Unit = {}
) {
    val rippleColor = MaterialTheme.colorScheme.primary
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = modifier.clickable(
            onClick = { onClick(chapter) },
            interactionSource = interactionSource,
            indication = rememberRipple(color = rippleColor)
        )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(chapter.mainImageFileName != null) {
                AsyncCircularLogo(url = chapter.mainImageFileName)
            }else{
                CircularLogo(id = R.drawable.logo_powered_by)
            }
            Spacer(modifier = modifier.padding(start = 8.dp))
            Column(modifier = modifier.weight(1.0f)) {
                Text(
                    text = chapter.dateAdded ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                )
                Text(
                    text = chapter.name!!,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = modifier.padding(top = 4.dp, end = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = chapter.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                    modifier = modifier.padding(top = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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