package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.useAccelerometerData
import com.example.drivingbehaviormonitor.utils.useGyroscopeData

/**
 * AccelerationScreen
 * This screen shows live data from the phone’s accelerometer and gyroscope.
 * We use custom hooks (functions) to tap into those sensors in real-time.
 * Super useful for visualizing braking, turning, or jerky movement.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccelerationScreen(navController: NavController) {
    val accelerometerData = useAccelerometerData()
    val gyroscopeData = useGyroscopeData()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acceleration & Braking") },
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
            //  Showing accelerometer data (movement along X/Y/Z axis)
            Text(text = "Accelerometer Data:")
            Text(text = "X: ${"%.2f".format(accelerometerData[0])}")
            Text(text = "Y: ${"%.2f".format(accelerometerData[1])}")
            Text(text = "Z: ${"%.2f".format(accelerometerData[2])}")

            Spacer(modifier = Modifier.height(16.dp)) // adds spacing between sensor sections

            //  Showing gyroscope data (rotation/turning speed)
            Text(text = "Gyroscope Data:")
            Text(text = "X: ${"%.2f".format(gyroscopeData[0])}")
            Text(text = "Y: ${"%.2f".format(gyroscopeData[1])}")
            Text(text = "Z: ${"%.2f".format(gyroscopeData[2])}")
        }
    }
}
