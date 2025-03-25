package com.example.drivingbehaviormonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.drivingbehaviormonitor.ui.theme.DrivingBehaviorMonitorTheme

// This is the starting point of our app!
// When the app launches, this activity gets created and sets up everything.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This makes the app use the whole screen (goes under the status bar too).
        enableEdgeToEdge()

        // This is where we tell the app: “Hey, show the UI using Jetpack Compose!”
        setContent {
            // We apply our app’s custom theme here so everything looks consistent.
            DrivingBehaviorMonitorTheme {
                // This is our navigation controller. Think of it as a mini-GPS for navigating between screens.
                val navController = rememberNavController()

                // This is where the navigation graph kicks in — it decides which screen to show.
                NavGraph(navController = navController)
            }
        }
    }
}
