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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.useGyroscopeData
import kotlin.math.abs

// CorneringScreen – Detects sharp turns using the phone’s gyroscope.
//
// The gyroscope gives us rotation values around X, Y, and Z axes.
// In most phones held upright (portrait mode), Z-axis rotation corresponds to left/right steering.
// A high value on the Z-axis usually means a fast or sharp turn.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorneringScreen(navController: NavController) {
    // 🔄 Grab live gyroscope data (angular velocity in radians/sec)
    val gyroscopeData = useGyroscopeData()

    // Sharp turn detection logic:
    // If rotation around Z-axis is stronger than 1.5 rad/s, it's likely a sharp turn.
    val isSharpTurn = abs(gyroscopeData[2]) > 1.5f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cornering Behavior") },
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
            // Show gyroscope rotation values
            Text("Gyroscope Rotation:")
            Text("X: ${"%.2f".format(gyroscopeData[0])}")
            Text("Y: ${"%.2f".format(gyroscopeData[1])}")
            Text("Z (rotation): ${"%.2f".format(gyroscopeData[2])}")

            Spacer(modifier = Modifier.height(24.dp))

            // Show a message if a sharp turn is detected
            if (isSharpTurn) {
                Text("⚠️ Sharp turn detected!", color = MaterialTheme.colorScheme.error)
            } else {
                Text("No sharp turning detected.")
            }
        }
    }
}
