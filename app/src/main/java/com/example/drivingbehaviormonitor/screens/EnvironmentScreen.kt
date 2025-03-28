package com.example.drivingbehaviormonitor.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.getLastKnownLocation
import com.example.drivingbehaviormonitor.utils.getRoadTypeDescription
import com.example.drivingbehaviormonitor.utils.getTrafficFlowDescription
import com.example.drivingbehaviormonitor.utils.getWeatherDescription
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentScreen(navController: NavController) {
    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var weatherDescription by remember { mutableStateOf<String?>(null) }
    var trafficCondition by remember { mutableStateOf<String?>(null) }
    var roadType by remember { mutableStateOf<String?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    var locationServicesEnabled by remember { mutableStateOf(true) }

    val context = LocalContext.current

    // Checks if GPS/location services are enabled on the device
    fun isLocationServicesEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // Ask for location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
    }

    // Initial permission check
    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        permissionGranted = granted

        if (!granted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        locationServicesEnabled = isLocationServicesEnabled(context)
    }

    // Live clock
    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = LocalDateTime.now()
            delay(1000)
        }
    }

    // Location-dependent data
    LaunchedEffect(permissionGranted, locationServicesEnabled) {
        if (permissionGranted && locationServicesEnabled) {
            val location = getLastKnownLocation(context)
            if (location != null) {
                weatherDescription = getWeatherDescription(location.latitude, location.longitude)
                trafficCondition = getTrafficFlowDescription(location.latitude, location.longitude)
                roadType = getRoadTypeDescription(location.latitude, location.longitude)
            } else {
                weatherDescription = "Unable to fetch location"
                trafficCondition = "Unable to fetch traffic data"
                roadType = "Unable to fetch road type"
            }
        } else if (!locationServicesEnabled) {
            weatherDescription = "Location services are disabled"
            trafficCondition = "Cannot determine traffic without GPS"
            roadType = "Cannot determine road type without GPS"
        }
    }

    val dayOfWeek = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val formattedTime = currentDateTime.toLocalTime().format(timeFormatter)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Environmental & Contextual Metrics") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Text("Current Day of Week: $dayOfWeek")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Current Time of Day: $formattedTime")
            Spacer(modifier = Modifier.height(8.dp))
            weatherDescription?.lines()?.forEach { Text(it) }
                ?: Text("Weather Condition: Loading...")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Traffic Condition: ${trafficCondition ?: "Loading..."}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Road Type: ${roadType ?: "Loading..."}")
        }
    }
}
