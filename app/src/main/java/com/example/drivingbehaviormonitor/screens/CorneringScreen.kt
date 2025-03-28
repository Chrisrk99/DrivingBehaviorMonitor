package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.useGyroscopeData
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorneringScreen(navController: NavController) {
    val gyroscopeData = useGyroscopeData()
    val zRotation = gyroscopeData[2]
    val threshold = 1.5f // Sensitivity for detecting sharp turns

    // This state controls whether the warning is shown
    var showSharpTurn by remember { mutableStateOf(false) }

    // When we detect a sharp turn, update the timestamp
    var lastTurnTime by remember { mutableStateOf(0L) }

    val currentTime = System.currentTimeMillis()

    // If a sharp turn is happening right now, update the last turn time
    if (abs(zRotation) > threshold) {
        lastTurnTime = currentTime
        if (!showSharpTurn) {
            showSharpTurn = true
        }
    }

    // Automatically hide warning after 3 seconds without a sharp turn
    LaunchedEffect(lastTurnTime) {
        delay(3000)
        if (System.currentTimeMillis() - lastTurnTime >= 3000) {
            showSharpTurn = false
        }
    }

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
            Text("Gyroscope Rotation:")
            Text("X: ${"%.2f".format(gyroscopeData[0])}")
            Text("Y: ${"%.2f".format(gyroscopeData[1])}")
            Text("Z (rotation): ${"%.2f".format(zRotation)}")

            Spacer(modifier = Modifier.height(24.dp))

            if (showSharpTurn) {
                Text("⚠️ Sharp turn detected!", color = MaterialTheme.colorScheme.error)
            } else {
                Text("No sharp turning detected.")
            }
        }
    }
}
