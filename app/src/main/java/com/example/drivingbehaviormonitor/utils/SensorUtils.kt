package com.example.drivingbehaviormonitor.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext


// This helper composable listens to the phone’s accelerometer sensor and gives you [x, y, z] acceleration values in m/s².
// It’s used inside AccelerationScreen to show real-time movement (like braking or speeding up).
@Composable
fun useAccelerometerData(): FloatArray {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val sensorValues = remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        sensorValues.value = it.values.clone()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed for our use case
            }
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        // Clean up listener when this composable leaves the screen (to avoid battery drain)
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return sensorValues.value
}

// This does the same thing, but for the gyroscope. It returns [x, y, z] rotation rates in rad/s.
//  Used to track turning or rotation-related behavior — helpful for analyzing sharp turns, etc.
@Composable
fun useGyroscopeData(): FloatArray {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val sensorValues = remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_GYROSCOPE) {
                        sensorValues.value = it.values.clone()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed
            }
        }

        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return sensorValues.value
}
