package screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import uk.ac.tees.mad.e4617552.saferoute.viewmodel.ContactsViewModel

@Composable
fun ContactsScreen(viewModel: ContactsViewModel = viewModel()) {

    val contacts by viewModel.contacts.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var deleteContactId by remember { mutableStateOf("") }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.padding(20.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Emergency Contacts", style = MaterialTheme.typography.headlineSmall)

                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Contact", tint = Color(0xFFD81B60))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Contact List
            LazyColumn {
                items(contacts) { contact ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(contact.name, fontSize = 18.sp, color = Color.Black)
                                Text(contact.phone, color = Color.DarkGray)
                            }

                            IconButton(onClick = {
                                deleteContactId = contact.id
                                showDeleteDialog = true
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        // ADD CONTACT DIALOG
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                confirmButton = {
                    Button(onClick = {
                        if (name.isNotEmpty() && phone.isNotEmpty()) {
                            viewModel.addContact(
                                name,
                                phone,
                                onSuccess = {
                                    Toast.makeText(context, "Contact added!", Toast.LENGTH_SHORT).show()
                                    name = ""
                                    phone = ""
                                    showAddDialog = false
                                },
                                onFail = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                },
                title = { Text("Add Emergency Contact") },
                text = {
                    Column {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
                    }
                }
            )
        }

        // DELETE CONFIRMATION
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    Button(onClick = {
                        viewModel.deleteContact(
                            deleteContactId,
                            onSuccess = {
                                Toast.makeText(context, "Contact deleted!", Toast.LENGTH_SHORT).show()
                                showDeleteDialog = false
                            },
                            onFail = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
                },
                title = { Text("Delete Contact") },
                text = { Text("Are you sure you want to delete this contact?") }
            )
        }
    }
}
