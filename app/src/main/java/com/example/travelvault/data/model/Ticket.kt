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

    // We'll rename this in our minds to "filePath"
    val pdfFilePath: String,

    // --- NEW FIELD ---
    // This will store "application/pdf", "image/jpeg", etc.
    val fileMimeType: String
    // --- END NEW FIELD ---
)