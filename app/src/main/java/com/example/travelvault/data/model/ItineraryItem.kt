package com.example.travelvault.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "itinerary_items",
    // This creates a "foreign key" relationship.
    // It links each ItineraryItem to a parent Ticket.
    // If a Ticket is deleted, all its ItineraryItems will also be deleted.
    foreignKeys = [
        ForeignKey(
            entity = Ticket::class,
            parentColumns = ["id"],
            childColumns = ["ticketId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // This index makes looking up items by ticketId faster.
    indices = [Index("ticketId")]
)
data class ItineraryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // The ID of the ticket this item belongs to
    val ticketId: Int,

    val date: LocalDate,
    val time: LocalTime,
    val title: String,
    val notes: String? = null,
    val location: String? = null
)
