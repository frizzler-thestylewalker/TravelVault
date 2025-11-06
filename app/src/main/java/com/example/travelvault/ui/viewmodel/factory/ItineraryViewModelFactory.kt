// com.example.travelvault.ui.viewmodel.factory/ItineraryViewModelFactory.kt
package com.example.travelvault.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.travelvault.data.ItineraryRepository
import com.example.travelvault.ui.viewmodel.ItineraryFolderViewModel
import com.example.travelvault.ui.viewmodel.ItineraryViewModel

/**
 * Factory for creating Itinerary-related ViewModels.
 */
class ItineraryViewModelFactory(
    private val repository: ItineraryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Handle ItineraryViewModel (List)
            modelClass.isAssignableFrom(ItineraryViewModel::class.java) -> {
                ItineraryViewModel(repository) as T
            }
            // --- WORKAROUND ---
            // We just create the ViewModel. We will initialize it
            // with the folderId from the AppNavigation screen.
            modelClass.isAssignableFrom(ItineraryFolderViewModel::class.java) -> {
                ItineraryFolderViewModel(repository) as T
            }
            // --- END WORKAROUND ---
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}