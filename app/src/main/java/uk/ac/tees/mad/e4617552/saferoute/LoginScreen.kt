package uk.ac.tees.mad.e4617552.saferoute

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // ⭐ FIX: Clean white input fields with proper borders
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = Color.White,
        focusedBorderColor = Color.White,
        unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
        focusedLabelColor = Color.White,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        cursorColor = Color.Black,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0A1A3F) // Navy Blue background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Login",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(12.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(errorMessage, color = Color(0xFFFF6B6B))
            }

            Spacer(modifier = Modifier.height(22.dp))

            // LOGIN BUTTON
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Please fill all fields."
                        return@Button
                    }

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener {

                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()

                            val uid = auth.currentUser?.uid
                            if (uid == null) {
                                Toast.makeText(context, "UID is null!", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }

                            db.collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener { doc ->
                                    val name = doc.getString("name") ?: "User"

                                    Toast.makeText(context, "Welcome $name!", Toast.LENGTH_SHORT).show()

                                    navController.navigate("home/$name") {
                                        popUpTo("login") { inclusive = true }
                                    }


                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Firestore Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }

                        }
                        .addOnFailureListener {
                            errorMessage = it.message ?: "Login failed"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD81B60) // Pink button
                )
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Don't have an account? Sign up", color = Color.White)
            }
        }
    }
}
