// com.example.travelvault.data.model/ItineraryFolderWithItems.kt
package com.example.travelvault.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * A data class to hold the result of a query joining an ItineraryFolder
 * with all of its ItineraryItems.
 */
data class ItineraryFolderWithItems(
    @Embedded
    val folder: ItineraryFolderEntity,

    @Relation(
        parentColumn = "id", // The ID from ItineraryFolderEntity
        entityColumn = "folderId" // The matching ID from ItineraryItemEntity
    )
    val items: List<ItineraryItemEntity>
)