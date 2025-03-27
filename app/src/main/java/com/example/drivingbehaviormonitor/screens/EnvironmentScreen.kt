package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.getWeatherDescription
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentScreen(navController: NavController) {
    // Store the current time and update every second
    var currentDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    // Store the weather info
    var weatherDescription by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime = LocalDateTime.now()
            delay(1000)
        }
    }

    // Hardcoded coordinates (Tokyo, Japan) to test weather API response
    val testLat = 35.0
    val testLon = 139.0

    // Fetch weather once using the test coordinates
    LaunchedEffect(Unit) {
        weatherDescription = getWeatherDescription(testLat, testLon)
    }

    // Format display values
    val dayOfWeek = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val formattedTime = currentDateTime.toLocalTime().format(timeFormatter)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Time & Day Info") },
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
            Text("Current Day of Week: $dayOfWeek")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Current Time of Day: $formattedTime")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Weather Condition: ${weatherDescription ?: "Loading..."}")
        }
    }
}
