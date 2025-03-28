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

// This is the HomeScreen – it’s like the main menu of our app!
// It shows a list of driving behavior categories we’re tracking.
// When someone taps a category, it takes them to a detailed screen for that topic.
// Here’s the list of categories we’re working with:
val categories = listOf(
    "Acceleration and Braking Patterns",
    "Speed Consistency",
    "Cornering Behavior",
    "Lane Changes and Drifts",
    "Wearable Device Metrics",
    "Environmental & Contextual Metrics"
)

// This is the main function that builds our HomeScreen
@Composable
fun HomeScreen(navController: NavController) { // NavController helps us move to other screens
    // LazyColumn is a cool tool that makes a scrollable list – perfect for showing our categories!
    // It only loads what’s on the screen, so it’s super efficient, even with long lists
    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // This makes the list take up the whole screen
            .padding(16.dp) // Adds some space around the edges so it looks neat
    ) {
        // This "items" part loops through our categories list and makes an entry for each one
        items(categories) { category ->
            // Each category becomes a clickable Text item in the list
            Text(
                text = category, // This shows the category name (like "Cornering Behavior")
                modifier = Modifier
                    .fillMaxWidth() // Makes the text stretch across the screen
                    .clickable {
                        // When someone taps a category, this decides where to go!
                        // It uses the NavController to switch to the right screen
                        when (category) {
                            // Each line here links a category to its screen
                            "Acceleration and Braking Patterns" -> navController.navigate("acceleration")
                            "Speed Consistency" -> navController.navigate("speed")
                            "Cornering Behavior" -> navController.navigate("cornering")
                            "Lane Changes and Drifts" -> navController.navigate("lanechange")
                            "Wearable Device Metrics" -> navController.navigate("wearable")
                            "Environmental & Contextual Metrics" -> navController.navigate("environment")
                            // Some screens (like Wearable) might not be built yet – we can add them later!
                        }
                    }
                    .padding(vertical = 12.dp) // Adds some space above and below each item so they’re not squished
            )
        }
    }
}