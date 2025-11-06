// com.example.travelvault.data.local/ItineraryDao.kt
package com.example.travelvault.data.local

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.travelvault.data.model.ItineraryFolderEntity
import com.example.travelvault.data.model.ItineraryFolderWithItems
import com.example.travelvault.data.model.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Itinerary features.
 */
@Dao
interface ItineraryDao {

    // --- Folder Operations ---

    /**
     * Inserts a new Itinerary Folder.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: ItineraryFolderEntity)

    /**
     * Updates an existing Itinerary Folder.
     */
    @Update
    suspend fun updateFolder(folder: ItineraryFolderEntity)

    /**
     * Deletes an Itinerary Folder.
     * Note: Items associated with this folder will be deleted automatically
     * due to the 'onDelete = CASCADE' foreign key setting.
     */
    @Delete
    suspend fun deleteFolder(folder: ItineraryFolderEntity)

    /**
     * Gets a reactive [Flow] of all Itinerary Folders,
     * ordered by creation date (newest first).
     */
    @Query("SELECT * FROM itinerary_folders ORDER BY createdAt DESC")
    fun getAllFolders(): Flow<List<ItineraryFolderEntity>>

    /**
     * Gets a single folder by its ID.
     */
    @Query("SELECT * FROM itinerary_folders WHERE id = :folderId")
    suspend fun getFolderById(folderId: Int): ItineraryFolderEntity?


    // --- Item Operations ---

    /**
     * Inserts a new Itinerary Item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItineraryItemEntity)

    /**
     * Updates an existing Itinerary Item.
     */
    @Update
    suspend fun updateItem(item: ItineraryItemEntity)

    /**
     * Deletes an Itinerary Item.
     */
    @Delete
    suspend fun deleteItem(item: ItineraryItemEntity)

    /**
     * Gets a single item by its ID.
     */
    @Query("SELECT * FROM itinerary_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): ItineraryItemEntity?


    // --- Relational Query ---

    /**
     * Gets a single folder and all its associated items as a reactive [Flow].
     * The [Transaction] annotation ensures this is executed atomically.
     * The items will be sorted by date, then by time.
     */
    @Transaction
    @Query("SELECT * FROM itinerary_folders WHERE id = :folderId")
    fun getFolderWithItems(folderId: Int): Flow<ItineraryFolderWithItems?>

}