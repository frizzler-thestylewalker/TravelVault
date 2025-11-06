// com.example.travelvault/MainActivity.kt
package com.example.travelvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import com.example.travelvault.data.ItineraryRepository
import com.example.travelvault.data.TicketRepository
import com.example.travelvault.data.local.AppDatabase
import com.example.travelvault.data.local.AppDatabase.Companion.MIGRATION_1_2
import com.example.travelvault.data.local.AppDatabase.Companion.MIGRATION_2_3
import com.example.travelvault.ui.screens.AppNavigation
import com.example.travelvault.ui.theme.TravelVaultTheme
import com.example.travelvault.ui.viewmodel.ItineraryViewModel
import com.example.travelvault.ui.viewmodel.TicketViewModel
import com.example.travelvault.ui.viewmodel.factory.ItineraryViewModelFactory
import com.example.travelvault.ui.viewmodel.TicketViewModelFactory

class MainActivity : ComponentActivity() {

    // --- Database ---
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "travelvault.db"
        )
            .allowMainThreadQueries()
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Include both migrations
            .build()
    }

    // --- Ticket Dependencies ---
    private val ticketRepository by lazy {
        TicketRepository(database.ticketDao(), applicationContext)
    }
    private val ticketViewModel: TicketViewModel by viewModels {
        TicketViewModelFactory(ticketRepository)
    }

    // --- Itinerary Dependencies ---
    private val itineraryRepository by lazy {
        ItineraryRepository(database.itineraryDao())
    }
    private val itineraryViewModelFactory by lazy {
        ItineraryViewModelFactory(itineraryRepository)
    }
    private val itineraryViewModel: ItineraryViewModel by viewModels {
        itineraryViewModelFactory
    }


    // --- 2. The main entry point for the UI ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TravelVaultTheme {

                // --- NEW: Define the gradient brush ---
                // We'll fade from the 'surface' color (card/header)
                // down to the 'background' color (base).
                val gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )

                // --- UPDATED: Apply the gradient to the root Surface ---
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = gradientBrush), // Apply the gradient
                    color = Color.Transparent // Make Surface's own color transparent
                ) {
                    AppNavigation(
                        ticketViewModel = ticketViewModel,
                        itineraryViewModel = itineraryViewModel,
                        itineraryViewModelFactory = itineraryViewModelFactory
                    )
                }
                // --- END OF UPDATE ---
            }
        }
    }
}