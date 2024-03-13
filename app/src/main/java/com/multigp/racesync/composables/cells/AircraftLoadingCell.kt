package com.multigp.racesync.composables.cells

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun AircraftLoadingCell(
    modifier: Modifier = Modifier
) {
    Column{
        ComponentRectangle(modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.78f)
            .clip(shape = RoundedCornerShape(10.dp)))
        Spacer(modifier = modifier.height(8.dp))
        ComponentRectangleLineLong(
            modifier
                .height(18.dp)
                .fillMaxWidth())
    }
}

@Preview(showBackground = true, widthDp = 240)
@Composable
fun AircraftLoadingCellPreview(){
    RaceSyncTheme {
        AircraftLoadingCell()
    }
}