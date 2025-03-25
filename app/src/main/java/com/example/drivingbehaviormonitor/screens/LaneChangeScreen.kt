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

//  LaneChangeScreen
// This screen shows real-time sensor data and checks if the driver is making sharp sideways movements.
// A high value on the X-axis usually means the car is swerving left or right — like during a lane change or drift.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaneChangeScreen(navController: NavController) {
    // 📡 Get live sensor data from accelerometer and gyroscope
    val accelerometerData = useAccelerometerData()
    val gyroscopeData = useGyroscopeData()

    // Lane change detection logic:
    // We're watching for strong horizontal movement (X-axis).
    // If abs(X) > 2.5, we assume a possible drift or fast lane switch.
    val isDrifting = abs(accelerometerData[0]) > 2.5f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lane Changes & Drifts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            //  Show live accelerometer data
            Text("Accelerometer Data:")
            Text("X: ${"%.2f".format(accelerometerData[0])}")
            Text("Y: ${"%.2f".format(accelerometerData[1])}")
            Text("Z: ${"%.2f".format(accelerometerData[2])}")

            Spacer(modifier = Modifier.height(16.dp))

            // Show live gyroscope data too (for rotation tracking later)
            Text("Gyroscope Data:")
            Text("X: ${"%.2f".format(gyroscopeData[0])}")
            Text("Y: ${"%.2f".format(gyroscopeData[1])}")
            Text("Z: ${"%.2f".format(gyroscopeData[2])}")

            Spacer(modifier = Modifier.height(24.dp))

            //  Display a lane-change warning message if drifting is detected
            if (isDrifting) {
                Text(
                    "⚠️ Possible lane change or drift detected!",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text("No lane change detected.")
            }
        }
    }
}
