// com.example.travelvault.ui.viewmodel/TicketViewModelFactory.kt
package com.example.travelvault.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelvault.data.TicketRepository

/**
 * Factory class to instantiate the TicketViewModel correctly.
 * It ensures the ViewModel receives the required TicketRepository dependency.
 */
class TicketViewModelFactory(
    private val repository: TicketRepository
) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the requested ViewModel is TicketViewModel
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            // Return a new instance of TicketViewModel using the provided repository
            return TicketViewModel(repository) as T
        }
        // If we try to create a different ViewModel, throw an error
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}