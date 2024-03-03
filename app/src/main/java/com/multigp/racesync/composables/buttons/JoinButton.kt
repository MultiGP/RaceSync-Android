package com.multigp.racesync.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun JoinButton(
    isJoined: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isJoined) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface,
            contentColor = if (isJoined) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.tertiary
        ),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = if (isJoined) stringResource(R.string.title_button_joined) else stringResource(id = R.string.title_button_join),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JoinButtonPreview() {
    RaceSyncTheme {
        JoinButton(true)
    }
}