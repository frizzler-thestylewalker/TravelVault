// com.example.travelvault.data/ItineraryRepository.kt
package com.example.travelvault.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.travelvault.data.local.ItineraryDao
import com.example.travelvault.data.model.ItineraryFolderEntity
import com.example.travelvault.data.model.ItineraryFolderWithItems
import com.example.travelvault.data.model.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository for handling all Itinerary-related data operations.
 * This class is the single source of truth for Itinerary data
 * and abstracts the data source (Room DAO) from the ViewModels.
 */
class ItineraryRepository(private val itineraryDao: ItineraryDao) {

    // --- Folder Functions ---

    /**
     * Gets a reactive flow of all itinerary folders, ordered by creation date.
     * Called by ItineraryViewModel.
     */
    fun getAllFolders(): Flow<List<ItineraryFolderEntity>> {
        return itineraryDao.getAllFolders()
    }

    /**
     * Gets a reactive flow of a single folder with all its items.
     * Called by ItineraryFolderViewModel.
     */
    fun getFolderWithItems(folderId: Int): Flow<ItineraryFolderWithItems?> {
        return itineraryDao.getFolderWithItems(folderId)
    }

    /**
     * Creates a new folder with the given title and description.
     * Called by ItineraryViewModel.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveNewFolder(title: String, description: String?) {
        val folder = ItineraryFolderEntity(
            title = title,
            description = description,
            createdAt = LocalDate.now()
        )
        itineraryDao.insertFolder(folder)
    }

    /**
     * Updates an existing folder.
     * Called by ItineraryViewModel.
     */
    suspend fun updateFolder(folder: ItineraryFolderEntity) {
        itineraryDao.updateFolder(folder)
    }

    /**
     * Deletes a folder.
     * Called by ItineraryViewModel and ItineraryFolderViewModel.
     */
    suspend fun deleteFolder(folder: ItineraryFolderEntity) {
        itineraryDao.deleteFolder(folder)
    }


    // --- Item Functions ---

    /**
     * --- THIS IS THE FIX ---
     * Inserts or updates an itinerary item.
     * If the item's ID is 0, it's a new item (insert).
     * Otherwise, it's an existing item (update).
     * Called by ItineraryFolderViewModel.
     */
    suspend fun upsertItem(item: ItineraryItemEntity) {
        if (item.id == 0) {
            itineraryDao.insertItem(item)
        } else {
            itineraryDao.updateItem(item)
        }
    }

    /**
     * Deletes an itinerary item.
     * Called by ItineraryFolderViewModel.
     */
    suspend fun deleteItem(item: ItineraryItemEntity) {
        itineraryDao.deleteItem(item)
    }
}