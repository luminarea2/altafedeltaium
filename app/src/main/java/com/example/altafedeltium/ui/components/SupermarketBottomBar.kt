package com.example.altafedeltium.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.example.altafedeltium.ui.navigation.AppDestination

@Composable
fun SupermarketBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    cartBadgeCount: Int = 0
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
    ) {
        AppDestination.bottomItems.forEach { destination ->
            val selected = currentDestination
                ?.hierarchy
                ?.any { it.route == destination.route } == true
            
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(AppDestination.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (destination == AppDestination.Cart && cartBadgeCount > 0) {
                                Badge {
                                    Text(text = cartBadgeCount.toString())
                                }
                            }
                        }
                    ) {
                        destination.icon?.let {
                            Icon(imageVector = it, contentDescription = destination.label)
                        }
                    }
                },
                label = { Text(text = destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4D2600),
                    selectedTextColor = Color(0xFF4D2600),
                    indicatorColor = Color.White.copy(alpha = 0.5f),
                    unselectedIconColor = Color(0xFF4D2600).copy(alpha = 0.7f),
                    unselectedTextColor = Color(0xFF4D2600).copy(alpha = 0.7f)
                )
            )
        }
    }
}
