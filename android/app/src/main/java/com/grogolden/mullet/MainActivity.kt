package com.grogolden.mullet

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController

import com.grogolden.mullet.ui.navigation.NavGraph

import dagger.hilt.android.AndroidEntryPoint


/**
 * Main entry point for the Mullet app.
 *
 * - Uses Jetpack Compose for UI rendering.
 * - Initializes the Navigation Component.
 * - Injected with Hilt for dependency management.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Called when the activity is first created.
     *
     * - Initializes Jetpack Compose.
     * - Sets up navigation using NavGraph.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Creates a navigation controller for managing screen transitions
            val navController = rememberNavController()

            // Loads the navigation graph, defining app screens and navigation behavior
            NavGraph(navController = navController)
        }
    }
}
