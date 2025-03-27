package com.example.drivingbehaviormonitor.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.coroutines.resume

const val WEATHER_API_KEY = "d9d3b497573b2028d5091935a68517c4" // Replace with your real API key

/**
 *  useLocationSpeed()
 * This function gives us real-time speed based on GPS location updates.
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

    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationServicesEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    DisposableEffect(locationServicesEnabled) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location? = result.lastLocation
                if (location != null) {
                    if (lastLocation != null && lastUpdateTime != 0L) {
                        val distance = lastLocation!!.distanceTo(location)
                        val currentTime = System.currentTimeMillis()
                        val timeDiff = (currentTime - lastUpdateTime) / 1000f
                        speed.value = if (timeDiff > 0) distance / timeDiff else 0f
                    }
                    lastLocation = location
                    lastUpdateTime = System.currentTimeMillis()
                } else {
                    speed.value = 0f
                }
            }
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && locationServicesEnabled
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

/**
 * Returns a description of the current weather for given coordinates.
 * Should be called from inside a LaunchedEffect, not directly in @Composable.
 */
suspend fun getWeatherDescription(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
    try {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$WEATHER_API_KEY&units=metric"
        val response = URL(url).readText()
        val json = JSONObject(response)
        val description = json.getJSONArray("weather")
            .getJSONObject(0)
            .getString("description")

        "Weather: ${description.replaceFirstChar { it.uppercaseChar() }}"
    } catch (e: Exception) {
        Log.e("WeatherFetch", "Failed to fetch weather", e)
        null
    }
}

/**
 * Safely fetches the last known device location using FusedLocationProvider.
 */
@SuppressLint("MissingPermission")
suspend fun getLastKnownLocation(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    return suspendCancellableCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location -> continuation.resume(location) }
            .addOnFailureListener { continuation.resume(null) }
    }
}
