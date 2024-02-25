package com.multigp.racesync.screens.landing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.multigp.racesync.navigation.Landing
import com.multigp.racesync.navigation.LandingNavGraph
import com.multigp.racesync.navigation.NavDestination
import com.multigp.racesync.navigation.drawerMenu
import kotlinx.coroutines.launch

@Composable
fun LandingScreen(
    navController: NavHostController,
    drawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedMenuItem by rememberSaveable {
        mutableStateOf(Landing.route)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus = drawerMenu, selectedMenuItem = selectedMenuItem) { route ->
                    scope.launch {
                        drawerState.close()
                    }
                    selectedMenuItem = route
                    navController.navigate(route)
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

@Composable
fun DrawerContent(
    menus: List<NavDestination>,
    selectedMenuItem: String,
    modifier: Modifier = Modifier,
    onMenuClick: (String) -> Unit
) {
    Surface(modifier = modifier.padding(all = 16.dp)) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            Text(
                text = "Settings",
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
                    onClick = { onMenuClick(it.route) })
                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)
            }

            Spacer(modifier = modifier.height(16.dp))

            Text(
                text = "Copyright @ 2015 - 2024 MultiGP, Inc.",
                color = Color.Gray,
                style = MaterialTheme.typography.titleSmall,
                modifier = modifier.align(Alignment.Start),

                )
        }
    }
}