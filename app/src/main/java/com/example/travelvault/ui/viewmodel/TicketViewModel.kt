// com.example.travelvault.ui.viewmodel/TicketViewModel.kt
package com.example.travelvault.ui.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelvault.data.TicketRepository
import com.example.travelvault.data.model.Ticket
import com.example.travelvault.data.model.TransportType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class TicketViewModel(private val repository: TicketRepository) : ViewModel() {

    // --- No changes to these StateFlows ---
    private val _todayTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val todayTickets: StateFlow<List<Ticket>> = _todayTickets.asStateFlow()

    private val _upcomingTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val upcomingTickets: StateFlow<List<Ticket>> = _upcomingTickets.asStateFlow()

    private val _pastTickets = MutableStateFlow<List<Ticket>>(emptyList())
    val pastTickets: StateFlow<List<Ticket>> = _pastTickets.asStateFlow()

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

    /**
     * --- UPDATED FUNCTION ---
     * Now accepts a TransportType.
     */
    fun saveNewTicket(
        routeName: String,
        travelDate: LocalDate?, // Make nullable for validation
        fileUri: Uri?, // Make nullable for validation
        mimeType: String?, // Add mimeType
        transportType: TransportType? // <-- NEW PARAMETER
    ) {
        // --- UPDATED VALIDATION ---
        if (routeName.isBlank() || travelDate == null || fileUri == null || mimeType == null || transportType == null) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Please fill all fields, select a file, and choose a transport type"))
            }
            return
        }
        // --- END UPDATED VALIDATION ---

        viewModelScope.launch {
            try {
                // --- UPDATED REPOSITORY CALL ---
                repository.saveNewTicket(routeName, travelDate, fileUri, mimeType, transportType)

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
}