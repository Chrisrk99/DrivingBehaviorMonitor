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

// HomeScreen displays a vertical list of driving behavior categories.
// Each category represents a feature or sensor-related metric we’re tracking in the app.
// When a user taps on one, it navigates to the corresponding detailed screen (if implemented).
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
    // LazyColumn efficiently renders vertical scrollable content (good for long lists).
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // takes up the full available screen space
            .padding(16.dp) // padding around the edges for cleaner look
    ) {
        items(categories) { category ->
            // Each category is shown as a clickable Text element.
            Text(
                text = category,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        //  Navigation logic: when the user taps a category, it opens the related screen.
                        // Currently wired up to 4 screens. The rest can be added later.
                        when (category) {
                            "Acceleration and Braking Patterns" -> navController.navigate("acceleration")
                            "Speed Consistency" -> navController.navigate("speed")
                            "Cornering Behavior" -> navController.navigate("cornering")
                            "Lane Changes and Drifts" -> navController.navigate("lanechange")
                            "Wearable Device Metrics" -> navController.navigate("wearable")
                            "Environmental & Contextual Metrics" -> navController.navigate("environment")

                            // Wearables & Contextual metrics are planned for future.
                        }
                    }
                    .padding(vertical = 12.dp) // adds space between list items
            )
        }
    }
}
