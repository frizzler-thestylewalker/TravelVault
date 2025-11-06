// com.example.travelvault.ui.viewmodel/ItineraryViewModel.kt
package com.example.travelvault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelvault.data.ItineraryRepository
import com.example.travelvault.data.model.ItineraryFolderEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the ItineraryListScreen.
 * Manages the list of all Itinerary Folders.
 */
class ItineraryViewModel(private val repository: ItineraryRepository) : ViewModel() {

    // StateFlow to hold the list of all folders, exposed to the UI
    private val _folders = MutableStateFlow<List<ItineraryFolderEntity>>(emptyList())
    val folders: StateFlow<List<ItineraryFolderEntity>> = _folders.asStateFlow()

    // SharedFlow for one-time UI events (e.g., navigation, snackbars)
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // As soon as the ViewModel is created, start collecting the flow of folders
        // from the repository and update the UI state.
        viewModelScope.launch {
            repository.getAllFolders().collect { folderList ->
                _folders.value = folderList
            }
        }
    }

    /**
     * Creates or updates an itinerary folder.
     * If [folderToEdit] is null, it creates a new folder.
     * If [folderToEdit] is not null, it updates the existing folder.
     */
    fun saveFolder(
        title: String,
        description: String?,
        folderToEdit: ItineraryFolderEntity?
    ) {
        if (title.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Title cannot be empty"))
            }
            return
        }

        viewModelScope.launch {
            try {
                if (folderToEdit == null) {
                    // Create new folder
                    repository.saveNewFolder(title, description)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Folder created"))
                } else {
                    // Update existing folder
                    val updatedFolder = folderToEdit.copy(
                        title = title,
                        description = description
                    )
                    repository.updateFolder(updatedFolder)
                    _eventFlow.emit(UiEvent.ShowSnackbar("Folder updated"))
                }
                _eventFlow.emit(UiEvent.DismissDialog) // Tell UI to close dialog
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }

    /**
     * Deletes a given itinerary folder.
     */
    fun deleteFolder(folder: ItineraryFolderEntity) {
        viewModelScope.launch {
            try {
                repository.deleteFolder(folder)
                _eventFlow.emit(UiEvent.ShowSnackbar("Folder deleted"))
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Error: ${e.message}"))
            }
        }
    }
}