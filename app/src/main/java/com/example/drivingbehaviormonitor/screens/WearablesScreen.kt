package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.queryHealthApiSupported
import com.example.drivingbehaviormonitor.utils.queryHeartRateVariability
import com.example.drivingbehaviormonitor.utils.queryRespiratoryRate // 👈 Add this import if you haven't yet

// This is the WearablesScreen – it shows data from wearable devices, like heart rate and breathing!
// We’re pulling info like Heart Rate Variability (HRV) and Respiratory Rate to see how the driver’s doing.

@OptIn(ExperimentalMaterial3Api::class) // This lets us use some new Material3 features
@Composable
fun WearablesScreen(navController: NavController) { // NavController helps us move between screens
    // Here I grab data from wearable devices using some helper functions
    val heartRateVar = queryHeartRateVariability() // Gets Heart Rate Variability (how steady the heartbeat is)
    val respiratoryRate = queryRespiratoryRate() // Gets breathing rate (breaths per minute)

    // Scaffold is like the basic frame for our screen
    Scaffold(
        // This sets up a top bar with a title and a back button
        topBar = {
            TopAppBar(
                title = { Text("Wearable Device Metrics") }, // The title at the top
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
        // Column stacks everything vertically on the screen
        Column(
            modifier = Modifier
                .padding(innerPadding) // Adds space so nothing bumps into the top bar
                .padding(24.dp) // Adds extra padding around the edges for a clean look
        ) {
            // This checks if the phone supports the Health Connect APIs we need
            if (!queryHealthApiSupported()) {
                // If the APIs aren’t available, we show this message and stop here
                Text(text = "Health Connect APIs are not installed or unavailable.")
                return@Scaffold // This stops the rest of the screen from loading
            }

            // If we get here, the APIs are good, so we show the wearable data!
            Text("Wearable Data:", style = MaterialTheme.typography.titleMedium) // A little header
            Spacer(modifier = Modifier.height(8.dp)) // Adds a small gap

            // This shows Heart Rate Variability – if we don’t have it, we say "Unavailable"
            Text("HRV: ${heartRateVar ?: "Unavailable"}")
            Spacer(modifier = Modifier.height(4.dp)) // Tiny gap between lines

            // This shows Respiratory Rate – same deal, "Unavailable" if we don’t have it
            Text("Respiratory Rate: ${respiratoryRate ?: "Unavailable"}")
        }
    }
}