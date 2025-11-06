// com.example.travelvault.ui.screens.itinerary/ItineraryDialogs.kt
package com.example.travelvault.ui.screens.itinerary

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.travelvault.data.model.ItineraryFolderEntity
import com.example.travelvault.data.model.ItineraryItemEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * A dialog for adding or editing an Itinerary Folder (a trip).
 */
@Composable
fun AddEditFolderDialog(
    folder: ItineraryFolderEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String?) -> Unit
) {
    var title by remember { mutableStateOf(folder?.title ?: "") }
    var description by remember { mutableStateOf(folder?.description ?: "") }
    var titleError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (folder == null) "New Itinerary" else "Edit Itinerary") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = { Text("Title (e.g., Paris 2025)") },
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = "Title cannot be empty"
                    } else {
                        onSave(title, description.takeIf { it.isNotBlank() })
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * A dialog for adding or editing an Itinerary Item (an event).
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemDialog(
    item: ItineraryItemEntity?,
    onDismiss: () -> Unit,
    onSave: (date: LocalDate?, time: String?, title: String, notes: String?) -> Unit
) {
    var title by remember { mutableStateOf(item?.title ?: "") }
    var notes by remember { mutableStateOf(item?.notes ?: "") }
    var time by remember { mutableStateOf(item?.time ?: "") }
    var selectedDate by remember { mutableStateOf(item?.date) }

    var titleError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Set initial date for the picker
    val initialYear = selectedDate?.year ?: calendar.get(Calendar.YEAR)
    val initialMonth = selectedDate?.monthValue?.minus(1) ?: calendar.get(Calendar.MONTH)
    val initialDay = selectedDate?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            selectedDate = LocalDate.of(year, month + 1, day)
            dateError = null
        },
        initialYear, initialMonth, initialDay
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "New Item" else "Edit Item") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Date Picker Button
                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val dateText = selectedDate?.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
                        ?: "Select Date"
                    Text(dateText)
                }
                if (dateError != null) {
                    Text(dateError!!, color = MaterialTheme.colorScheme.error)
                }

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = { Text("Title (e.g., Flight to CDG)") },
                    isError = titleError != null,
                    supportingText = { titleError?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Time
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time (Optional, e.g., 10:00 AM)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isTitleBlank = title.isBlank()
                    val isDateNull = selectedDate == null

                    titleError = if (isTitleBlank) "Title cannot be empty" else null
                    dateError = if (isDateNull) "Please select a date" else null

                    if (!isTitleBlank && !isDateNull) {
                        onSave(
                            selectedDate,
                            time.takeIf { it.isNotBlank() },
                            title,
                            notes.takeIf { it.isNotBlank() }
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}