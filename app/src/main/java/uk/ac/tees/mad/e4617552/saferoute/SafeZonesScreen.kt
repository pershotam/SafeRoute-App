package uk.ac.tees.mad.e4617552.saferoute

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.tasks.CancellationTokenSource

@SuppressLint("MissingPermission")
@Composable
fun SafeZonesScreen(context: Context = LocalContext.current) {

    Surface(modifier = Modifier.fillMaxSize()) {

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        var currentLocation by remember { mutableStateOf<LatLng?>(null) }
        val cameraPositionState = rememberCameraPositionState()

        // ✅ Request *live* current location
        LaunchedEffect(Unit) {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    currentLocation = latLng
                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }

        // 🗺️ Show Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            currentLocation?.let { loc ->
                // Current user location marker
                Marker(
                    state = MarkerState(position = loc),
                    title = "You are here"
                )

                // Example nearby safe zones
                val nearbySafeZones = listOf(
                    LatLng(loc.latitude + 0.001, loc.longitude + 0.001),
                    LatLng(loc.latitude - 0.001, loc.longitude - 0.001),
                    LatLng(loc.latitude + 0.002, loc.longitude - 0.002)
                )
                nearbySafeZones.forEachIndexed { index, zone ->
                    Marker(
                        state = MarkerState(position = zone),
                        title = "Safe Zone ${index + 1}",
                        snippet = "Nearby police/hospital"
                    )
                }
            }
        }
    }
}
