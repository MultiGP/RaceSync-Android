package com.multigp.racesync.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.multigp.racesync.R
import com.multigp.racesync.extensions.alertDialog
import com.multigp.racesync.extensions.textButton

@Composable
fun PermissionDialog(onRequestPermission: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.alertDialog(),
        title = { Text(stringResource(id = R.string.notification_permission_title)) },
        text = { Text(stringResource(id = R.string.notification_permission_description)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onRequestPermission()
                    onDismiss()
                },
                modifier = Modifier.textButton(),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) { Text(text = stringResource(R.string.request_notification_permission)) }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
fun RationaleDialog(onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.alertDialog(),
        title = { Text(stringResource(id = R.string.notification_permission_title)) },
        text = { Text(stringResource(id = R.string.notification_permission_settings)) },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.textButton(),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) { Text(text = stringResource(R.string.ok)) }
        },
        onDismissRequest = onDismiss
    )
}