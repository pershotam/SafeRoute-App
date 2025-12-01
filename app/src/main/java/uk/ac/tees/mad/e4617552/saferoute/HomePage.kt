@file:OptIn(ExperimentalMaterial3Api::class)

package uk.ac.tees.mad.e4617552.saferoute

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomePage(navController: NavController, username: String) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SafeRoute",
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A1A3F)
                ),
                actions = {
                    IconButton(onClick = { /* Drawer later */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "We're with you,",
                color = Color.White,
                fontSize = 22.sp
            )

            Text(
                text = username.uppercase(),
                color = Color(0xFFD81B60),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            // BUTTON 1 - SOS
            BigPrimaryButton(
                text = "SOS",
                container = Color(0xFFE53935)
            ) { navController.navigate("sos") }

            Spacer(modifier = Modifier.height(12.dp))

            // BUTTON 2 - Safe Zones
            BigPrimaryButton(
                text = "Find Safe Zones",
                container = Color(0xFF0A1A3F)
            ) { navController.navigate("safezones") }

            Spacer(modifier = Modifier.height(12.dp))

            // BUTTON 3 - Emergency Contacts
            BigPrimaryButton(
                text = "Emergency Contacts",
                container = Color(0xFF0A1A3F)
            ) { navController.navigate("contacts") }
        }
    }
}

@Composable
fun BigPrimaryButton(text: String, container: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Color.White
        )
    ) {
        Text(text, fontSize = 18.sp)
    }
}
