package uk.ac.tees.mad.e4617552.saferoute.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uk.ac.tees.mad.e4617552.saferoute.data.Contact

class ContactsViewModel : ViewModel() {

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
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error fetching contacts", error)
                    return@addSnapshotListener
                }

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

    fun addContact(name: String, phone: String, onSuccess: () -> Unit, onFail: (String) -> Unit) {
        if (uid == null) return

        val newContact = mapOf("name" to name, "phone" to phone)

        db.collection("users")
            .document(uid)
            .collection("contacts")
            .add(newContact)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail(it.message ?: "Error adding contact") }
    }

    fun deleteContact(contactId: String, onSuccess: () -> Unit, onFail: (String) -> Unit) {
        if (uid == null) return

        db.collection("users")
            .document(uid)
            .collection("contacts")
            .document(contactId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFail(it.message ?: "Error deleting contact") }
    }
}
