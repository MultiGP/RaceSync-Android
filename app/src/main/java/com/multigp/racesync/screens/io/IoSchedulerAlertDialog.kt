package com.multigp.racesync.screens.io

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.multigp.racesync.R

@Composable
fun IoSchedulerAlertDialog(
    activity: String,
    onDismiss: () -> Unit,
    onDontShowAgain: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.io_scheduler_alert_added_title)) },
        text = { Text(stringResource(R.string.io_scheduler_alert_added_message, activity)) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDontShowAgain) {
                Text(stringResource(R.string.io_scheduler_alert_dont_show_again))
            }
        }
    )
}
