package com.multigp.racesync.composables.cells

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.extensions.shimmerLoadingAnimation
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun ChapterLoadingCell(
    modifier: Modifier = Modifier
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComponentCircle(modifier.size(64.dp))
            Spacer(modifier = modifier.padding(start = 6.dp))
            Column(modifier = modifier.weight(1.0f)) {
                ComponentRectangleLineLong(modifier.height(12.dp).width(110.dp))
                ComponentRectangleLineLong(modifier.height(24.dp).padding(top = 6.dp))
                ComponentRectangleLineLong(modifier.height(20.dp).padding(top = 6.dp))
            }
            Column(Modifier.padding(start = 8.dp)) {
                ComponentRectangle(modifier.size(width = 54.dp, height = 32.dp))
                ComponentRectangle(modifier.size(width = 54.dp, height = 36.dp).padding(top = 4.dp))
            }
        }
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
fun ComponentCircle(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(color = Color.LightGray, shape = CircleShape)
            .size(64.dp)
            .shimmerLoadingAnimation() // <--- Here.
    )
}

@Composable
fun ComponentSquare(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(shape = RoundedCornerShape(24.dp))
            .background(color = Color.LightGray)
            .size(32.dp)
            .shimmerLoadingAnimation() // <--- Here.
    )
}

@Composable
fun ComponentRectangle(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.small)
            .background(color = Color.LightGray)
            .shimmerLoadingAnimation() // <--- Here.
    )
}

@Composable
fun ComponentRectangleLineLong(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(color = Color.LightGray)
            .height(32.dp)
            .fillMaxWidth()
            .shimmerLoadingAnimation() // <--- Here.
    )
}

@Composable
fun ComponentRectangleLineShort(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(color = Color.LightGray)
            .height(32.dp)
            .fillMaxWidth()
            .shimmerLoadingAnimation() // <--- Here.
    )
}


@Preview(showBackground = true, device = "id:Nexus One")
@Composable
fun ChapterLoadingCellPreview(){
    RaceSyncTheme {
        Column {
            ChapterLoadingCell()
        }
    }
}