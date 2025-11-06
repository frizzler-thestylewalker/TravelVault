// com.example.travelvault.ui.viewmodel/UiEvent.kt
package com.example.travelvault.ui.viewmodel

/**
 * A sealed interface to define one-time UI events sent from the ViewModel.
 */
sealed interface UiEvent {
    // Event to show a snackbar message
    data class ShowSnackbar(val message: String) : UiEvent
    // Event to tell the screen to navigate back
    object NavigateBack : UiEvent
    // --- NEW EVENT ---
    // Event to tell the UI to dismiss any open dialog
    object DismissDialog : UiEvent
    // --- END NEW ---
}