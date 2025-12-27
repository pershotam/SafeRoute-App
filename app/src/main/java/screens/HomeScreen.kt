@file:OptIn(ExperimentalMaterial3Api::class)

package screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import uk.ac.tees.mad.e4617552.saferoute.viewmodel.LocationViewModel

// ------------------- REUSABLE BUTTON -------------------
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

// ------------------- DRAWER ITEM -------------------
@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = Color.White, fontSize = 16.sp)
    }
}

// ------------------- HOME SCREEN -------------------
@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(navController: NavController, username: String) {

    // ⭐ Get ViewModel once
    val locationViewModel: LocationViewModel = viewModel()

    // ⭐ Observing live location text + LatLng
    val currentLocation by locationViewModel.location.collectAsState()
    val locationText by locationViewModel.locationText.collectAsState()

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ---------- PERMISSION ----------
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->

        if (granted) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { loc ->
                if (loc != null) {
                    locationViewModel.updateLocation(LatLng(loc.latitude, loc.longitude))
                } else {
                    locationViewModel.updateText("Unable to get location")
                }
            }
        } else {
            locationViewModel.updateText("Permission denied")
        }
    }

    // Request permission or retrieve last known location
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { loc ->
                if (loc != null) {
                    locationViewModel.updateLocation(LatLng(loc.latitude, loc.longitude))
                }
            }
        }
    }

    // Animate map camera when location updates
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1200
            )
        }
    }

    // ------------------- RIGHT SIDE DRAWER -------------------
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(260.dp)
                        .align(Alignment.CenterEnd),
                    color = Color(0xFF0A1A3F)
                ) {
                    Column(Modifier.padding(20.dp)) {

                        Text("Welcome,", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                        Text(username.uppercase(), color = Color(0xFFD81B60), fontSize = 22.sp,
                            fontWeight = FontWeight.Bold)

                        Spacer(Modifier.height(20.dp))
                        Divider(color = Color.White.copy(alpha = 0.3f))
                        Spacer(Modifier.height(20.dp))

                        DrawerItem("Profile") { scope.launch { drawerState.close() } }
                        DrawerItem("Home") { scope.launch { drawerState.close() } }
                        DrawerItem("SOS Alert") {
                            scope.launch { drawerState.close() }
                            navController.navigate("sos")
                        }
                        DrawerItem("Safe Zones") {
                            scope.launch { drawerState.close() }
                            navController.navigate("safezones")
                        }
                        DrawerItem("Emergency Contacts") {
                            scope.launch { drawerState.close() }
                            navController.navigate("contacts")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        DrawerItem("Logout") {
                            scope.launch { drawerState.close() }
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    ) {

        // ------------------- MAIN CONTENT -------------------
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "SafeRoute",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(Color(0xFF0A1A3F)),
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    }
                )
            }
        ) { padding ->

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("We're with you,", color = Color.White, fontSize = 22.sp)
                Text(username.uppercase(), color = Color(0xFFD81B60),
                    fontSize = 28.sp, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(10.dp))

                Text(locationText, color = Color.LightGray, fontSize = 14.sp)

                Spacer(Modifier.height(16.dp))

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        currentLocation?.let {
                            Marker(MarkerState(position = it), title = "You are here")
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                BigPrimaryButton("Alert", Color(0xFFE53935)) {

                    val lat = currentLocation?.latitude ?: 0.0
                    val lng = currentLocation?.longitude ?: 0.0

                    navController.navigate("sos/$username/$lat/$lng")
                }


                Spacer(Modifier.height(12.dp))

                BigPrimaryButton("Find Safe Zones", Color(0xFF0A1A3F)) {
                    navController.navigate("safezones")
                }

                Spacer(Modifier.height(12.dp))

                BigPrimaryButton("Emergency Contacts", Color(0xFF0A1A3F)) {
                    navController.navigate("contacts")
                }
            }
        }
    }
}
