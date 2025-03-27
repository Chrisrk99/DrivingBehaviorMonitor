package com.example.drivingbehaviormonitor.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.drivingbehaviormonitor.utils.queryHealthApiSupported
import com.example.drivingbehaviormonitor.utils.queryHeartRateVariabilityAndResp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearablesScreen(navController: NavController) {
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
            if (!queryHealthApiSupported()) {
                Text(text = "Health Connect apis are not installed")
                return@Scaffold
            }
            val x = queryHeartRateVariabilityAndResp()
            Text("HRV: ${x.hrv}")
            Text("Respratory: ${x.resp}")
        }
    }
}