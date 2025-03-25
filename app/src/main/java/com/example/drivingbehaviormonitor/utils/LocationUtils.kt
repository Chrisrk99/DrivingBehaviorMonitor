package com.example.drivingbehaviormonitor.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

/**
 *  useLocationSpeed()
 * This function gives us real-time speed based on GPS location updates.
 * We calculate speed manually by checking how far the user moved (in meters)
 * and dividing that by how much time has passed (in seconds).
 */
@Composable
fun useLocationSpeed(): Float {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val speed = remember { mutableStateOf(0f) }
    var locationServicesEnabled by remember { mutableStateOf(false) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var lastUpdateTime by remember { mutableStateOf(0L) }

    // First, check if GPS is turned on
    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationServicesEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    //  Start listening for location updates only if we have permission and GPS is enabled
    DisposableEffect(locationServicesEnabled) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location? = result.lastLocation
                if (location != null) {
                    if (lastLocation != null && lastUpdateTime != 0L) {
                        val distance = lastLocation!!.distanceTo(location) // meters
                        val currentTime = System.currentTimeMillis()
                        val timeDiff = (currentTime - lastUpdateTime) / 1000f // seconds
                        if (timeDiff > 0) {
                            speed.value = distance / timeDiff // meters per second
                        } else {
                            speed.value = 0f
                        }
                    }
                    lastLocation = location
                    lastUpdateTime = System.currentTimeMillis()
                } else {
                    speed.value = 0f
                }
            }
        }

        // 🛡Only register for updates if we have permission and GPS is enabled
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && locationServicesEnabled
        ) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000L
            ).setMinUpdateDistanceMeters(0f).build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    return if (locationServicesEnabled) speed.value else 0f
}
