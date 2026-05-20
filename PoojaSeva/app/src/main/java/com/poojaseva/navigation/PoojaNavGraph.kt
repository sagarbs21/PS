package com.poojaseva.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.poojaseva.ui.screens.pandit.PanditSelectScreen
import com.poojaseva.ui.screens.payment.PaymentScreen
import com.poojaseva.ui.screens.profile.ProfileScreen
import com.poojaseva.ui.screens.splash.SplashScreen

@Composable
fun PoojaNavGraph() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.Splash) {
        composable(Routes.Splash) {
            SplashScreen(onDone = {
                nav.navigate(Routes.Onboarding) { popUpTo(Routes.Splash) { inclusive = true } }
            })
        }
        composable(Routes.Onboarding) {
            OnboardingScreen(onDone = {
                nav.navigate(Routes.Auth) { popUpTo(Routes.Onboarding) { inclusive = true } }
            })
        }
        composable(Routes.Auth) {
            AuthScreen(onAuthenticated = {
                nav.navigate(Routes.Home) { popUpTo(Routes.Auth) { inclusive = true } }
            })
        }
        composable(Routes.Home) {
            HomeScreen(
                onCategoryClick = { id -> nav.navigate(Routes.serviceList(id)) },
                onServiceClick = { id -> nav.navigate(Routes.serviceDetail(id)) },
                onOrdersClick = { nav.navigate(Routes.Orders) },
                onProfileClick = { nav.navigate(Routes.Profile) },
            )
        }
        composable(
            Routes.ServiceList,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { entry ->
            val categoryId = entry.arguments?.getString("categoryId").orEmpty()
            ServiceListScreen(
                categoryId = categoryId,
                onBack = { nav.popBackStack() },
                onServiceClick = { id -> nav.navigate(Routes.serviceDetail(id)) },
            )
        }
        composable(
            Routes.ServiceDetail,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("serviceId").orEmpty()
            ServiceDetailScreen(
                serviceId = id,
                onBack = { nav.popBackStack() },
                onBook = { nav.navigate(Routes.panditSelect(id)) },
            )
        }
        composable(
            Routes.PanditSelect,
            arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
        ) { entry ->
            val sid = entry.arguments?.getString("serviceId").orEmpty()
            PanditSelectScreen(
                serviceId = sid,
                onBack = { nav.popBackStack() },
                onPanditChosen = { pid -> nav.navigate(Routes.bookingForm(sid, pid)) },
            )
        }
        composable(
            Routes.BookingForm,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("panditId") { type = NavType.StringType },
            )
        ) { entry ->
            BookingFormScreen(
                serviceId = entry.arguments?.getString("serviceId").orEmpty(),
                panditId = entry.arguments?.getString("panditId").orEmpty(),
                onBack = { nav.popBackStack() },
                onCreated = { bid -> nav.navigate(Routes.payment(bid)) },
            )
        }
        composable(
            Routes.Payment,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { entry ->
            val bid = entry.arguments?.getString("bookingId").orEmpty()
            PaymentScreen(
                bookingId = bid,
                onPaid = { nav.navigate(Routes.confirmation(bid)) { popUpTo(Routes.Home) } },
            )
        }
        composable(
            Routes.Confirmation,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { entry ->
            ConfirmationScreen(
                bookingId = entry.arguments?.getString("bookingId").orEmpty(),
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
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { entry ->
            OrderDetailScreen(
                bookingId = entry.arguments?.getString("bookingId").orEmpty(),
                onBack = { nav.popBackStack() }
            )
        }
        composable(Routes.Profile) {
            ProfileScreen(onBack = { nav.popBackStack() })
        }
    }
}
