// com.example.travelvault.ui.viewmodel/ItineraryFolderViewModel.kt
package com.example.travelvault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelvault.data.ItineraryRepository
import com.example.travelvault.data.model.ItineraryFolderWithItems
import com.example.travelvault.data.model.ItineraryItemEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

// Using the workaround constructor (no SavedStateHandle)
class ItineraryFolderViewModel(
    private val repository: ItineraryRepository
) : ViewModel() {

    // Private flow for the raw folder data
    private val _folderDetails = MutableStateFlow<ItineraryFolderWithItems?>(null)

    // Public flow for the folder title
    val folderTitle: StateFlow<String> = _folderDetails
        .filterNotNull()
        .map { it.folder.title }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Loading...")

    // Public flow for the grouped items
    val groupedItems: StateFlow<Map<LocalDate, List<ItineraryItemEntity>>> = _folderDetails
        .filterNotNull()
        .map {
            // Group items by date, then sort the items list by time (nulls last)
            it.items
                .sortedWith(compareBy(nullsLast()) { item -> item.time })
                .groupBy { item -> item.date }
                .toSortedMap() // Sort the dates
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Event flow for snackbars/dialogs
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // Function to load the data (called from AppNavigation)
    fun loadFolder(folderId: Int) {
        if (_folderDetails.value?.folder?.id == folderId) return
        viewModelScope.launch {
            repository.getFolderWithItems(folderId)
                .filterNotNull()
                .collect { folderWithItems ->
                    _folderDetails.value = folderWithItems
                }
        }
    }

    /**
     * Saves (inserts or updates) an itinerary item.
     */
    fun saveItem(
        date: LocalDate?, // This is nullable from the dialog
        time: String?,
        title: String,
        notes: String?,
        itemToEdit: ItineraryItemEntity?
    ) {
        val folderId = _folderDetails.value?.folder?.id
        // We already check for null date and blank title in the dialog,
        // but we check again here for safety.
        if (date == null || title.isBlank() || folderId == null) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Error: Date or Title is missing"))
            }
            return
        }

        viewModelScope.launch {
            try {
                // --- THIS IS THE FIX ---
                val itemToUpsert: ItineraryItemEntity
                if (itemToEdit == null) {
                    // Create NEW item, passing all required fields
                    itemToUpsert = ItineraryItemEntity(
                        folderId = folderId,
                        date = date,
                        time = time,
                        title = title,
                        notes = notes
                    )
                } else {
                    // Update EXISTING item
                    itemToUpsert = itemToEdit.copy(
                        date = date,
                        time = time,
                        title = title,
                        notes = notes,
                        folderId = folderId // Ensure folderId is correct
                    )
                }

                repository.upsertItem(itemToUpsert)
                _eventFlow.emit(UiEvent.ShowSnackbar(if (itemToEdit == null) "Item added" else "Item updated"))
                _eventFlow.emit(UiEvent.DismissDialog)
                // --- END OF FIX ---
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }

    /**
     * Deletes a given itinerary item.
     */
    fun deleteItem(item: ItineraryItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
            _eventFlow.emit(UiEvent.ShowSnackbar("Item deleted"))
        }
    }

    /**
     * Deletes the entire folder.
     * (This function isn't used by the detail screen yet, but is good to have)
     */
    fun deleteFolder() {
        _folderDetails.value?.folder?.let {
            viewModelScope.launch {
                repository.deleteFolder(it)
                // In a real app, we would also emit a navigation event here
                _eventFlow.emit(UiEvent.ShowSnackbar("Folder deleted"))
            }
        }
    }
}