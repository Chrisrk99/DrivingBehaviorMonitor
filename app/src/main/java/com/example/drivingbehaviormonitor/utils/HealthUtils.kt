package com.example.drivingbehaviormonitor.utils

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDateTime

val PERMISSIONS =
    setOf(
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
    )

/**
 * Alias to check if google's proprietary(?) health apis are available
 */
@Composable
fun queryHealthApiSupported(): Boolean =
    HealthConnectClient.getSdkStatus(LocalContext.current) == HealthConnectClient.SDK_AVAILABLE

@Composable
fun queryHeartRateVariability(): Double? {
    if (!queryHealthApiSupported()) {
        return null
    } // catchall

    val context = LocalContext.current
    val healthConnect = HealthConnectClient.getOrCreate(context)

    // widget state
    var heartRateVar by remember { mutableStateOf<Double?>(null) }
    var permsEnabled by remember { mutableStateOf(false) }

    // perms
    // https://stackoverflow.com/a/67120456
    val permLauncher =
        rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
            permsEnabled = granted.containsAll(PERMISSIONS)
        }

    // polling for the thingy
    LaunchedEffect(permsEnabled) {
        val granted = healthConnect.permissionController.getGrantedPermissions()
        if (!granted.containsAll(PERMISSIONS)) {
            // request permissions
            permLauncher.launch(PERMISSIONS)
            heartRateVar = null
            return@LaunchedEffect
        }

        // update the heartRate thingy
        heartRateVar = try {
            healthConnect.readRecords(
                ReadRecordsRequest(
                    HeartRateVariabilityRmssdRecord::class,
                    timeRangeFilter = TimeRangeFilter.Companion.after(
                        LocalDateTime.now().minusSeconds(30)
                    )
                )
            ).records.last().heartRateVariabilityMillis
        } catch (e: Exception) {
            Log.e("queryHeartRateVariability", "failed to read heart rate var record", e)
            null
        }
    }

    return heartRateVar
}


@Composable
fun queryRespiratoryRate(): Double? {
    // Check if the Health Connect API is even supported on the device
    if (!queryHealthApiSupported()) {
        return null
    }

    val context = LocalContext.current
    val healthConnect = HealthConnectClient.getOrCreate(context)

    // This is the respiratory rate value we'll eventually return (if we get it!)
    var respiratoryRate by remember { mutableStateOf<Double?>(null) }
    var permsEnabled by remember { mutableStateOf(false) }

    // Ask the user for permissions (just like we did for HRV)
    val permLauncher =
        rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
            permsEnabled = granted.containsAll(PERMISSIONS)
        }

    // Launch the permission check and respiratory rate query
    LaunchedEffect(permsEnabled) {
        val granted = healthConnect.permissionController.getGrantedPermissions()
        if (!granted.containsAll(PERMISSIONS)) {
            // 🚪 Ask for permission if not already granted
            permLauncher.launch(PERMISSIONS)
            respiratoryRate = null
            return@LaunchedEffect
        }

        // Try to grab the most recent respiratory rate record from the past 30 seconds
        respiratoryRate = try {
            healthConnect.readRecords(
                ReadRecordsRequest(
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.after(
                        LocalDateTime.now().minusSeconds(30)
                    )
                )
            ).records.last().rate
        } catch (e: Exception) {
            Log.e("queryRespiratoryRate", "failed to read respiratory rate record", e)
            null
        }
    }

    return respiratoryRate
}
