package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

//  HomeScreen shows the list of driving behavior categories.
// Each item is clickable and navigates to a corresponding screen (if available).
// Right now, only "Acceleration and Braking" and "Speed Consistency" are wired up.
val categories = listOf(
    "Acceleration and Braking Patterns",
    "Speed Consistency",
    "Cornering Behavior",
    "Lane Changes and Drifts",
    "Wearable Device Metrics",
    "Environmental & Contextual Metrics"
)

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(categories) { category ->
            Text(
                text = category,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //  Navigation logic – more screens can be added as needed
                        when (category) {
                            "Acceleration and Braking Patterns" -> navController.navigate("acceleration")
                            "Speed Consistency" -> navController.navigate("speed")
                            // Add more conditions here as new features/screens are built
                        }
                    }
                    .padding(vertical = 12.dp)
            )
        }
    }
}
