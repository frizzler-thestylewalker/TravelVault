// com.example.travelvault.ui.screens/AppNavigation.kt
package com.example.travelvault.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travelvault.ui.screens.itinerary.ItineraryDetailScreen
import com.example.travelvault.ui.screens.itinerary.ItineraryListScreen
import com.example.travelvault.ui.screens.navigation.BottomNavScreen
import com.example.travelvault.ui.viewmodel.ItineraryFolderViewModel
import com.example.travelvault.ui.viewmodel.ItineraryViewModel
import com.example.travelvault.ui.viewmodel.TicketViewModel
import com.example.travelvault.ui.viewmodel.factory.ItineraryViewModelFactory

/**
 * Defines the navigation routes for the app.
 * We now use "graphs" to group screens.
 */
object Routes {
    // Top-level graphs (for bottom nav)
    const val TICKETS_GRAPH = "tickets_graph"
    const val ITINERARY_GRAPH = "itinerary_graph"

    // Ticket screens (nested in TICKETS_GRAPH)
    const val TICKET_LIST = "ticket_list"
    const val UPLOAD_TICKET = "upload_ticket"

    // Itinerary screens (nested in ITINERARY_GRAPH)
    const val ITINERARY_LIST = "itinerary_list"
    const val ITINERARY_DETAIL = "itinerary_detail/{folderId}" // Requires a folderId argument
}

/**
 * The main AppNavigation composable.
 * This now accepts *both* ViewModels from MainActivity.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    ticketViewModel: TicketViewModel,
    itineraryViewModel: ItineraryViewModel,
    itineraryViewModelFactory: ItineraryViewModelFactory // Accept the factory
) {
    // This NavController is for the *main* navigation (Bottom Bar)
    val navController: NavHostController = rememberNavController()

    // We pass the required ViewModels down to the screens that need them.
    MainScreen(
        navController = navController,
        ticketViewModel = ticketViewModel,
        itineraryViewModel = itineraryViewModel,
        itineraryViewModelFactory = itineraryViewModelFactory // Pass factory down
    )
}

/**
 * The main screen container, which includes the Scaffold, BottomNavigationBar,
 * and the NavHost that swaps screens.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavHostController,
    ticketViewModel: TicketViewModel,
    itineraryViewModel: ItineraryViewModel,
    itineraryViewModelFactory: ItineraryViewModelFactory // Accept factory
) {
    Scaffold(
        // --- THIS IS THE FIX ---
        // Make the Scaffold's background transparent
        // so the gradient from MainActivity shows through.
        containerColor = Color.Transparent,
        // --- END OF FIX ---

        bottomBar = {
            // Define the list of bottom nav items
            val items = listOf(
                BottomNavScreen.Tickets,
                BottomNavScreen.Itinerary,
            )

            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid re-launching the same screen
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // This is the NavHost that contains all our screens.
        // It's inside the Scaffold, so screens are displayed above the bottom bar.
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Tickets.route, // Start on the Tickets tab
            modifier = Modifier.padding(innerPadding)
        ) {

            // --- NESTED GRAPH 1: TICKETS ---
            navigation(
                startDestination = Routes.TICKET_LIST,
                route = BottomNavScreen.Tickets.route // This graph is tied to the "Tickets" tab
            ) {
                // Ticket List Screen (Existing)
                composable(Routes.TICKET_LIST) {
                    TicketListScreen(
                        viewModel = ticketViewModel,
                        onNavigateToUpload = {
                            navController.navigate(Routes.UPLOAD_TICKET)
                        }
                    )
                }

                // Upload Ticket Screen (Existing)
                composable(Routes.UPLOAD_TICKET) {
                    UploadScreen(
                        viewModel = ticketViewModel,
                        onTicketSaved = {
                            navController.popBackStack() // Go back to the list
                        }
                    )
                }
            }

            // --- NESTED GRAPH 2: ITINERARY ---
            navigation(
                startDestination = Routes.ITINERARY_LIST,
                route = BottomNavScreen.Itinerary.route // This graph is tied to the "Itinerary" tab
            ) {
                // Itinerary List Screen (New)
                composable(Routes.ITINERARY_LIST) {
                    ItineraryListScreen(
                        viewModel = itineraryViewModel,
                        onNavigateToFolder = { folderId ->
                            // Navigate to the detail screen, passing the folderId
                            navController.navigate("itinerary_detail/$folderId")
                        }
                    )
                }

                // Itinerary Detail Screen (New)
                composable(
                    route = Routes.ITINERARY_DETAIL,
                    arguments = listOf(navArgument("folderId") { type = NavType.IntType })
                ) { navBackStackEntry -> // Get the back stack entry
                    // Create an ItineraryFolderViewModel, which is scoped to this
                    // specific navigation route (and its folderId).
                    val folderViewModel: ItineraryFolderViewModel = viewModel(
                        factory = itineraryViewModelFactory // Use the factory
                    )

                    // (Workaround from before, this is correct)
                    val folderId = navBackStackEntry.arguments?.getInt("folderId")
                    LaunchedEffect(folderId) {
                        if (folderId != null) {
                            folderViewModel.loadFolder(folderId)
                        }
                    }

                    ItineraryDetailScreen(
                        viewModel = folderViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}