package com.multigp.racesync.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun JoinButton(
    isJoined: Boolean,
    status: String? = "Open",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = getContainerColor(status = status!!, isJoined = isJoined),
            contentColor = getContentColor(status = status, isJoined = isJoined)
        ),
        border = BorderStroke(1.dp, color = getBorderColor(status = status, isJoined = isJoined))
    ) {
        Text(
            text = getText(status = status, isJoined = isJoined),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun getContainerColor(status: String, isJoined: Boolean): Color {
    return if (status == "Closed") {
        Color.LightGray
    } else if (isJoined) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.surface
    }
}

@Composable
fun getContentColor(status: String, isJoined: Boolean): Color {
    return if (status == "Closed") {
        Color.DarkGray
    } else if (isJoined) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.tertiary
    }
}

@Composable
fun getBorderColor(status: String, isJoined: Boolean): Color {
    return if (status == "Closed") {
        Color.LightGray
    } else {
        MaterialTheme.colorScheme.tertiary
    }
}

@Composable
fun getText(status: String, isJoined: Boolean): String {
    return if (status == "Closed") {
        status
    } else if (isJoined) {
        "Joined"
    } else {
        "Join"
    }
}

@Preview(showBackground = true)
@Composable
fun JoinButtonPreview() {
    RaceSyncTheme {
        JoinButton(true, "Closed")
    }
}