        val bottomItems by lazy { all.filter { it.showInBottomBar } }
        val all by lazy {
            listOf(
                Login,
                Register,
                Home,
                Cart,
                Profile,
                Work,
                ProductDetail,
                Favorites,
                Checkout,
                OrderHistory,
                Apply,
                MyApplications
            )
        }
package com.example.altafedeltium.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
    val showInBottomBar: Boolean = false
) {
    companion object {
        fun getBottomItems() = listOf(Home, Cart, Profile, Work)
    }

    data object Login : AppDestination(route = "login", label = "Login")

    data object Register : AppDestination(route = "register", label = "Registrazione")
    data object Home : AppDestination(route = "home", label = "Home", icon = Icons.Default.Home, showInBottomBar = true)
    data object Cart : AppDestination(route = "cart", label = "Carrello", icon = Icons.Default.ShoppingCart, showInBottomBar = true)
    data object Profile : AppDestination(route = "profile", label = "Profilo", icon = Icons.Default.Person, showInBottomBar = true)
    data object Work : AppDestination(route = "work", label = "Lavoro", icon = Icons.Default.Work, showInBottomBar = true)
    data object ProductDetail : AppDestination(route = "product/{productId}", label = "Dettaglio") {
        fun createRoute(productId: Int) = "product/$productId"
    }
    data object Favorites : AppDestination(route = "favorites", label = "Preferiti")
    data object Checkout : AppDestination(route = "checkout", label = "Checkout")
    data object OrderHistory : AppDestination(route = "order_history", label = "Storico ordini")

    // Career funnel – not in bottom bar
    data object Apply : AppDestination(route = "apply/{jobId}", label = "Candidatura") {
        fun createRoute(jobId: Int) = "apply/$jobId"
    }
    data object MyApplications : AppDestination(route = "my_applications", label = "Le mie Candidature")
}


