package com.multigp.racesync.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multigp.racesync.ui.theme.JoinButtonClosedGray
import com.multigp.racesync.ui.theme.JoinButtonGreen
import com.multigp.racesync.ui.theme.RaceCellTitleColor
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun JoinButton(
    isJoined: Boolean,
    status: String? = "Open",
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = {
            if (status != "Closed" && !isLoading) {
                onClick()
            }
        },
        enabled = status != "Closed" && !isLoading,
        modifier = modifier.defaultMinSize(minWidth = 76.dp, minHeight = 32.dp),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = resolveContainerColor(status = status ?: "Open", isJoined = isJoined),
            contentColor = resolveContentColor(status = status ?: "Open", isJoined = isJoined),
            disabledContainerColor = if (isLoading) {
                resolveContainerColor(status = status ?: "Open", isJoined = isJoined)
            } else {
                JoinButtonClosedGray
            },
            disabledContentColor = if (isLoading) {
                resolveContentColor(status = status ?: "Open", isJoined = isJoined)
            } else {
                RaceCellTitleColor
            }
        ),
        border = BorderStroke(1.dp, color = resolveBorderColor(status = status ?: "Open", isJoined = isJoined))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
                color = resolveContentColor(status = status ?: "Open", isJoined = isJoined)
            )
        } else {
            Text(
                text = resolveText(status = status ?: "Open", isJoined = isJoined),
                fontSize = 14.sp,
                fontWeight = if (isJoined) FontWeight.Normal else FontWeight.Bold
            )
        }
    }
}

// ── Private helper functions for JoinButton state resolution ──

private fun resolveContainerColor(status: String, isJoined: Boolean): Color {
    return when {
        status == "Closed" -> JoinButtonClosedGray
        isJoined -> JoinButtonGreen
        else -> Color.White
    }
}

private fun resolveContentColor(status: String, isJoined: Boolean): Color {
    return when {
        status == "Closed" -> RaceCellTitleColor
        isJoined -> Color.White
        else -> JoinButtonGreen
    }
}

private fun resolveBorderColor(status: String, isJoined: Boolean): Color {
    return if (status == "Closed") JoinButtonClosedGray else JoinButtonGreen
}

private fun resolveText(status: String, isJoined: Boolean): String {
    return when {
        status == "Closed" -> status
        isJoined -> "Joined"
        else -> "Join"
    }
}

@Preview(showBackground = true)
@Composable
fun JoinButtonPreview() {
    RaceSyncTheme {
        JoinButton(true, "Closed")
    }
}
