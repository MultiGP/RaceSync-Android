package com.multigp.racesync.screens.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
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
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(menus = drawerMenu) { route ->
                    scope.launch {
                        drawerState.close()
                    }
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
    modifier: Modifier = Modifier,
    onMenuClick: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Settings",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        menus.forEach {
            NavigationDrawerItem(
                label = { Text(text = it.title!!) },
                icon = { Image(imageVector = it.icon!!, contentDescription = null) },
                selected = false,
                onClick = { onMenuClick(it.route) })
        }
    }
}