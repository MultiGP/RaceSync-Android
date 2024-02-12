package com.multigp.racesync.composables

import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CustomAlertDialog(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    confirmButtonTitle: String? = "OK",
    dismissButtonTitle: String? = "Cancel",
    onConfirm: () -> Unit = {},
    onDismiss: (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(body)
        },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text(confirmButtonTitle ?: "OK")
            }
        },
        dismissButton = {
            if (onDismiss != null) {
                Button(onClick = onDismiss) {
                    Text(dismissButtonTitle ?: "Cancel")
                }
            }
        }
    )
}