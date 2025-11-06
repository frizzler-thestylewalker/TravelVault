// com.example.travelvault.data.model/ItineraryItemEntity.kt
package com.example.travelvault.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents a single event or item within an ItineraryFolder.
 *
 * @param id The unique identifier for the item.
 * @param folderId The ID of the [ItineraryFolderEntity] this item belongs to.
 * @param date The date of the event.
 * @param time An optional time for the event (e.g., "10:00 AM", "8:00 PM").
 * @param title The name of the event (e.g., "Flight to CDG", "Dinner at Eiffel Tower").
 * @param notes An optional field for details, location, confirmation numbers, etc.
 */
@Entity(
    tableName = "itinerary_items",
    // Define the foreign key relationship
    foreignKeys = [
        ForeignKey(
            entity = ItineraryFolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            // When a folder is deleted, all its items are also deleted.
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Create an index on folderId to speed up queries
    indices = [Index(value = ["folderId"])]
)
data class ItineraryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val folderId: Int,
    val date: LocalDate,
    val time: String?,
    val title: String,
    val notes: String?
)