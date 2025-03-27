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

data class HRVResp (val hrv: Double?, val resp: Double?)

@Composable
fun queryHeartRateVariabilityAndResp(): HRVResp {
    val TAG = "queryHeartRateVariability"

    if (!queryHealthApiSupported()) {
        return HRVResp(null, null)
    } // catchall

    val context = LocalContext.current
    val healthConnect = remember { HealthConnectClient.getOrCreate(context) }

    // widget state
    var heartRateVar by remember { mutableStateOf<Double?>(null) }
    var permsEnabled by remember { mutableStateOf(false) }
    var permsPrompt by remember { mutableStateOf(false) }
    var respRateVar by remember { mutableStateOf<Double?>(null) }

    // perms
    // https://stackoverflow.com/a/67120456
    val permLauncher =
        rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
            Log.d(TAG, "got granted $granted")
            permsEnabled = granted.containsAll(PERMISSIONS)
            permsPrompt = false
        }

    // polling for the thingy
    LaunchedEffect(permsEnabled, permsEnabled, heartRateVar) {
        val granted = healthConnect.permissionController.getGrantedPermissions()
        if (!granted.containsAll(PERMISSIONS)) {
            if (!permsPrompt) {
                // request permissions
                permsPrompt = true
                Log.d(TAG, "pushing permlauncher")
                permLauncher.launch(PERMISSIONS)
                Log.d(TAG, "launched permlauncher")
            }
            heartRateVar = null
            Log.d(TAG, "returned on perms not found")
            return@LaunchedEffect
        }

        // update the heartRate and resprate thingy
        respRateVar = try {
            healthConnect.readRecords(
                ReadRecordsRequest(
                    RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.Companion.after(
                        LocalDateTime.now().minusSeconds(30)
                    )
                )
            ).records.last().rate
        } catch (e: Exception) {
            Log.e(TAG, "failed to read heart rate var record", e)
            null
        }

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
            Log.e(TAG, "failed to read heart rate var record", e)
            null
        }
    }


    return HRVResp(hrv = heartRateVar, resp = respRateVar)
}
