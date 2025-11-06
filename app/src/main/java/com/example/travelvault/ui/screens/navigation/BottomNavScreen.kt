// com.example.travelvault.ui.screens.navigation/BottomNavScreen.kt
package com.example.travelvault.ui.screens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the items for the bottom navigation bar.
 *
 * @param route The navigation route string.
 * @param title The text label for the tab.
 * @param icon The icon for the tab.
 */
sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    /**
     * Represents the "Tickets" tab.
     * Its route is "tickets_graph" which will be a nested navigation graph
     * for the ticket list and upload screens.
     */
    object Tickets : BottomNavScreen(
        route = "tickets_graph",
        title = "Tickets",
        icon = Icons.Default.List
    )

    /**
     * Represents the "Itinerary" tab.
     * Its route is "itinerary_graph" which will be a nested navigation graph
     * for the itinerary list and detail screens.
     */
    object Itinerary : BottomNavScreen(
        route = "itinerary_graph",
        title = "Itinerary",
        icon = Icons.Default.CardTravel
    )
}