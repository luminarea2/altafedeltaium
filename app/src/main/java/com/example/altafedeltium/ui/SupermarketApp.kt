package com.example.altafedeltium.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.altafedeltium.ui.components.SupermarketBottomBar
import com.example.altafedeltium.ui.navigation.AppDestination
import com.example.altafedeltium.ui.screens.applications.MyApplicationsScreen
import com.example.altafedeltium.ui.screens.apply.ApplicationScreen
import com.example.altafedeltium.ui.screens.auth.LoginScreen
import com.example.altafedeltium.ui.screens.auth.RegisterScreen
import com.example.altafedeltium.ui.screens.cart.CartScreen
import com.example.altafedeltium.ui.screens.cart.CheckoutScreen
import com.example.altafedeltium.ui.screens.home.FavoritesScreen
import com.example.altafedeltium.ui.screens.home.HomeScreen
import com.example.altafedeltium.ui.screens.home.ProductDetailScreen
import com.example.altafedeltium.ui.screens.profile.OrderHistoryScreen
import com.example.altafedeltium.ui.screens.profile.ProfileScreen
import com.example.altafedeltium.ui.screens.work.WorkScreen
import com.example.altafedeltium.ui.screens.work.JobFavoritesScreen
import com.example.altafedeltium.ui.viewmodel.AuthSessionStore
import com.example.altafedeltium.ui.viewmodel.HomeViewModel
import com.example.altafedeltium.ui.viewmodel.JobSearchViewModel

@Composable
fun SupermarketApp() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()
    val jobSearchViewModel: JobSearchViewModel = viewModel()
    val initialUser = AuthSessionStore.currentUser

    LaunchedEffect(initialUser?.email) {
        initialUser?.let { user ->
            homeViewModel.syncProfileFromAuthenticatedUser(user.fullName, user.email)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination: NavDestination? = navBackStackEntry?.destination

    val showBottomBarRoutes = setOf(
        AppDestination.Home.route,
        AppDestination.Cart.route,
        AppDestination.Profile.route,
        AppDestination.Work.route,
        AppDestination.Favorites.route,
        AppDestination.Checkout.route,
        AppDestination.OrderHistory.route,
        AppDestination.ProductDetail.route
    )
    val showBottomBar = currentDestination
        ?.hierarchy
        ?.any { it.route in showBottomBarRoutes } == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                SupermarketBottomBar(
                    navController = navController,
                    currentDestination = currentDestination,
                    cartBadgeCount = homeViewModel.cartItemsCount()
                )
            }
        }
    ) { paddingValues ->
        SupermarketNavHost(
            paddingValues = paddingValues,
            navController = navController,
            homeViewModel = homeViewModel,
            jobSearchViewModel = jobSearchViewModel,
            startDestination = if (initialUser != null) AppDestination.Home.route else AppDestination.Login.route,
            navToHome = {
                navController.navigate(AppDestination.Home.route) {
                    popUpTo(AppDestination.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            navToRegister = { navController.navigate(AppDestination.Register.route) },
            navToLogin = { navController.popBackStack() }
        )
    }
}

@Composable
private fun SupermarketNavHost(
    paddingValues: PaddingValues,
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    jobSearchViewModel: JobSearchViewModel,
    startDestination: String,
    navToHome: () -> Unit,
    navToRegister: () -> Unit,
    navToLogin: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(AppDestination.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    AuthSessionStore.currentUser?.let { user ->
                        homeViewModel.syncProfileFromAuthenticatedUser(user.fullName, user.email)
                    }
                    navToHome()
                },
                onGoToRegister = navToRegister
            )
        }
        composable(AppDestination.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    AuthSessionStore.currentUser?.let { user ->
                        homeViewModel.syncProfileFromAuthenticatedUser(user.fullName, user.email)
                    }
                    navToHome()
                },
                onGoToLogin = navToLogin
            )
        }
        composable(AppDestination.Home.route) {
            HomeScreen(
                homeViewModel = homeViewModel,
                onOpenProduct = { productId ->
                    navController.navigate(AppDestination.ProductDetail.createRoute(productId))
                },
                onOpenFavorites = {
                    navController.navigate(AppDestination.Favorites.route)
                }
            )
        }
        composable(AppDestination.Cart.route) {
            CartScreen(
                homeViewModel = homeViewModel,
                onCheckout = { navController.navigate(AppDestination.Checkout.route) }
            )
        }
        composable(AppDestination.Profile.route) {
            ProfileScreen(
                homeViewModel = homeViewModel,
                onOpenOrderHistory = { navController.navigate(AppDestination.OrderHistory.route) },
                onLogout = {
                    // Clear transient app state tied to the previous user
                    homeViewModel.clearSession()
                    AuthSessionStore.logout()
                    navController.navigate(AppDestination.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = AppDestination.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: return@composable
            val product = homeViewModel.getProductById(productId) ?: return@composable
            ProductDetailScreen(
                product = product,
                isFavorite = homeViewModel.isFavorite(productId),
                onBack = { navController.popBackStack() },
                onToggleFavorite = { homeViewModel.toggleFavorite(productId) },
                onAddToCart = { homeViewModel.addToCart(it) },
                onIncreaseQuantity = { homeViewModel.increaseQuantity(it) },
                onDecreaseQuantity = { homeViewModel.decreaseQuantity(it) },
                cartQuantity = homeViewModel.cartItemQuantity(productId)
            )
        }
        composable(AppDestination.Favorites.route) {
            FavoritesScreen(
                favorites = homeViewModel.favoriteProducts(),
                onBack = { navController.popBackStack() },
                onOpenProduct = { productId ->
                    navController.navigate(AppDestination.ProductDetail.createRoute(productId))
                },
                onRemoveFavorite = { homeViewModel.toggleFavorite(it) },
                onAddToCart = { homeViewModel.addToCart(it) },
                onIncreaseQuantity = { homeViewModel.increaseQuantity(it) },
                onDecreaseQuantity = { homeViewModel.decreaseQuantity(it) },
                cartQuantityFor = { homeViewModel.cartItemQuantity(it) }
            )
        }
        composable(AppDestination.Checkout.route) {
            CheckoutScreen(
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(AppDestination.OrderHistory.route)
                }
            )
        }
        composable(AppDestination.OrderHistory.route) {
            OrderHistoryScreen(
                homeViewModel = homeViewModel,
                onBack = {
                    // Navigate back to the cart (which should be empty after placing the order)
                    navController.navigate(AppDestination.Cart.route) {
                        // Remove the checkout screen from the back stack so Back from Cart doesn't return to it
                        popUpTo(AppDestination.Checkout.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(AppDestination.Work.route) {
            WorkScreen(
                viewModel = jobSearchViewModel,
                onApply = { jobId ->
                    navController.navigate(AppDestination.Apply.createRoute(jobId))
                },
                onMyApplications = {
                    navController.navigate(AppDestination.MyApplications.route)
                }
                ,
                onOpenFavoritesJobs = {
                    navController.navigate(AppDestination.FavoritesJobs.route)
                }
            )
        }

        composable(AppDestination.FavoritesJobs.route) {
            JobFavoritesScreen(
                favorites = jobSearchViewModel.favoriteJobs(),
                onBack = { navController.popBackStack() },
                onOpenJob = { jobId -> navController.navigate(AppDestination.Apply.createRoute(jobId)) },
                onToggleFavorite = { jobSearchViewModel.toggleFavorite(it) },
                onApply = { jobId -> navController.navigate(AppDestination.Apply.createRoute(jobId)) }
            )
        }
        composable(
            route = AppDestination.Apply.route,
            arguments = listOf(navArgument("jobId") { type = NavType.IntType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getInt("jobId") ?: return@composable
            ApplicationScreen(
                jobId = jobId,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(AppDestination.MyApplications.route) {
                        popUpTo(AppDestination.Work.route)
                    }
                },
                homeViewModel = homeViewModel
            )
        }
        composable(AppDestination.MyApplications.route) {
            MyApplicationsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
