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
import com.example.drivingbehaviormonitor.utils.useGyroscopeData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccelerationScreen(navController: NavController) {
    // These custom hooks pull live accelerometer and gyroscope data from the device/emulator.
    val accelerometerData = useAccelerometerData()
    val gyroscopeData = useGyroscopeData()

    //  We're focusing on the Z-axis (up/down) to determine acceleration or braking.
    val zAxis = accelerometerData[2]

    //  When the screen first loads, we remember the initial Z value as a reference (baseline).
    // This allows us to detect relative changes instead of relying on hardcoded gravity values.
    val baselineZ = remember { mutableFloatStateOf(zAxis) }

    //  Threshold determines how sensitive we are to detecting movement.
    val threshold = 1.5f
    val delta = zAxis - baselineZ.floatValue // how much has Z changed?

    // Simple logic to interpret driving behavior based on delta:
    // - If Z drops significantly, we assume the car is speeding up (accelerating)
    // - If Z increases significantly, we assume braking
    // - Otherwise, assume stable
    val statusMessage = when {
        delta > threshold -> "Braking"
        delta < -threshold -> "Accelerating"
        else -> "Stable"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acceleration & Braking") },
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
            // Live accelerometer sensor data
            Text(text = "Accelerometer Data:")
            Text(text = "X: ${"%.2f".format(accelerometerData[0])}")
            Text(text = "Y: ${"%.2f".format(accelerometerData[1])}")
            Text(text = "Z: $zAxis")

            Spacer(modifier = Modifier.height(8.dp))

            // Show interpreted driving status
            Text(text = "Driving Behavior:")
            Text(text = "Status: $statusMessage")

            Spacer(modifier = Modifier.height(16.dp))

            // Gyroscope data can help with understanding rotation, which we might use later
            Text(text = "Gyroscope Data:")
            Text(text = "X: ${"%.2f".format(gyroscopeData[0])}")
            Text(text = "Y: ${"%.2f".format(gyroscopeData[1])}")
            Text(text = "Z: ${"%.2f".format(gyroscopeData[2])}")
        }
    }
}
