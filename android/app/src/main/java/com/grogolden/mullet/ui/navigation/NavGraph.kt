package com.grogolden.mullet.ui.navigation

import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.grogolden.mullet.ui.screens.DashboardScreen

/**
 * Defines the navigation graph for the Mullet app.
 *
 * - Uses Jetpack Compose Navigation (`NavHost`).
 * - Manages screen transitions via `navController`.
 * - Sets "dashboard" as the initial screen.
 *
 * @param navController The navigation controller managing app navigation.
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "dashboard" // Set Dashboard as the initial destination
    ) {
        // Dashboard destination
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
    }
}
