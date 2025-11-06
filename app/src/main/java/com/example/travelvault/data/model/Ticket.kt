// com.example.travelvault.data.model/Ticket.kt
package com.example.travelvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val routeName: String,

    val travelDate: LocalDate,

    val pdfFilePath: String, // This just means "file path"

    val fileMimeType: String,

    // --- NEW FIELD ---
    // This will store the mode of transport (e.g., AIRPLANE, TRAIN)
    // We'll give it a default value so existing tickets don't break.
    val transportType: TransportType = TransportType.OTHER
    // --- END NEW FIELD ---
)