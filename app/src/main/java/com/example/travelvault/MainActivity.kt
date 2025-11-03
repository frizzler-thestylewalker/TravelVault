// com.example.travelvault/MainActivity.kt
package com.example.travelvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.travelvault.data.TicketRepository
import com.example.travelvault.data.local.AppDatabase
import com.example.travelvault.data.local.AppDatabase.Companion.MIGRATION_1_2
import com.example.travelvault.data.local.AppDatabase.Companion.MIGRATION_2_3 // <-- NEW IMPORT
import com.example.travelvault.ui.screens.AppNavigation
import com.example.travelvault.ui.theme.TravelVaultTheme
import com.example.travelvault.ui.viewmodel.TicketViewModel
import com.example.travelvault.ui.viewmodel.TicketViewModelFactory

class MainActivity : ComponentActivity() {

    // --- 1. Initialize all our components ---

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "travelvault.db"
        )
            .allowMainThreadQueries()
            // --- ADDED BOTH MIGRATIONS ---
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            // --- END OF CHANGE ---
            .build()
    }

    private val repository by lazy {
        // --- UPDATED CONSTRUCTOR ---
        // This will show an error until we update TicketRepository
        TicketRepository(
            ticketDao = database.ticketDao(),
            itineraryItemDao = database.itineraryItemDao(), // <-- ADDED
            context = applicationContext
        )
        // --- END OF CHANGE ---
    }

    private val ticketViewModel: TicketViewModel by viewModels {
        TicketViewModelFactory(repository)
    }

    // --- 2. The main entry point for the UI ---
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TravelVaultTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel = ticketViewModel)
                }
            }
        }
    }
}

