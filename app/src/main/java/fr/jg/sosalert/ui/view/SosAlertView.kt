package fr.jg.sosalert.ui.view

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.jg.sosalert.ui.navigation.NavigationDestination

@Composable
fun SosAlertView(navController: NavHostController = rememberNavController()) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val navigationItemContentList =
        listOf(ContactsDestination, HomeDestination, SettingsDestination)

    val title = when (currentRoute) {
        ContactsDestination.route -> ContactsDestination.topBarTitle
        SettingsDestination.route -> SettingsDestination.topBarTitle
        else -> HomeDestination.topBarTitle
    }

    Scaffold(
        topBar = {
            SosAlertTopBar(title = stringResource(title))
        },
        bottomBar = {
            SosAlertBottomBar(
                currentDestination = currentBackStackEntry?.destination,
                navigationItemContentList = navigationItemContentList,
                onTabPressed = { route ->
                    navController.navigate(route)
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(200)) },
            exitTransition = { fadeOut(animationSpec = tween(200)) },
        ) {
            composable(route = ContactsDestination.route) {
                Contacts()
            }
            composable(route = HomeDestination.route) {
                Home()
            }
            composable(route = SettingsDestination.route) {
                Settings()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosAlertTopBar(title: String) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
    )
}

@Composable
private fun SosAlertBottomBar(
    currentDestination: NavDestination?,
    navigationItemContentList: List<NavigationDestination>,
    onTabPressed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hierarchyRoutes =
        currentDestination?.hierarchy?.mapNotNull { it.route }?.toList() ?: emptyList()

    Log.d("NavDebug", "Current hierarchy routes: $hierarchyRoutes")

    NavigationBar(modifier = modifier) {
        for (navItem in navigationItemContentList) {
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                onClick = {
                    if (currentDestination?.route != navItem.route) {
                        onTabPressed(navItem.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.bottomBarIcon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = context.getString(navItem.bottomBarTitle),
                    )
                }
            )
        }
    }
}