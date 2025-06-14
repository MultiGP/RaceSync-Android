package com.multigp.racesync.screens.landing

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.multigp.racesync.R
import com.multigp.racesync.composables.PermissionDialog
import com.multigp.racesync.composables.RationaleDialog
import com.multigp.racesync.navigation.Landing
import com.multigp.racesync.navigation.LandingNavGraph
import com.multigp.racesync.navigation.Logout
import com.multigp.racesync.navigation.NavDestination
import com.multigp.racesync.navigation.drawerMenu
import com.multigp.racesync.viewmodels.DrawerContentViewModel
import com.multigp.racesync.viewmodels.LandingViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LandingScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var selectedMenuItem by rememberSaveable {
        mutableStateOf(Landing.route)
    }

    BackHandler(onBack = {
        scope.launch {
            drawerState.close()
        }
    })

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus = drawerMenu, selectedMenuItem = selectedMenuItem) { route ->
                    scope.launch {
                        drawerState.close()
                    }
                    if (route == Logout.route) {
                        onLogout()
                    } else {
                        selectedMenuItem = route
                        navController.navigate(route)
                    }
                }
            }
        }
    ) {
        LandingNavGraph(
            navController = navController,
            modifier = modifier,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DrawerContent(
    menus: List<NavDestination>,
    selectedMenuItem: String,
    modifier: Modifier = Modifier,
    viewModel: DrawerContentViewModel = hiltViewModel(),
    onMenuClick: (String) -> Unit
) {
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var requestFCMTokenAfterPermission by remember { mutableStateOf(false) }

    val receiveNotifications by viewModel.notificationPreference.collectAsState()
    val scope = rememberCoroutineScope()

    val notificationPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
        else null

    LaunchedEffect(notificationPermissionState?.status?.isGranted, requestFCMTokenAfterPermission) {
        if (requestFCMTokenAfterPermission && notificationPermissionState?.status?.isGranted == true) {
            requestFCMTokenAfterPermission = false
            val token = Firebase.messaging.token.await()
            viewModel.updateFCMToken(token)
        }
    }

    Surface(modifier = modifier.padding(all = 16.dp)) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            Text(
                text = "MultiGP Essentials",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge,
                modifier = modifier.align(Alignment.CenterHorizontally),

                )
            Spacer(modifier = modifier.height(16.dp))
            menus.forEach {
                NavigationDrawerItem(
                    label = {
                        Text(
                            text = it.title!!
                        )
                    },
                    shape = RectangleShape,
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.surface,
                        unselectedContainerColor = MaterialTheme.colorScheme.surface,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary
                    ),
                    selected = selectedMenuItem == it.route,
                    icon = {
                        it.iconPainterId?.let { resId ->
                            Icon(
                                painter = painterResource(resId),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        } ?: run {
                            Icon(imageVector = it.icon!!, contentDescription = null)
                        }
                    },
                    onClick = { onMenuClick(it.route) })
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier.padding(start = 8.dp, bottom = 16.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications, // Notification bell
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.receive_notifications_from_zippyq),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = receiveNotifications,
                    onCheckedChange = {
                        scope.launch {
                            val shouldRequestPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                notificationPermissionState?.status?.isGranted == false

                            if (shouldRequestPermission) {
                                notificationPermissionState?.let { notifPermState ->
                                    if (notifPermState.status.shouldShowRationale) {
                                        showRationaleDialog = true
                                    } else {
                                        showPermissionDialog = true
                                    }
                                }
                            } else {
                                val token = Firebase.messaging.token.await()
                                viewModel.updateFCMToken(token)
                            }
                        }
                    },
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = modifier.height(16.dp))

            Text(
                text = "Copyright @ 2015 - 2025 MultiGP, Inc.",
                color = Color.Gray,
                style = MaterialTheme.typography.titleSmall,
                modifier = modifier.align(Alignment.Start),

                )
            Text(
                text = "Developed by Viki 'Barracuda' Baarathi",
                color = Color.Gray,
                style = MaterialTheme.typography.titleSmall,
                modifier = modifier.align(Alignment.Start),

                )
        }
        if (showRationaleDialog) {
            RationaleDialog(onDismiss = { showRationaleDialog = false })
        }
        if (showPermissionDialog) {
            PermissionDialog(
                onRequestPermission = {
                    requestFCMTokenAfterPermission = true
                    notificationPermissionState?.launchPermissionRequest()
                },
                onDismiss = {
                    showPermissionDialog = false
                }
            )
        }
    }
}