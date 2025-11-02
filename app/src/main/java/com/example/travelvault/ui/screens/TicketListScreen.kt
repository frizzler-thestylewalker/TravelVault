// com.example.travelvault/ui/screens/TicketListScreen.kt
package com.example.travelvault.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.travelvault.R
import com.example.travelvault.data.model.Ticket
import com.example.travelvault.ui.components.WavyHeader
import com.example.travelvault.ui.viewmodel.TicketViewModel
import com.example.travelvault.ui.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TicketListScreen(
    viewModel: TicketViewModel,
    onNavigateToUpload: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val todayTickets by viewModel.todayTickets.collectAsState()
    val upcomingTickets by viewModel.upcomingTickets.collectAsState()
    val pastTickets by viewModel.pastTickets.collectAsState()

    val totalTickets = todayTickets.size + upcomingTickets.size + pastTickets.size

    var ticketToDelete by remember { mutableStateOf<Ticket?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                else -> {}
            }
        }
    }

    if (ticketToDelete != null) {
        DeleteConfirmationDialog(
            ticketName = ticketToDelete!!.routeName,
            onConfirm = {
                viewModel.deleteTicket(ticketToDelete!!)
                ticketToDelete = null
            },
            onDismiss = {
                ticketToDelete = null
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToUpload() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Ticket")
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            WavyHeader(
                height = 100.dp,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Travel Vault",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
            ) {

                if (totalTickets == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tickets found.\nTap the + button to add one.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        if (todayTickets.isNotEmpty()) {
                            item {
                                ListHeader(title = "Today's Trip")
                            }
                            items(todayTickets, key = { "today-${it.id}" }) { ticket ->
                                TicketItem(
                                    ticket = ticket,
                                    onDelete = { ticketToDelete = it },
                                    onOpen = { openTicketFile(context, it) } // <-- UPDATED
                                )
                            }
                        }

                        if (upcomingTickets.isNotEmpty()) {
                            item {
                                ListHeader(title = "Upcoming Trips")
                            }
                            items(upcomingTickets, key = { "upcoming-${it.id}" }) { ticket ->
                                TicketItem(
                                    ticket = ticket,
                                    onDelete = { ticketToDelete = it },
                                    onOpen = { openTicketFile(context, it) } // <-- UPDATED
                                )
                            }
                        }

                        if (pastTickets.isNotEmpty()) {
                            item {
                                ListHeader(title = "Trip Archive")
                            }
                            items(pastTickets, key = { "past-${it.id}" }) { ticket ->
                                TicketItem(
                                    ticket = ticket,
                                    onDelete = { ticketToDelete = it },
                                    onOpen = { openTicketFile(context, it) } // <-- UPDATED
                                )
                            }
                        }

                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun ListHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (title != "Trip Archive") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_swipe_left),
                    contentDescription = "Swipe hint",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Swipe to delete",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketItem(
    ticket: Ticket,
    onDelete: (Ticket) -> Unit,
    onOpen: (Ticket) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete(ticket)
                return@rememberSwipeToDismissBoxState false
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        content = {
            TicketCard(
                ticket = ticket,
                onClick = { onOpen(ticket) } // <-- UPDATED
            )
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    ticketName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Delete Ticket?") },
        text = { Text(text = "Are you sure you want to permanently delete the ticket for '$ticketName'?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TicketCard(
    ticket: Ticket,
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = ticket.routeName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = ticket.travelDate.format(formatter),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * --- UPDATED FUNCTION ---
 * Renamed to openTicketFile and now accepts the full Ticket object.
 */
private fun openTicketFile(context: Context, ticket: Ticket) {
    val file = File(ticket.pdfFilePath) // This field name is from V1, but it just means "file path"
    if (!file.exists()) {
        Toast.makeText(context, "Error: File not found.", Toast.LENGTH_SHORT).show()
        return
    }

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        // --- THIS IS THE KEY CHANGE ---
        // Set the data AND the mimeType.
        // Android will find the right app (PDF viewer or Gallery)
        setDataAndType(uri, ticket.fileMimeType)
        // --- END OF KEY CHANGE ---

        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app found to open this file type.", Toast.LENGTH_LONG).show()
    }
}