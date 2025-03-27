package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.getLastKnownLocation
import com.example.drivingbehaviormonitor.utils.getTrafficFlowDescription
import com.example.drivingbehaviormonitor.utils.getWeatherDescription
import com.example.drivingbehaviormonitor.utils.getRoadTypeDescription
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// This tells us we’re using some experimental Material3 features
@OptIn(ExperimentalMaterial3Api::class)
// This is the EnvironmentScreen – it shows stuff like time, weather, traffic, and road type!
@Composable
fun EnvironmentScreen(navController: NavController) { // NavController helps us move between screens
    // These "var" lines let us store and update info that changes, like the time or weather
    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) } // Keeps track of the current time
    var weatherDescription by remember { mutableStateOf<String?>(null) } // Stores weather info
    var trafficCondition by remember { mutableStateOf<String?>(null) } // Stores traffic info
    var roadType by remember { mutableStateOf<String?>(null) } // Stores road type info

    // This grabs the app’s context, which we need to get the phone’s location
    val context = LocalContext.current

    // This makes a live clock that updates every second
    LaunchedEffect(Unit) {
        while (true) { // This loop keeps running forever
            currentDateTime = LocalDateTime.now() // Updates the time to right now
            delay(1000) // Waits 1 second before updating again
        }
    }

    // Here I fetch location-based data like weather, traffic, and road type
    LaunchedEffect(Unit) {
        val location = getLastKnownLocation(context) // Tries to get the phone’s last known spot
        if (location != null) { // If we got a location...
            // These functions use the latitude and longitude to get real-world info
            weatherDescription = getWeatherDescription(location.latitude, location.longitude)
            trafficCondition = getTrafficFlowDescription(location.latitude, location.longitude)
            roadType = getRoadTypeDescription(location.latitude, location.longitude)
        } else { // If we couldn’t get a location...
            // We show these messages so the user knows something went wrong
            weatherDescription = "Unable to fetch location"
            trafficCondition = "Unable to fetch traffic data"
            roadType = "Unable to fetch road type"
        }
    }

    // This gets the day of the week (like "Monday") in a nice, readable way
    val dayOfWeek = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    // This sets up a format for the time (like "14:30:45")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val formattedTime = currentDateTime.toLocalTime().format(timeFormatter) // Formats the current time

    // Scaffold is like the basic frame for our screen
    Scaffold(
        // This creates a top bar with a title and back button
        topBar = {
            TopAppBar(
                title = { Text("Environmental & Contextual Metrics") }, // The title at the top
                navigationIcon = {
                    // This adds a back button to go to the previous screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        // contentDescription is for accessibility (like screen readers)
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
            // Here I show the day of the week (e.g., "Monday")
            Text("Current Day of Week: $dayOfWeek")
            Spacer(modifier = Modifier.height(8.dp)) // Little gap for neatness

            // This shows the current time, updating every second
            Text("Current Time of Day: $formattedTime")
            Spacer(modifier = Modifier.height(8.dp)) // Another gap

            // This shows the weather – if it’s ready, we split it into lines; if not, we say "Loading..."
            weatherDescription?.lines()?.forEach { line -> Text(line) }
                ?: Text("Weather Condition: Loading...")
            Spacer(modifier = Modifier.height(8.dp))

            // This shows traffic info, or "Loading..." if it’s not ready yet
            Text("Traffic Condition: ${trafficCondition ?: "Loading..."}")
            Spacer(modifier = Modifier.height(8.dp))

            // This shows the road type, or "Loading..." if we don’t have it yet
            Text("Road Type: ${roadType ?: "Loading..."}")
        }
    }
}