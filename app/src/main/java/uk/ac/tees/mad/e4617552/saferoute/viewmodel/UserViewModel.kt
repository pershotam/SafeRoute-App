package uk.ac.tees.mad.e4617552.saferoute.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var currentUserName: String = ""
    var currentUserEmail: String = ""
    var currentUserId: String = ""

    // -------------------------------------------------------
    // 🔥 This function actually performs Firebase Login
    // -------------------------------------------------------
    fun doLogin(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

    // -------------------------------------------------------
    // 🔥 Save user details so other screens can access easily
    // -------------------------------------------------------
    fun setUserInfo(uid: String, name: String, email: String) {
        currentUserId = uid
        currentUserName = name
        currentUserEmail = email
    }
}