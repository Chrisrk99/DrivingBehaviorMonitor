package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.useAccelerometerData
import com.example.drivingbehaviormonitor.utils.useGravityAccelerometerData
import com.example.drivingbehaviormonitor.utils.useGyroscopeData

// This tells the app we're using some experimental features from Material3 library
@OptIn(ExperimentalMaterial3Api::class)
// This is the main function for our Acceleration Screen, and it takes a NavController to handle navigation
@Composable
fun AccelerationScreen(navController: NavController) {
    // Here I used custom hooks to get live data from the phone's sensors
    // accelerometerData gives us movement info (X, Y, Z axes)
    val accelerometerData = useAccelerometerData()
    val gravityData = useGravityAccelerometerData()
    // gyroscopeData gives us rotation info (also X, Y, Z axes)
    val gyroscopeData = useGyroscopeData()

    // Factor out gravity from each axis. We don't know phone orientation while driving
    val xAxis = accelerometerData[0] - gravityData[0]
    val yAxis = accelerometerData[1] - gravityData[1]
    val zAxis = accelerometerData[2] - gravityData[2]

    // When the screen loads, we "remember" the starting Z value as our baseline
    // This helps us compare changes later instead of guessing what "normal" is
    val baselineX = remember { mutableFloatStateOf(xAxis) }
    val baselineY = remember { mutableFloatStateOf(yAxis) }
    val baselineZ = remember { mutableFloatStateOf(zAxis) }

    // This threshold is like a sensitivity setting - it decides how big a change in Z we care about
    val threshold = 1.5f
    // Here I calculate how much Z has changed from our baseline
    val delta = Math.sqrt(Math.pow((xAxis - baselineX.floatValue).toDouble(), 2.0) + Math.pow((yAxis - baselineY.floatValue).toDouble(), 2.0) + Math.pow((zAxis - baselineZ.floatValue).toDouble(), 2.0)).toFloat()

    // This part figures out what the driver is doing based on the Z change (delta):
    // - If Z goes way up (delta > threshold), we say "Braking"
    // - If Z drops a lot (delta < -threshold), we say "Accelerating"
    // - Otherwise, we assume the car is "Stable"
    val statusMessage = when {
        delta > threshold -> "Braking"
        delta < -threshold -> "Accelerating"
        else -> "Stable"
    }

    // Scaffold is like the basic layout structure for our screen
    Scaffold(
        // This creates a top bar with a title and a back button
        topBar = {
            TopAppBar(
                title = { Text("Acceleration & Braking") }, // This sets the title at the top
                navigationIcon = {
                    // This adds a back button that takes us to the previous screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back" // This is for accessibility (screen readers)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // Column stacks everything vertically on the screen
        Column(
            modifier = Modifier
                .padding(innerPadding) // This adds space so stuff doesn’t bump into the top bar
                .padding(24.dp) // This adds extra padding around the edges for a nice look
        ) {
            // Here I’m showing the raw accelerometer data
            Text(text = "Accelerometer Data:")
            Text(text = "X: ${"%.2f".format(accelerometerData[0])}") // X-axis (side-to-side movement)
            Text(text = "Y: ${"%.2f".format(accelerometerData[1])}") // Y-axis (forward/back movement)
            Text(text = "Z: ${"%.2f".format(accelerometerData[2])}") // Z-axis (up/down movement)

            // This adds a little gap between sections
            Spacer(modifier = Modifier.height(8.dp))

            // This shows what we think the driver is doing based on the Z-axis
            Text(text = "Driving Behavior:")
            Text(text = "Status: $statusMessage") // Displays "Braking", "Accelerating", or "Stable"

            // Another gap to keep things neat
            Spacer(modifier = Modifier.height(16.dp))

            // Here I’m showing gyroscope data, which we might use later for rotation stuff
            Text(text = "Gyroscope Data:")
            Text(text = "X: ${"%.2f".format(gyroscopeData[0])}") // X-axis rotation
            Text(text = "Y: ${"%.2f".format(gyroscopeData[1])}") // Y-axis rotation
            Text(text = "Z: ${"%.2f".format(gyroscopeData[2])}") // Z-axis rotation
        }
    }
}