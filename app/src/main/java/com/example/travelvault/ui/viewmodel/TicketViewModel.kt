// com.example.travelvault.ui.viewmodel/TicketViewModel.kt
package com.example.travelvault.ui.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelvault.data.TicketRepository
import com.example.travelvault.data.model.ItineraryItem // <-- ADDED IMPORT
import com.example.travelvault.data.model.Ticket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime // <-- ADDED IMPORT

@RequiresApi(Build.VERSION_CODES.O)
class TicketViewModel(private val repository: TicketRepository) : ViewModel() {

    // --- Ticket StateFlows ---
    private val _todayTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val todayTickets: StateFlow<List<Ticket>> = _todayTickets.asStateFlow()

    private val _upcomingTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val upcomingTickets: StateFlow<List<Ticket>> = _upcomingTickets.asStateFlow()

    private val _pastTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val pastTickets: StateFlow<List<Ticket>> = _pastTickets.asStateFlow()

    // --- Event Flow ---
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.getAllTickets().collect { allTickets ->
                val today = LocalDate.now()

                _todayTickets.value = allTickets.filter { it.travelDate == today }

                _upcomingTickets.value = allTickets
                    .filter { it.travelDate > today }
                    .sortedBy { it.travelDate }

                _pastTickets.value = allTickets
                    .filter { it.travelDate < today }
                    .sortedByDescending { it.travelDate }
            }
        }
    }

    // --- TICKET FUNCTIONS ---

    fun saveNewTicket(
        routeName: String,
        travelDate: LocalDate?,
        fileUri: Uri?,
        mimeType: String?
    ) {
        if (routeName.isBlank() || travelDate == null || fileUri == null || mimeType == null) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please fill all fields and select a file"))
            }
            return
        }

        viewModelScope.launch {
            try {
                repository.saveNewTicket(routeName, travelDate, fileUri, mimeType)
                _eventFlow.emit(UiEvent.ShowSnackbar("Ticket Saved!"))
                _eventFlow.emit(UiEvent.NavigateBack)
            } catch (e: Exception) {
                e.printStackTrace()
                _eventFlow.emit(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }

    fun deleteTicket(ticket: Ticket) {
        viewModelScope.launch {
            try {
                repository.deleteTicket(ticket)
                _eventFlow.emit(UiEvent.ShowSnackbar("Ticket deleted"))
            } catch (e: Exception) {
                e.printStackTrace()
                _eventFlow.emit(UiEvent.ShowSnackbar("Error deleting ticket"))
            }
        }
    }

    // --- ITINERARY FUNCTIONS (ALL NEW) ---

    // Holds the list of itinerary items for the *currently viewed* ticket
    private val _itineraryItems = MutableStateFlow<List<ItineraryItem>>(emptyList())
    val itineraryItems: StateFlow<List<ItineraryItem>> = _itineraryItems.asStateFlow()

    /**
     * Gets all itinerary items for a specific ticket and updates the flow.
     * This will be called when the user navigates to the itinerary screen.
     */
    fun getItineraryForTicket(ticketId: Int) {
        viewModelScope.launch {
            repository.getItineraryForTicket(ticketId)
                .collect { items ->
                    _itineraryItems.value = items
                }
        }
    }

    /**
     * Saves a new itinerary item.
     */
    fun saveItineraryItem(
        ticketId: Int,
        date: LocalDate,
        time: LocalTime,
        title: String,
        notes: String?,
        location: String?
    ) {
        if (title.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please enter a title"))
            }
            return
        }

        val newItem = ItineraryItem(
            ticketId = ticketId,
            date = date,
            time = time,
            title = title,
            notes = notes,
            location = location
        )

        viewModelScope.launch {
            repository.saveItineraryItem(newItem)
            _eventFlow.emit(UiEvent.ShowSnackbar("Itinerary item saved"))
        }
    }

    /**
     * Deletes an itinerary item.
     */
    fun deleteItineraryItem(item: ItineraryItem) {
        viewModelScope.launch {
            repository.deleteItineraryItem(item)
            _eventFlow.emit(UiEvent.ShowSnackbar("Itinerary item deleted"))
        }
    }
}
