package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.useAccelerometerData
import com.example.drivingbehaviormonitor.utils.useGyroscopeData
import kotlin.math.abs
import androidx.compose.material.icons.automirrored.filled.ArrowBack

// This is the LaneChangeScreen – it’s all about spotting when the driver swerves or changes lanes!
// We use the phone’s sensors to check for big sideways movements.
// A high X-axis value (left/right movement) could mean a lane change or drift.

@OptIn(ExperimentalMaterial3Api::class) // This lets us use some new Material3 features
@Composable
fun LaneChangeScreen(navController: NavController) { // NavController helps us move between screens
    // Here I grab live data from the phone’s sensors
    val accelerometerData = useAccelerometerData() // This tracks movement (X, Y, Z axes)
    val gyroscopeData = useGyroscopeData() // This tracks rotation (also X, Y, Z axes)

    // This is our lane change detector!
    // We look at the X-axis (side-to-side movement) from the accelerometer.
    // If it’s bigger than 2.5 (positive or negative), we think it’s a drift or lane switch.
    // The abs() function makes sure we catch movement in either direction!
    val isDrifting = abs(accelerometerData[0]) > 2.5f

    // Scaffold is like the basic layout for our screen
    Scaffold(
        // This sets up a top bar with a title and a back button
        topBar = {
            TopAppBar(
                title = { Text("Lane Changes & Drifts") }, // This is the title at the top
                navigationIcon = {
                    // This adds a back button to go to the previous screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back" // Helps accessibility tools describe it
                        )
                    }
                }
            )
        }
    ) { innerPadding -> // innerPadding keeps stuff from overlapping the top bar
        // Column stacks all our text vertically on the screen
        Column(
            modifier = Modifier
                .padding(innerPadding) // Adds space so nothing bumps into the top bar
                .padding(24.dp) // Adds extra padding around the edges for a nice look
        ) {
            // Here I show the raw accelerometer data so we can see the movement
            Text("Accelerometer Data:")
            Text("X: ${"%.2f".format(accelerometerData[0])}") // X-axis (left/right – our focus!)
            Text("Y: ${"%.2f".format(accelerometerData[1])}") // Y-axis (forward/back)
            Text("Z: ${"%.2f".format(accelerometerData[2])}") // Z-axis (up/down)

            // This adds a little gap between sections
            Spacer(modifier = Modifier.height(16.dp))

            // Here I show the gyroscope data – we might use it later for rotation stuff
            Text("Gyroscope Data:")
            Text("X: ${"%.2f".format(gyroscopeData[0])}") // X-axis rotation
            Text("Y: ${"%.2f".format(gyroscopeData[1])}") // Y-axis rotation
            Text("Z: ${"%.2f".format(gyroscopeData[2])}") // Z-axis rotation

            // Another gap to keep things neat
            Spacer(modifier = Modifier.height(24.dp))

            // This checks if we detected a drift or lane change
            if (isDrifting) {
                // If X-axis movement is big, we show a warning in red
                Text(
                    "⚠️ Possible lane change or drift detected!",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                // If everything’s calm, we just say no change detected
                Text("No lane change detected.")
            }
        }
    }
}

