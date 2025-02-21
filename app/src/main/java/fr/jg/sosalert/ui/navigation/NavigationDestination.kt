package fr.jg.sosalert.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

interface NavigationDestination {
    val route: String
    val topBarTitle: Int
    val bottomBarTitle: Int
    val bottomBarIcon: ImageVector
}