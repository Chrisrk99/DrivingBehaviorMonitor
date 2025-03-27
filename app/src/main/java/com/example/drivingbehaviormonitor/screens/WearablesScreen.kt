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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearablesScreen(navController: NavController) {
    val heartRateVar = queryHeartRateVariability()
    val respiratoryRate = queryRespiratoryRate()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wearable Device Metrics") },
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
            // Show message if Health APIs aren't available
            if (!queryHealthApiSupported()) {
                Text(text = "Health Connect APIs are not installed or unavailable.")
                return@Scaffold
            }

            // Display HRV + Respiratory values
            Text("Wearable Data:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text("HRV: ${heartRateVar ?: "Unavailable"}")
            Spacer(modifier = Modifier.height(4.dp))

            Text("Respiratory Rate: ${respiratoryRate ?: "Unavailable"}")
        }
    }
}
