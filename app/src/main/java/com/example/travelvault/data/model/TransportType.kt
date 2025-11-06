// com.example.travelvault.data.model/TransportType.kt
package com.example.travelvault.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines the different modes of transport for a ticket.
 * Includes a helper to get a corresponding icon.
 */
enum class TransportType(val icon: ImageVector) {
    AIRPLANE(Icons.Default.Flight),
    TRAIN(Icons.Default.Train),
    BUS(Icons.Default.DirectionsBus),
    OTHER(Icons.Default.HelpOutline); // A good default

    // Helper to make the enum presentable in the dropdown
    override fun toString(): String {
        return name.lowercase().replaceFirstChar { it.titlecase() }
    }
}