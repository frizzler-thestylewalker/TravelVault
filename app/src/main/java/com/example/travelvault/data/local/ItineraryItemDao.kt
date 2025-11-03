package com.example.travelvault.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travelvault.data.model.ItineraryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItineraryItem(item: ItineraryItem)

    @Delete
    suspend fun deleteItineraryItem(item: ItineraryItem)

    // Gets all itinerary items for a specific ticket,
    // ordered by date and then time.
    @Query("SELECT * FROM itinerary_items WHERE ticketId = :ticketId ORDER BY date ASC, time ASC")
    fun getItineraryForTicket(ticketId: Int): Flow<List<ItineraryItem>>
}
