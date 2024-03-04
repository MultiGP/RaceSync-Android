package com.multigp.racesync.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.text.IconText
import com.multigp.racesync.composables.text.CustomDropdownMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceConfigurationSheet(
    modifier: Modifier = Modifier,
    onBottomSheetDismiss: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState()
    var selectedRadiusIndex by remember { mutableStateOf(0) }
    var selectedMetricsIndex by remember { mutableStateOf(0) }
    val metricRadii = listOf("150 km", "300 km", "750 km", "3000 km", "4500km")
    val imperialRadii = listOf("100 mi", "200 mi", "500 mi", "2000 mi", "3000 mi")

    ModalBottomSheet(
        onDismissRequest = onBottomSheetDismiss,
        sheetState = sheetState
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = "Race Feed Options",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Divider(thickness = 1.dp, color = Color.Gray)
        Column(
            modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconText(text = "Search Radius", icon = R.drawable.ic_radius)
                CustomDropdownMenu(
                    modifier = modifier
                        .weight(1.0f)
                        .padding(start = 16.dp),
                    label = "",
                    items = if (selectedMetricsIndex == 0) imperialRadii else metricRadii,
                    selectedIndex = selectedRadiusIndex,
                    onItemSelected = { index, _ -> selectedRadiusIndex = index }
                )
            }
            Row(
                modifier = modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconText(text = "Measurement System", icon = R.drawable.ic_ruler)
                CustomDropdownMenu(
                    modifier = modifier
                        .weight(1.0f)
                        .padding(start = 16.dp),
                    label = "",
                    items = listOf("Imperial", "Metric"),
                    selectedIndex = selectedMetricsIndex,
                    onItemSelected = { index, _ -> selectedMetricsIndex = index }
                )
            }
        }
    }
}