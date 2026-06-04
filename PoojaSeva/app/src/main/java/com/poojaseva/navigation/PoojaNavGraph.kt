package com.poojaseva.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import com.poojaseva.ui.screens.auth.AuthScreen
import com.poojaseva.ui.screens.booking.BookingFormScreen
import com.poojaseva.ui.screens.confirmation.ConfirmationScreen
import com.poojaseva.ui.screens.detail.ServiceDetailScreen
import com.poojaseva.ui.screens.home.HomeScreen
import com.poojaseva.ui.screens.list.ServiceListScreen
import com.poojaseva.ui.screens.onboarding.OnboardingScreen
import com.poojaseva.ui.screens.orders.OrderDetailScreen
import com.poojaseva.ui.screens.orders.OrdersScreen
import com.poojaseva.ui.screens.payment.PaymentScreen
import com.poojaseva.ui.screens.profile.ProfileScreen
import com.poojaseva.ui.screens.search.SearchScreen
import com.poojaseva.ui.screens.splash.SplashScreen

@Composable
fun PoojaNavGraph() {
    val nav = rememberNavController()

    // Clears the whole back stack so the target becomes the new root (used after
    // login and logout so the user can't navigate "back" into the wrong state).
    val resetTo: (String) -> Unit = { route ->
        nav.navigate(route) {
            popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(navController = nav, startDestination = Routes.Splash) {
        composable(Routes.Splash) {
            SplashScreen(onReady = { route -> resetTo(route) })
        }

        composable(Routes.Onboarding) {
            OnboardingScreen(onDone = { resetTo(Routes.Auth) })
        }

        composable(Routes.Auth) {
            AuthScreen(onAuthenticated = { resetTo(Routes.Home) })
        }

        composable(Routes.Home) {
            HomeScreen(
                onSearchClick = { nav.navigate(Routes.Search) },
                onCategoryClick = { id -> nav.navigate(Routes.serviceList(id)) },
                onServiceClick = { id -> nav.navigate(Routes.serviceDetail(id)) },
                onOrdersClick = { nav.navigate(Routes.Orders) },
                onProfileClick = { nav.navigate(Routes.Profile) },
            )
        }

        composable(Routes.Search) {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onServiceClick = { id -> nav.navigate(Routes.serviceDetail(id)) },
            )
        }

        composable(
            Routes.ServiceList,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType }),
        ) {
            ServiceListScreen(
                onBack = { nav.popBackStack() },
                onServiceClick = { id -> nav.navigate(Routes.serviceDetail(id)) },
            )
        }

        composable(
            Routes.ServiceDetail,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType }),
        ) {
            ServiceDetailScreen(
                onBack = { nav.popBackStack() },
                onBook = { id -> nav.navigate(Routes.bookingForm(id)) },
            )
        }

        composable(
            Routes.BookingForm,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType }),
        ) {
            BookingFormScreen(
                onBack = { nav.popBackStack() },
                onCreated = { bid -> nav.navigate(Routes.payment(bid)) },
            )
        }

        composable(
            Routes.Payment,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) {
            PaymentScreen(
                onBack = { nav.popBackStack() },
                onPaid = { bid ->
                    nav.navigate(Routes.confirmation(bid)) { popUpTo(Routes.Home) }
                },
            )
        }

        composable(
            Routes.Confirmation,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) {
            ConfirmationScreen(
                onViewOrders = { nav.navigate(Routes.Orders) { popUpTo(Routes.Home) } },
                onHome = { nav.popBackStack(Routes.Home, inclusive = false) },
            )
        }

        composable(Routes.Orders) {
            OrdersScreen(
                onBack = { nav.popBackStack() },
                onOrderClick = { id -> nav.navigate(Routes.orderDetail(id)) },
            )
        }

        composable(
            Routes.OrderDetail,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType }),
        ) {
            OrderDetailScreen(onBack = { nav.popBackStack() })
        }

        composable(Routes.Profile) {
            ProfileScreen(
                onBack = { nav.popBackStack() },
                onSignIn = { resetTo(Routes.Auth) },
                onLoggedOut = { resetTo(Routes.Auth) },
            )
        }
    }
}
