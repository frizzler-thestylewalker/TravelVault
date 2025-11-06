// com.example.travelvault.data.model/ItineraryFolderEntity.kt
package com.example.travelvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents a single "trip" folder in the database.
 *
 * @param id The unique identifier for the folder.
 * @param title The name of the trip (e.g., "Paris 2025", "Summer Vacation").
 * @param description An optional description or date range (e.g., "Honeymoon", "June 10th - June 20th").
 * @param createdAt The date this folder was created, used for sorting.
 */
@Entity(tableName = "itinerary_folders")
data class ItineraryFolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String?,
    val createdAt: LocalDate
)