package com.guleryigitcan.teknasyoncase.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guleryigitcan.teknasyoncase.presentation.detail.EarthquakeDetailRoute
import com.guleryigitcan.teknasyoncase.presentation.home.HomeRoute
import com.guleryigitcan.teknasyoncase.presentation.settings.AlertSettingsRoute
import kotlinx.serialization.Serializable

data class NotificationNavigationRequest(val earthquakeId: String, val sequence: Long)

@Composable
fun AppNavigation(notificationRequest: NotificationNavigationRequest? = null) {
    val navController = rememberNavController()

    LaunchedEffect(notificationRequest) {
        notificationRequest?.let { request ->
            navController.navigate(EarthquakeDetailDestination(request.earthquakeId)) {
                launchSingleTop = true
            }
        }
    }
    NavHost(navController = navController, startDestination = HomeDestination) {
        composable<HomeDestination> {
            HomeRoute(
                onEarthquakeClick = { id ->
                    navController.navigate(EarthquakeDetailDestination(earthquakeId = id))
                },
                onAlertSettingsClick = { navController.navigate(AlertSettingsDestination) },
            )
        }
        composable<EarthquakeDetailDestination> {
            EarthquakeDetailRoute(onBackClick = navController::navigateUp)
        }
        composable<AlertSettingsDestination> {
            AlertSettingsRoute(onBackClick = navController::navigateUp)
        }
    }
}
