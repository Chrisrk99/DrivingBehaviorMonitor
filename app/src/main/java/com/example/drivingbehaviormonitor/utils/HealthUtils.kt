package com.example.drivingbehaviormonitor.utils

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.temporal.ChronoUnit

val PERMISSIONS = setOf(
    HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
    HealthPermission.getReadPermission(RespiratoryRateRecord::class),
)

@Composable
fun queryHealthApiSupported(): Boolean {
    val context = LocalContext.current
    return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
}

@Composable
fun queryHeartRateVariability(): Double? {
    val context = LocalContext.current
    val healthConnect = HealthConnectClient.getOrCreate(context)

    var heartRateVar by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(Unit) {
        try {
            val records = healthConnect.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateVariabilityRmssdRecord::class,
                    timeRangeFilter = TimeRangeFilter.after(Instant.now().minus(5, ChronoUnit.MINUTES))
                )
            )
            heartRateVar = records.records.lastOrNull()?.heartRateVariabilityMillis
        } catch (e: Exception) {
            Log.e("queryHeartRateVariability", "Error reading HRV", e)
        }
    }

    return heartRateVar
}

@Composable
fun queryRespiratoryRate(): Double? {
    val context = LocalContext.current
    val healthConnect = HealthConnectClient.getOrCreate(context)

    var respiratoryRate by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(Unit) {
        try {
            val records = healthConnect.readRecords(
                ReadRecordsRequest(
                    recordType = RespiratoryRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.after(Instant.now().minus(5, ChronoUnit.MINUTES))
                )
            )
            respiratoryRate = records.records.lastOrNull()?.rate
        } catch (e: Exception) {
            Log.e("queryRespiratoryRate", "Error reading respiratory rate", e)
        }
    }

    return respiratoryRate
}
