package com.example.drivingbehaviormonitor.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.PERMISSIONS
import com.example.drivingbehaviormonitor.utils.queryHealthApiSupported
import com.example.drivingbehaviormonitor.utils.queryHeartRateVariability
import com.example.drivingbehaviormonitor.utils.queryRespiratoryRate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearablesScreen(navController: NavController) {
    val context = LocalContext.current
    val healthConnect = HealthConnectClient.getOrCreate(context)

    // Whether the user has granted Health Connect permissions
    var permsGranted by remember { mutableStateOf(false) }

    // Launcher to request permission dialog for Health Connect
    val permissionLauncher = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        permsGranted = granted.containsAll(PERMISSIONS)
    }

    // Trigger the permission check when the screen first launches
    LaunchedEffect(Unit) {
        val granted = healthConnect.permissionController.getGrantedPermissions()
        if (!granted.containsAll(PERMISSIONS)) {
            permissionLauncher.launch(PERMISSIONS)
        } else {
            permsGranted = true
        }
    }

    // Only fetch wearable data if Health Connect is available and permissions are granted
    val heartRateVar = if (permsGranted) queryHeartRateVariability() else null
    val respiratoryRate = if (permsGranted) queryRespiratoryRate() else null
    val isHealthSupported = queryHealthApiSupported()

    // Scaffold is the structure for our screen layout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wearable Device Metrics") },
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
            if (!isHealthSupported) {
                Text("Health Connect APIs are not installed or unavailable.")
                return@Column
            }

            Text("Wearable Data:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            // HRV display – fallback to "Unavailable"
            Text("HRV: ${heartRateVar?.let { "%.2f ms".format(it) } ?: "Unavailable"}")
            Spacer(modifier = Modifier.height(4.dp))

            // Respiratory Rate display – fallback to "Unavailable"
            Text("Respiratory Rate: ${respiratoryRate?.let { "%.2f bpm".format(it) } ?: "Unavailable"}")
        }
    }
}
