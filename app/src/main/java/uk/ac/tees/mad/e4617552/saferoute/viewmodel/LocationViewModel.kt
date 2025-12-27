package uk.ac.tees.mad.e4617552.saferoute.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {

    private val _location = MutableStateFlow<LatLng?>(null)
    val location: StateFlow<LatLng?> = _location

    private val _locationText = MutableStateFlow("Fetching location...")
    val locationText: StateFlow<String> = _locationText

    fun updateLocation(newLocation: LatLng?) {
        _location.value = newLocation
        if (newLocation != null) {
            _locationText.value = "Lat: ${newLocation.latitude}, Lng: ${newLocation.longitude}"
        }
    }

    // 🔥 THIS WAS MISSING (fixes your error)
    fun updateText(text: String) {
        _locationText.value = text
    }
}