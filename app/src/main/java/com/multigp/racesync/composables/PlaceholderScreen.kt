package com.multigp.racesync.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun PlaceholderScreen(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    buttonTitle: String? = "",
    isError: Boolean = false,
    canRetry: Boolean = false,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(all = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = if(isError) MaterialTheme.colorScheme.error else Color.DarkGray
            )
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(top = 8.dp)
            )

            if(canRetry){
                TextButton(
                    onClick = onButtonClick
                ) {
                    Text(
                        text = buttonTitle!!,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, device = "id:Nexus One")
@Composable
fun PlaceholderScreenPreview() {
    RaceSyncTheme {
        PlaceholderScreen(
            "No Races Found",
            "You haven't joined any upcoming races yet.",
            modifier = Modifier,
            "Search Nearby Races",
            isError = true,
            canRetry = true
        )
    }
}