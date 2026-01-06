@file:OptIn(ExperimentalMaterial3Api::class)

package screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.*

@SuppressLint("MissingPermission")
@Composable
fun SafeZonesScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {

    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()

    // Get current location
    LaunchedEffect(Unit) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location: Location? ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                currentLocation = latLng
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safe Zones") },
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

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            cameraPositionState = cameraPositionState
        ) {

            currentLocation?.let { userLoc ->

                // User location
                Marker(
                    state = MarkerState(position = userLoc),
                    title = "Your Location"
                )

                // SIMULATED MEANINGFUL SAFE ZONES
                val safeZones = listOf(
                    Triple(
                        "Police Station",
                        "Nearby police help",
                        LatLng(userLoc.latitude + 0.0012, userLoc.longitude + 0.0008)
                    ),
                    Triple(
                        "City Hospital",
                        "Emergency medical support",
                        LatLng(userLoc.latitude - 0.0010, userLoc.longitude - 0.0012)
                    ),
                    Triple(
                        "Public Market",
                        "Crowded & well-lit area",
                        LatLng(userLoc.latitude + 0.0020, userLoc.longitude - 0.0015)
                    )
                )

                safeZones.forEach { zone ->

                    Marker(
                        state = MarkerState(position = zone.third),
                        title = zone.first,
                        snippet = zone.second,
                        onClick = {

                            // Open Google Maps with route
                            val uri = (
                                    "https://www.google.com/maps/dir/" +
                                            "${userLoc.latitude},${userLoc.longitude}/" +
                                            "${zone.third.latitude},${zone.third.longitude}"
                                    ).toUri()

                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("com.google.android.apps.maps")
                            }

                            context.startActivity(intent)
                            true
                        }
                    )
                }
            }
        }
    }
}
