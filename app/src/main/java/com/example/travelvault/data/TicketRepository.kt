// com.example.travelvault.data/TicketRepository.kt
package com.example.travelvault.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.travelvault.data.local.TicketDao
import com.example.travelvault.data.model.Ticket
import com.example.travelvault.data.model.TransportType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate

class TicketRepository(
    private val ticketDao: TicketDao,
    private val context: Context
) {

    fun getAllTickets(): Flow<List<Ticket>> {
        return ticketDao.getAllTickets()
    }

    /**
     * --- UPDATED FUNCTION ---
     * Now accepts a TransportType.
     */
    suspend fun saveNewTicket(
        routeName: String,
        travelDate: LocalDate,
        fileUri: Uri,
        mimeType: String?,
        transportType: TransportType // <-- NEW PARAMETER
    ) {
        // Basic validation
        if (mimeType == null) {
            throw IOException("File type could not be determined.")
        }

        withContext(Dispatchers.IO) {

            // 1. Copy the file and get its new, secure path
            val internalFilePath = copyFileToInternalStorage(fileUri, mimeType)

            // 2. Create the Ticket object with the new path AND mimeType
            val newTicket = Ticket(
                routeName = routeName,
                travelDate = travelDate,
                pdfFilePath = internalFilePath,
                fileMimeType = mimeType,
                transportType = transportType // <-- SAVE THE NEW FIELD
            )

            // 3. Save the ticket metadata to the Room database
            ticketDao.insertTicket(newTicket)
        }
    }

    suspend fun deleteTicket(ticket: Ticket) {
        withContext(Dispatchers.IO) {
            // This logic works for any file, not just PDFs, so no change is needed.
            try {
                val file = File(ticket.pdfFilePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            ticketDao.deleteTicket(ticket)
        }
    }

    /**
     * --- No changes to this function ---
     * Renamed from 'copyPdfToInternalStorage'.
     * Now uses MimeTypeMap to get the correct file extension.
     */
    private fun copyFileToInternalStorage(fileUri: Uri, mimeType: String): String {

        // Use Android's MimeTypeMap to get the official extension
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "file"

        // Create a unique filename with the correct extension
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
}