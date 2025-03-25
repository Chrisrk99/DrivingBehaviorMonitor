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

// SpeedScreen shows the current GPS-based driving speed.
// We request location permissions and show a friendly message if GPS is off.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeedScreen(navController: NavController) {
    var hasLocationPermission by remember { mutableStateOf(false) }
    var locationServicesEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Check if GPS/location services are enabled on the device/emulator
    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(LocationManager::class.java)
        locationServicesEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // Handle location permission request if needed
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            hasLocationPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Speed Consistency") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            when {
                !hasLocationPermission -> {
                    Text("Location permission required to display speed data.")
                }
                !locationServicesEnabled -> {
                    Text("Please enable GPS in device settings to track speed.")
                }
                else -> {
                    val speedMps = useLocationSpeed()        // raw speed in meters per second
                    val speedMph = speedMps * 2.23694f       // convert to miles per hour

                    Text("GPS Speed Data:")
                    Text("Speed: ${"%.2f".format(speedMph)} mph")
                    //Text("Raw (m/s): ${"%.2f".format(speedMps)}")
                }
            }
        }
    }
}
