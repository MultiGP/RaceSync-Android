package com.multigp.racesync.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.composables.text.CustomDropdownMenu
import com.multigp.racesync.composables.text.IconText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceConfigurationSheet(
    initialRadius: Double = 100.0,
    initialUnit: String = "mi",
    modifier: Modifier = Modifier,
    onBottomSheetDismiss: () -> Unit = {},
    onRadiusSelection: (radius: Double, unit: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val metricRadii = linkedMapOf(
        "150 km" to 150,
        "300 km" to 300,
        "750 km" to 750,
        "3000 km" to 3000,
        "4500km" to 4500
    )
    val imperialRadii = linkedMapOf(
        "100 mi" to 100,
        "200 mi" to 200,
        "500 mi" to 500,
        "2000 mi" to 2000,
        "3000 mi" to 3000
    )

    val (measurementSystemIndex, radiusIndex) = initialRadiusToIndex(
        initialRadius,
        initialUnit,
        metricRadii,
        imperialRadii
    )

    var selectedRadiusIndex by remember { mutableStateOf(radiusIndex) }
    var selectedMetricsIndex by remember { mutableStateOf(measurementSystemIndex) }

    ModalBottomSheet(
        onDismissRequest = onBottomSheetDismiss,
        sheetState = sheetState
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            text = stringResource(R.string.race_feed_option_sheet_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        Column(
            modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 60.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconText(
                    text = stringResource(R.string.label_search_radius),
                    icon = R.drawable.ic_radius
                )
                CustomDropdownMenu(
                    modifier = modifier
                        .weight(1.0f)
                        .padding(start = 16.dp),
                    label = "",
                    items = if (selectedMetricsIndex == 0) imperialRadii.keys.toList() else metricRadii.keys.toList(),
                    selectedIndex = selectedRadiusIndex,
                    onItemSelected = { index, _ ->
                        selectedRadiusIndex = index
                        val (radius, unit) = processRadiusSelection(
                            metricRadii,
                            imperialRadii,
                            index,
                            selectedMetricsIndex
                        )
                        onRadiusSelection(radius, unit)
                    }
                )
            }
            Row(
                modifier = modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconText(
                    text = stringResource(R.string.label_measurement_system),
                    icon = R.drawable.ic_ruler
                )
                CustomDropdownMenu(
                    modifier = modifier
                        .weight(1.0f)
                        .padding(start = 16.dp),
                    label = "",
                    items = listOf("Imperial", "Metric"),
                    selectedIndex = selectedMetricsIndex,
                    onItemSelected = { index, _ ->
                        selectedMetricsIndex = index
                        val (radius, unit) = processRadiusSelection(
                            metricRadii,
                            imperialRadii,
                            selectedRadiusIndex,
                            index
                        )
                        onRadiusSelection(radius, unit)
                    }
                )
            }
        }
    }
}

private fun processRadiusSelection(
    metricRadii: Map<String, Int>,
    imperialRadii: Map<String, Int>,
    radiusIndex: Int,
    measurementSystemIndex: Int
) = if (measurementSystemIndex == 0) {
    val key = imperialRadii.keys.toList()[radiusIndex]
    val radius = imperialRadii[key]
    Pair(radius?.toDouble() ?: 100.0, "mi")
} else {
    val key = metricRadii.keys.toList()[radiusIndex]
    val radius = metricRadii[key]
    Pair(radius?.toDouble() ?: 100.0, "km")
}

private fun initialRadiusToIndex(
    radius: Double,
    unit: String,
    metricRadii: Map<String, Int>,
    imperialRadii: Map<String, Int>
) = if (unit == "mi") {
    val index = imperialRadii.values.indexOf(radius.toInt())
    Pair(0, index)
} else {
    val index = metricRadii.values.indexOf(radius.toInt())
    Pair(1, index)
}