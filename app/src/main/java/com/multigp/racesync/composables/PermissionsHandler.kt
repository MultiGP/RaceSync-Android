package com.multigp.racesync.composables

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsHandler(
    permissionState: MultiplePermissionsState,
    deniedContent: @Composable (Boolean) -> Unit,
    grantedContent: @Composable () -> Unit
) {
    if (permissionState.allPermissionsGranted) {
        grantedContent()
    } else {
        val shouldShowRationale = permissionState.permissions.any { state ->
            (state.status as PermissionStatus.Denied).shouldShowRationale
        }
        deniedContent(shouldShowRationale)
    }
}


@ExperimentalPermissionsApi
@Composable
fun PermissionDeniedContent(
    @StringRes alertTitle: Int,
    @StringRes requestMessage: Int,
    @StringRes rationaleMessage: Int,
    shouldShowRationale: Boolean,
    onRequestPermission: () -> Unit
) {

    if (shouldShowRationale) {

        CustomAlertDialog(
            title = stringResource(alertTitle),
            body = stringResource(rationaleMessage),
            onConfirm = onRequestPermission
        )
    } else {
        CustomAlertDialog(
            title = stringResource(alertTitle),
            body = stringResource(requestMessage),
            onConfirm = onRequestPermission
        )
    }
}