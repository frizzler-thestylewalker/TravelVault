// com.example.travelvault.data/TicketRepository.kt
package com.example.travelvault.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.travelvault.data.local.ItineraryItemDao // <-- ADDED IMPORT
import com.example.travelvault.data.local.TicketDao
import com.example.travelvault.data.model.ItineraryItem // <-- ADDED IMPORT
import com.example.travelvault.data.model.Ticket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate

// --- UPDATED CONSTRUCTOR ---
class TicketRepository(
    private val ticketDao: TicketDao,
    private val itineraryItemDao: ItineraryItemDao, // <-- ADDED
    private val context: Context
) {

    // --- TICKET FUNCTIONS ---

    fun getAllTickets(): Flow<List<Ticket>> {
        return ticketDao.getAllTickets()
    }

    suspend fun saveNewTicket(
        routeName: String,
        travelDate: LocalDate,
        fileUri: Uri,
        mimeType: String?
    ) {
        if (mimeType == null) {
            throw IOException("File type could not be determined.")
        }

        withContext(Dispatchers.IO) {
            val internalFilePath = copyFileToInternalStorage(fileUri, mimeType)
            val newTicket = Ticket(
                routeName = routeName,
                travelDate = travelDate,
                pdfFilePath = internalFilePath,
                fileMimeType = mimeType
            )
            ticketDao.insertTicket(newTicket)
        }
    }

    suspend fun deleteTicket(ticket: Ticket) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(ticket.pdfFilePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Deleting the ticket will also delete all its
            // itinerary items because of the "onDelete = CASCADE" rule.
            ticketDao.deleteTicket(ticket)
        }
    }

    private fun copyFileToInternalStorage(fileUri: Uri, mimeType: String): String {
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "file"
        val filename = "ticket_${System.currentTimeMillis()}.$fileExtension"
        val ticketsDir = File(context.filesDir, "tickets").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val destinationFile = File(ticketsDir, filename)

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
            val outputStream = FileOutputStream(destinationFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Failed to copy file to internal storage", e)
        }
    }

    // --- ITINERARY FUNCTIONS (ALL NEW) ---

    /**
     * Gets a flow of all itinerary items for a specific ticket.
     */
    fun getItineraryForTicket(ticketId: Int): Flow<List<ItineraryItem>> {
        return itineraryItemDao.getItineraryForTicket(ticketId)
    }

    /**
     * Saves a new or updated itinerary item.
     */
    suspend fun saveItineraryItem(item: ItineraryItem) {
        withContext(Dispatchers.IO) {
            itineraryItemDao.insertItineraryItem(item)
        }
    }

    /**
     * Deletes a specific itinerary item.
     */
    suspend fun deleteItineraryItem(item: ItineraryItem) {
        withContext(Dispatchers.IO) {
            itineraryItemDao.deleteItineraryItem(item)
        }
    }
}
