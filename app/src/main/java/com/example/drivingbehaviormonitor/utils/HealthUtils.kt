package com.example.drivingbehaviormonitor.utils

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
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
