// com.example.travelvault.data.local/TicketDao.kt
package com.example.travelvault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travelvault.data.model.Ticket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    // Insert a new ticket. Replace if conflict (though auto-gen ID prevents this)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket)

    // Get all tickets, ordered by date (oldest first).
    // Flow is a Coroutine feature that emits updates whenever data changes.
    @Query("SELECT * FROM tickets ORDER BY travelDate ASC")
    fun getAllTickets(): Flow<List<Ticket>>

    // --- NEW FUNCTION ---
    // Add the delete function
    @Delete
    suspend fun deleteTicket(ticket: Ticket)
}