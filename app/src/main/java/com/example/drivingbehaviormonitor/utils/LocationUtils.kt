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

// These are API keys we need to talk to weather and traffic services
const val WEATHER_API_KEY = "d9d3b497573b2028d5091935a68517c4"
const val TOMTOM_API_KEY = "ACFOLNzdm0Krdc57AWdAemBjVg9LjHaZ"

// This function gets the current speed using the phone’s GPS
@Composable
fun useLocationSpeed(): Float {
    val context = LocalContext.current // Grabs the app’s context (like its environment)
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context) // Sets up the GPS tool
    }

    // These keep track of speed and location info
    val speed = remember { mutableStateOf(0f) } // Stores the current speed
    var locationServicesEnabled by remember { mutableStateOf(false) } // Checks if GPS is on
    var lastLocation by remember { mutableStateOf<Location?>(null) } // Last known spot
    var lastUpdateTime by remember { mutableStateOf(0L) } // When we last checked

    // Here I check if GPS is turned on
    LaunchedEffect(Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationServicesEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // This starts listening for location updates to calculate speed
    DisposableEffect(locationServicesEnabled) {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    if (lastLocation != null && lastUpdateTime != 0L) {
                        val distance = lastLocation!!.distanceTo(location) // How far we moved
                        val currentTime = System.currentTimeMillis() // Current time
                        val timeDiff = (currentTime - lastUpdateTime) / 1000f // Time since last update in seconds
                        speed.value = if (timeDiff > 0) distance / timeDiff else 0f // Speed = distance ÷ time
                    }
                    lastLocation = location // Update the last spot
                    lastUpdateTime = System.currentTimeMillis() // Update the time
                } else {
                    speed.value = 0f // No location? Speed stays zero
                }
            }
        }

        // If we have permission and GPS is on, start tracking
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && locationServicesEnabled
        ) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000L // Check every second, very precise
            ).setMinUpdateDistanceMeters(0f).build() // No minimum distance needed

            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper() // Runs on the main thread
            )
        }

        // This stops tracking when we’re done so we don’t waste battery
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Returns the speed if GPS is on, or zero if it’s not
    return if (locationServicesEnabled) speed.value else 0f
}

// This gets weather info based on a location (latitude and longitude)
suspend fun getWeatherDescription(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
    try {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$WEATHER_API_KEY&units=metric"
        val response = URL(url).readText() // Fetches data from the weather API
        val json = JSONObject(response) // Turns the response into something we can read

        val weather = json.getJSONArray("weather").getJSONObject(0)
        val description = weather.getString("description").replaceFirstChar { it.uppercaseChar() } // Weather type, capitalized
        val main = json.getJSONObject("main")
        val temperature = main.getDouble("temp") // Temperature in Celsius
        val humidity = main.getInt("humidity") // Humidity percentage

        // Puts it all together in a nice string
        "Weather: $description\nTemp: ${temperature}°C\nHumidity: $humidity%"
    } catch (e: Exception) {
        Log.e("WeatherFetch", "Failed to fetch weather", e) // Logs an error if something goes wrong
        null // Returns nothing if we can’t get the weather
    }
}

// This grabs the phone’s last known location quickly
@SuppressLint("MissingPermission")
suspend fun getLastKnownLocation(context: Context): Location? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    return suspendCancellableCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location -> continuation.resume(location) } // Returns the location if we get it
            .addOnFailureListener { continuation.resume(null) } // Returns null if we don’t
    }
}

// This figures out traffic conditions using the TomTom API
suspend fun getTrafficFlowDescription(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
    try {
        val url = "https://api.tomtom.com/traffic/services/4/flowSegmentData/absolute/10/json" +
                "?point=$lat,$lon&unit=KMPH&key=$TOMTOM_API_KEY"
        val response = URL(url).readText() // Fetches traffic data
        val json = JSONObject(response)
        val currentSpeed = json.getJSONObject("flowSegmentData").getDouble("currentSpeed") // Current speed in km/h
        val freeFlowSpeed = json.getJSONObject("flowSegmentData").getDouble("freeFlowSpeed") // Normal speed without traffic
        val speedRatio = currentSpeed / freeFlowSpeed // Ratio of current to normal speed

        // Decides what traffic is like based on the ratio
        when {
            speedRatio > 0.8 -> "Light traffic" // Almost normal speed
            speedRatio > 0.5 -> "Moderate traffic" // Slower but moving
            else -> "Heavy traffic" // Crawling along
        }
    } catch (e: Exception) {
        Log.e("TrafficAPI", "Error fetching traffic data", e) // Logs an error if it fails
        null // Returns nothing if we can’t get traffic info
    }
}

// This finds out what kind of road we’re on using OpenStreetMap
suspend fun getRoadTypeDescription(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
    try {
        val url = "https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lon&format=json&zoom=18&addressdetails=1"
        val connection = URL(url).openConnection().apply {
            setRequestProperty("User-Agent", "DrivingBehaviorMonitorApp/1.0") // Tells the API who we are
        }
        val response = connection.getInputStream().bufferedReader().readText() // Gets the road data
        val json = JSONObject(response)
        val address = json.optJSONObject("address")
        val road = address?.optString("road", "") // Street name, if there is one
        val type = json.optString("type", "") // Road type

        // Turns the road type into something readable
        val typeLabel = when (type) {
            "motorway" -> "Highway"
            "residential" -> "Residential Street"
            "primary" -> "Primary Road"
            "tertiary" -> "Tertiary Road"
            "unclassified" -> "Local Road"
            else -> type.replaceFirstChar { it.uppercaseChar() } // Capitalizes whatever it is
        }

        // Adds the road name if we have it
        "$typeLabel${if (!road.isNullOrBlank()) " ($road)" else ""}"
    } catch (e: Exception) {
        Log.e("RoadTypeFetch", "Failed to fetch road info", e) // Logs an error if it fails
        null // Returns nothing if we can’t get the road type
    }
}