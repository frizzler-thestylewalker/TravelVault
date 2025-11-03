// com.example.travelvault.ui.screens/AppNavigation.kt
package com.example.travelvault.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travelvault.ui.viewmodel.TicketViewModel

/**
 * Defines the navigation routes for the app.
 */
object Routes {
    const val TICKET_LIST = "ticket_list"
    const val UPLOAD_TICKET = "upload_ticket"

    // --- NEW ROUTE ---
    // This route needs an argument (the ID of the ticket)
    const val ITINERARY_SCREEN = "itinerary_screen/{ticketId}"
    // Helper function to build the route with the actual ID
    fun itineraryScreen(ticketId: Int) = "itinerary_screen/$ticketId"
    // --- END NEW ROUTE ---
}

/**
 * The main navigation composable for the app.
 * This controls which screen is currently displayed.
 */
@RequiresApi(Build.VERSION_CODES.O) // Added because our screens require it
@Composable
fun AppNavigation(viewModel: TicketViewModel) {
    // Create a NavController to manage navigation state
    val navController = rememberNavController()

    // NavHost is the container that displays the correct screen
    NavHost(
        navController = navController,
        startDestination = Routes.TICKET_LIST // The app will start on the list screen
    ) {
        // Define the "Ticket List" screen
        composable(Routes.TICKET_LIST) {
            TicketListScreen(
                viewModel = viewModel,
                onNavigateToUpload = {
                    navController.navigate(Routes.UPLOAD_TICKET)
                },
                // --- NEW NAVIGATION ACTION ---
                onNavigateToItinerary = { ticketId ->
                    // Navigate to the itinerary screen, passing the ticketId
                    navController.navigate(Routes.itineraryScreen(ticketId))
                }
                // --- END NEW ACTION ---
            )
        }

        // Define the "Upload Ticket" screen
        composable(Routes.UPLOAD_TICKET) {
            UploadScreen(
                viewModel = viewModel,
                onTicketSaved = {
                    navController.popBackStack() // Go back to the list screen after saving
                }
            )
        }

        // --- NEW COMPOSABLE BLOCK ---
        // Define the "Itinerary" screen
        composable(
            route = Routes.ITINERARY_SCREEN,
            arguments = listOf(navArgument("ticketId") { type = NavType.IntType })
        ) { backStackEntry ->
            // Extract the ticketId from the navigation arguments
            val ticketId = backStackEntry.arguments?.getInt("ticketId")
            if (ticketId == null) {
                // If ID is missing, just go back (shouldn't happen)
                navController.popBackStack()
            } else {
                // This is the new screen you created
                ItineraryScreen(
                    viewModel = viewModel,
                    ticketId = ticketId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        // --- END NEW BLOCK ---
    }
}

