// com.example.travelvault/MainActivity.kt
package com.example.travelvault

// --- NEW IMPORT ---
// --- END NEW IMPORT ---
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
            // --- NEW LINE TO ADD ---
            .addMigrations(MIGRATION_1_2) // Tell Room to run our migration
            // --- END NEW LINE ---
            .build()
    }

    private val repository by lazy {
        TicketRepository(database.ticketDao(), applicationContext)
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