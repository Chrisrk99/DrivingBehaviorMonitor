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

// This is the CorneringScreen – it’s all about detecting sharp turns using the phone’s gyroscope!
// The gyroscope tells us how fast the phone is rotating around X, Y, and Z axes.
// When you’re holding the phone upright (portrait mode), the Z-axis rotation is what we care about –
// it shows left or right steering movements. A big Z value means a sharp turn!

@OptIn(ExperimentalMaterial3Api::class) // This lets us use some new Material3 features
@Composable
fun CorneringScreen(navController: NavController) { // NavController helps us move between screens
    // Here I grab live gyroscope data – it gives us rotation speed in radians per second (fancy unit for turning)
    val gyroscopeData = useGyroscopeData()

    // This is our sharp turn detector!
    // We check the Z-axis rotation (gyroscopeData[2]). If it’s more than 1.5 (positive or negative),
    // we say it’s a sharp turn. The abs() function makes sure we catch turns in either direction!
    val isSharpTurn = abs(gyroscopeData[2]) > 1.5f

    // Scaffold is like the skeleton of our screen – it holds everything together
    Scaffold(
        // This sets up the top bar with a title and a back button
        topBar = {
            TopAppBar(
                title = { Text("Cornering Behavior") }, // This is the title you see at the top
                navigationIcon = {
                    // This adds a back button that takes us to the previous screen
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        // The contentDescription helps screen readers for accessibility
                    }
                }
            )
        }
    ) { innerPadding -> // innerPadding makes sure stuff doesn’t overlap with the top bar
        // Column stacks all our text and stuff vertically
        Column(
            modifier = Modifier
                .padding(innerPadding) // This keeps things from bumping into the top bar
                .padding(24.dp) // This adds some nice space around the edges
        ) {
            // Here I’m showing the raw gyroscope data so we can see what’s happening
            Text("Gyroscope Rotation:")
            Text("X: ${"%.2f".format(gyroscopeData[0])}") // X-axis rotation (tilting forward/back)
            Text("Y: ${"%.2f".format(gyroscopeData[1])}") // Y-axis rotation (tilting side to side)
            Text("Z (rotation): ${"%.2f".format(gyroscopeData[2])}") // Z-axis rotation (left/right turns – our focus!)

            // This adds a little gap between sections to keep it tidy
            Spacer(modifier = Modifier.height(24.dp))

            // This checks if we detected a sharp turn and shows a message
            if (isSharpTurn) {
                // If Z rotation is big, we show a warning in red (error color)
                Text("⚠️ Sharp turn detected!", color = MaterialTheme.colorScheme.error)
            } else {
                // If no sharp turn, we just say everything’s chill
                Text("No sharp turning detected.")
            }
        }
    }
}