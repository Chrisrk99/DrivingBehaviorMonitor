package com.example.drivingbehaviormonitor.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.useLocationSpeed

// This is the SpeedScreen – it shows the car’s speed using the phone’s GPS!
// We need to ask for location permission and check if GPS is turned on.
// If something’s missing, we show a nice message to help the user.

@OptIn(ExperimentalMaterial3Api::class) // This lets us use some new Material3 features
@Composable
fun SpeedScreen(navController: NavController) { // NavController helps us move between screens
    // These "var" lines keep track of stuff that can change
    var hasLocationPermission by remember { mutableStateOf(false) } // Tracks if we have permission to use location
    var locationServicesEnabled by remember { mutableStateOf(false) } // Tracks if GPS is turned on
    val context = LocalContext.current // This grabs the app’s context (like its environment)

    // Here I check if GPS is enabled on the phone
    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(LocationManager::class.java) // Gets the phone’s location system
        locationServicesEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) // Checks if GPS is on
    }

    // This sets up a way to ask the user for location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission() // This is the permission request tool
    ) { isGranted ->
        hasLocationPermission = isGranted // Updates our permission status based on what the user says
    }

    // This checks if we already have permission or need to ask for it
    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION // Checks for fine location permission
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = true // We’re good to go!
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) // Pops up the permission request
        }
    }

    // Scaffold is like the basic frame for our screen
    Scaffold(
        // This creates a top bar with a title and a back button
        topBar = {
            TopAppBar(
                title = { Text("Speed Consistency") }, // The title at the top
                navigationIcon = {
                    // This adds a back button to go to the previous screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back") // Helps accessibility tools
                    }
                }
            )
        }
    ) { innerPadding -> // innerPadding keeps stuff from overlapping the top bar
        // Column stacks everything vertically on the screen
        Column(
            modifier = Modifier
                .padding(innerPadding) // Adds space so nothing bumps into the top bar
                .padding(24.dp) // Adds extra padding around the edges for a clean look
        ) {
            // This decides what to show based on permission and GPS status
            when {
                !hasLocationPermission -> {
                    // If we don’t have permission, we show this message
                    Text("Location permission required to display speed data.")
                }
                !locationServicesEnabled -> {
                    // If GPS is off, we ask the user to turn it on
                    Text("Please enable GPS in device settings to track speed.")
                }
                else -> {
                    // If everything’s good, we show the speed!
                    val speedMps = useLocationSpeed() // Gets raw speed in meters per second from GPS
                    val speedMph = speedMps * 2.23694f // Converts it to miles per hour (easier to read!)

                    Text("GPS Speed Data:") // A little header
                    Text("Speed: ${"%.2f".format(speedMph)} mph") // Shows speed rounded to 2 decimals
                    //Text("Raw (m/s): ${"%.2f".format(speedMps)}") // This is commented out but could show raw speed
                }
            }
        }
    }
}