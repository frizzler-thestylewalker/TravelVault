// com.example.travelvault.ui.screens/UploadScreen.kt
package com.example.travelvault.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.travelvault.data.model.TransportType
import com.example.travelvault.ui.viewmodel.TicketViewModel
import com.example.travelvault.ui.viewmodel.UiEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * The screen for adding a new ticket.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    viewModel: TicketViewModel,
    onTicketSaved: () -> Unit // This function is passed from AppNavigation
) {
    val context = LocalContext.current

    // --- State variables for the form (UPDATED) ---
    var routeName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileMimeType by remember { mutableStateOf<String?>(null) }
    var selectedFileName by remember { mutableStateOf("No file selected") }

    // --- NEW STATE for Transport Type Dropdown ---
    var selectedTransportType by remember { mutableStateOf<TransportType?>(null) }
    var isTransportDropdownExpanded by remember { mutableStateOf(false) }
    val transportTypes = TransportType.entries.toList()
    // --- END NEW STATE ---

    // --- Date Picker Logic (No change) ---
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            selectedDate = LocalDate.of(year, month + 1, day)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    // --- File Picker Logic (No change) ---
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                selectedFileUri = uri
                selectedFileMimeType = context.contentResolver.getType(uri)
                selectedFileName = getFileName(context, uri) ?: "File selected"
            }
        }
    )

    // --- Listen for UI Events (No change) ---
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.NavigateBack -> {
                    onTicketSaved()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Ticket") },
                navigationIcon = {
                    IconButton(onClick = { onTicketSaved() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. Route Name Input (No change)
            OutlinedTextField(
                value = routeName,
                onValueChange = { routeName = it },
                label = { Text("Route (e.g., Mumbai to Goa)") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- 2. NEW: Transport Type Dropdown ---
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = isTransportDropdownExpanded,
                    onExpandedChange = { isTransportDropdownExpanded = !isTransportDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedTransportType?.toString() ?: "Select Transport Type",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Transport Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = isTransportDropdownExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isTransportDropdownExpanded,
                        onDismissRequest = { isTransportDropdownExpanded = false }
                    ) {
                        transportTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = type.icon,
                                            contentDescription = null,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(text = type.toString())
                                    }
                                },
                                onClick = {
                                    selectedTransportType = type
                                    isTransportDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 3. Date Picker Button (No change)
            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                val dateText = selectedDate?.format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
                    ?: "Select Travel Date"
                Text(dateText)
            }

            // 4. File Picker Button (No change)
            Button(
                onClick = {
                    filePickerLauncher.launch(arrayOf("application/pdf", "image/*"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select PDF or Image")
            }
            Text(
                text = selectedFileName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.weight(1f))

            // --- 5. UPDATED: Save Button ---
            Button(
                onClick = {
                    viewModel.saveNewTicket(
                        routeName,
                        selectedDate,
                        selectedFileUri,
                        selectedFileMimeType,
                        selectedTransportType // <-- Pass the new value
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                // Updated enabled check
                enabled = routeName.isNotBlank() && selectedDate != null && selectedFileUri != null && selectedFileMimeType != null && selectedTransportType != null
            ) {
                Text("Save Ticket", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * --- Helper Function (No change) ---
 * Gets the display name of a file from its content Uri.
 */
private fun getFileName(context: Context, uri: Uri): String? {
    var fileName: String? = null
    if (uri.scheme == "content") {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
    }
    if (fileName == null) {
        fileName = uri.path
        val cut = fileName?.lastIndexOf('/')
        if (cut != -1) {
            fileName = fileName?.substring(cut!! + 1)
        }
    }
    return fileName
}