package com.multigp.racesync.composables.text

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.domain.model.countryNameToCode
import com.multigp.racesync.domain.model.getCountryEmoji
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun TextWithFlag(
    text: String,
    countryName: String,
    modifier: Modifier = Modifier
){
    val countryCode = countryNameToCode[countryName]
    val emoji = countryCode?.let { getCountryEmoji(it) } ?: "🏳️"
    Text(text = "$text $emoji")
}


@Preview(showBackground = true)
@Composable
fun TextWithFlagPreview() {
    RaceSyncTheme {
        TextWithFlag(text = "Farooq Zaman", countryName="Pakistan")
    }
}