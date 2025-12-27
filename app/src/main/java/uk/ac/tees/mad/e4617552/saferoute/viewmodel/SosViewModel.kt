package uk.ac.tees.mad.e4617552.saferoute.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.ac.tees.mad.e4617552.saferoute.data.Contact

class SosViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    init {
        loadContacts()
    }

    fun loadContacts() {
        if (uid == null) return

        db.collection("users")
            .document(uid)
            .collection("contacts")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.map {
                        Contact(
                            id = it.id,
                            name = it.getString("name") ?: "",
                            phone = it.getString("phone") ?: ""
                        )
                    }
                    _contacts.value = list
                }
            }
    }

    fun sendSOS(userName: String, location: LatLng, selectedContacts: List<Contact>) {
        if (uid == null) return

        val data = mapOf(
            "userId" to uid,
            "name" to userName,
            "lat" to location.latitude,
            "lng" to location.longitude,
            "timestamp" to System.currentTimeMillis(),
            "sentTo" to selectedContacts.map { it.name }
        )

        db.collection("sos_alerts")
            .add(data)
    }

    fun buildSmsMessage(userName: String, location: LatLng): String {
        val googleMapsUrl =
            "https://www.google.com/maps?q=${location.latitude},${location.longitude}"

        return """
            🚨 SOS ALERT from $userName

            I am in danger. Please help me immediately.

            📍 My Location:
            Latitude: ${location.latitude}
            Longitude: ${location.longitude}

            👉 Google Maps Link:
            $googleMapsUrl

            Sent via SafeRoute App.
        """.trimIndent()
    }
}
