package uk.ac.tees.mad.e4617552.saferoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import uk.ac.tees.mad.e4617552.saferoute.ui.theme.SafeRouteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {

            SafeRouteTheme {

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {

                    composable("splash") { SplashScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("signup") { SignUpScreen(navController) }


                    // ✅ Updated home route with username argument
                    composable("home/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: "User"
                        HomePage(navController, username)
                    }

                    // Buttons
                    composable("sos") { SosScreen() }
                    composable("safezones") { SafeZonesScreen() }
                    composable("contacts") { ContactsScreen() }
                }
            }
        }
    }
}
