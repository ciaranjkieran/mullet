package com.grogolden.mullet.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

import com.grogolden.mullet.ui.navigation.NavGraph

/**
 * Entry point for the Mullet app's Jetpack Compose UI.
 *
 * - Creates a `NavController` for managing navigation.
 * - Passes the `NavController` to `NavGraph` to handle screen transitions.
 */
@Composable
fun MulletComposeApp() {
    // Initialize a navigation controller for managing screen navigation
    val navController = rememberNavController()

    // Set up the navigation graph for the app
    NavGraph(navController = navController)
}
