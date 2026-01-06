@file:OptIn(ExperimentalMaterial3Api::class)

package screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import uk.ac.tees.mad.e4617552.saferoute.data.Contact
import uk.ac.tees.mad.e4617552.saferoute.viewmodel.SosViewModel

@Composable
fun SosScreen(
    navController: NavController,
    username: String,
    location: LatLng,
    viewModel: SosViewModel = viewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current

    val selectedContacts = remember { mutableStateListOf<Contact>() }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send SOS Alert") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
        ) {

            Text("Select emergency contacts:", color = Color.Gray)
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {
                items(contacts) { contact ->
                    val isSelected = contact in selectedContacts

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                if (isSelected) selectedContacts.remove(contact)
                                else selectedContacts.add(contact)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                Color(0xFFD81B60)
                            else
                                Color(0xFFEFEFEF)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = contact.name,
                                color = if (isSelected) Color.White else Color.Black
                            )
                            Text(
                                text = contact.phone,
                                color = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (selectedContacts.isNotEmpty()) {
                        showConfirmDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text("SEND SOS")
            }
        }
    }

    // -------- CONFIRMATION DIALOG --------
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm SOS Alert") },
            text = { Text("Are you sure you want to send SOS alert to selected contacts?") },
            confirmButton = {
                Button(onClick = {
                    showConfirmDialog = false

                    viewModel.sendSOS(username, location, selectedContacts)

                    val message = viewModel.buildSmsMessage(username, location)
                    val phoneNumbers = selectedContacts.joinToString(",") { it.phone }

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = "sms:$phoneNumbers".toUri()
                        putExtra("sms_body", message)
                    }

                    context.startActivity(intent)
                    showSuccessDialog = true
                }) {
                    Text("Yes, Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // -------- SUCCESS DIALOG --------
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("SOS Sent") },
            text = {
                Text(
                    "SOS alert saved in database & prepared for sending to:\n" +
                            selectedContacts.joinToString { it.name }
                )
            },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
