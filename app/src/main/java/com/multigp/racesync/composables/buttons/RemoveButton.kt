package com.multigp.racesync.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Owner-only secondary action — removes a race from the series. */
@Composable
fun RemoveButton(
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val color = MaterialTheme.colorScheme.error
    OutlinedButton(
        onClick = { if (!isLoading) onClick() },
        enabled = !isLoading,
        modifier = modifier.defaultMinSize(minWidth = 96.dp, minHeight = 32.dp),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = color,
            disabledContainerColor = Color.White,
            disabledContentColor = color.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, color = color)
    ) {
        Text(
            text = "Remove",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
