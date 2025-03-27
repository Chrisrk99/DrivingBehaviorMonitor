package com.example.drivingbehaviormonitor

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.example.drivingbehaviormonitor.ui.theme.DrivingBehaviorMonitorTheme

class MainActivity : ComponentActivity() {

    // Register for permission result callback
    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Location permission is required to show weather info!", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ask for location permission when app launches
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        enableEdgeToEdge()

        setContent {
            DrivingBehaviorMonitorTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
