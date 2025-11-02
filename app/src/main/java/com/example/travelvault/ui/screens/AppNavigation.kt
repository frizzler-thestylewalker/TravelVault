// com.example.travelvault.ui.screens/AppNavigation.kt
package com.example.travelvault.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.travelvault.ui.viewmodel.TicketViewModel

/**
 * Defines the navigation routes for the app.
 */
object Routes {
    const val TICKET_LIST = "ticket_list"
    const val UPLOAD_TICKET = "upload_ticket"
}

/**
 * The main navigation composable for the app.
 * This controls which screen is currently displayed.
 */
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
            // We will create this 'TicketListScreen' composable in the next step
            // For now, let's just pass in the required parameters
            TicketListScreen(
                viewModel = viewModel,
                onNavigateToUpload = {
                    navController.navigate(Routes.UPLOAD_TICKET)
                }
            )
        }

        // Define the "Upload Ticket" screen
        composable(Routes.UPLOAD_TICKET) {
            // We will create this 'UploadScreen' composable later
            UploadScreen(
                viewModel = viewModel,
                onTicketSaved = {
                    navController.popBackStack() // Go back to the list screen after saving
                }
            )
        }
    }
}