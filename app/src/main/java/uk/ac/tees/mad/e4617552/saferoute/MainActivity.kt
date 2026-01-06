package uk.ac.tees.mad.e4617552.saferoute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import screens.ContactsScreen
import screens.HomeScreen
import screens.LoginScreen
import screens.SafeZonesScreen
import screens.SignUpScreen
import uk.ac.tees.mad.e4617552.saferoute.ui.theme.SafeRouteTheme
import screens.SosScreen
import com.google.android.gms.maps.model.LatLng
import screens.ProfileScreen



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


                    // home route
                    composable("home/{username}") { backStackEntry ->
                        val username = backStackEntry.arguments?.getString("username") ?: "User"
                        HomeScreen(navController, username)
                    }

                    // Buttons
                    composable("sos/{username}/{lat}/{lng}") { backStack ->
                        val user = backStack.arguments?.getString("username") ?: "User"
                        val lat = backStack.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
                        val lng = backStack.arguments?.getString("lng")?.toDoubleOrNull() ?: 0.0

                        SosScreen(
                            navController = navController,
                            username = user,
                            location = LatLng(lat, lng)
                        )

                    }

                    composable("safezones") {
                        SafeZonesScreen(navController = navController)
                    }
                    composable("contacts") {
                        ContactsScreen(navController = navController)
                    }
                    composable("profile") {
                        ProfileScreen(navController = navController)
                    }



                }
            }
        }
    }
}
