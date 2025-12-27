package screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import uk.ac.tees.mad.e4617552.saferoute.viewmodel.SosViewModel
import uk.ac.tees.mad.e4617552.saferoute.data.Contact

@Composable
fun SosScreen(
    username: String,
    location: LatLng,
    viewModel: SosViewModel = viewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current

    val selectedContacts = remember { mutableStateListOf<Contact>() }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.padding(20.dp)) {

            Text("Send SOS Alert", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(20.dp))

            Text("Select emergency contacts:", color = Color.Gray)
            Spacer(Modifier.height(10.dp))

            LazyColumn {
                items(contacts) { contact ->
                    val isSelected = selectedContacts.contains(contact)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                if (isSelected) selectedContacts.remove(contact)
                                else selectedContacts.add(contact)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFD81B60) else Color(0xFFEFEFEF)
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                contact.name,
                                color = if (isSelected) Color.White else Color.Black
                            )
                            Text(
                                contact.phone,
                                color = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (selectedContacts.isNotEmpty()) showConfirmDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFFE53935))
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

                    // store inside Firestore
                    viewModel.sendSOS(username, location, selectedContacts)

                    // now open SMS app
                    val message = viewModel.buildSmsMessage(username, location)

                    val phoneNumbers = selectedContacts.joinToString(",") { it.phone }

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("sms:$phoneNumbers")
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
                    "SOS alert saved in database & prepared for sending to: " +
                            selectedContacts.joinToString { it.name }
                )
            },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false }) { Text("OK") }
            }
        )
    }
}
