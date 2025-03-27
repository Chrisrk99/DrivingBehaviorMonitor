package com.example.drivingbehaviormonitor.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

// This helper function listens to the phone’s accelerometer sensor and gives you [x, y, z] values in m/s².
// It’s used in AccelerationScreen to show real-time movement, like braking or speeding up.
@Composable
fun useAccelerometerData(): FloatArray {
    val context = LocalContext.current // Grabs the app’s context so we can use phone features
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager // Sets up the sensor manager
    }
    // This holds the [x, y, z] values – starts at zero until we get real data
    val sensorValues = remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    // This starts listening for sensor updates when the screen loads
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                // When the sensor sends new data, we grab it here
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        // If it’s from the accelerometer, update our values
                        sensorValues.value = it.values.clone() // Clone keeps the data safe
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // We don’t need this for now – it’s about sensor precision
            }
        }

        // Here I get the accelerometer sensor from the phone
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // This tells the sensor to send updates at a speed good for UI (not too fast or slow)
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        // When the screen goes away, we stop listening to save battery
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // This hands back the latest [x, y, z] values we got
    return sensorValues.value
}

// This does the same thing but for the gyroscope. It gives [x, y, z] rotation rates in rad/s.
// It’s used to track turning or rotation, like spotting sharp turns.
@Composable
fun useGyroscopeData(): FloatArray {
    val context = LocalContext.current // Gets the app’s context again
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager // Sets up the sensor manager
    }
    // This stores the [x, y, z] rotation values – starts at zero
    val sensorValues = remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    // This starts listening for gyroscope updates when the screen loads
    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                // When the gyroscope sends new data, we catch it here
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_GYROSCOPE) {
                        // If it’s from the gyroscope, update our values
                        sensorValues.value = it.values.clone() // Clone keeps the data fresh
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed for our use case – just about sensor accuracy
            }
        }

        // Here I get the gyroscope sensor from the phone
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        // This starts the gyroscope sending updates at a nice pace for the screen
        sensorManager.registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_UI)

        // This stops the listener when we leave the screen to save power
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    // This returns the latest [x, y, z] rotation values we’ve got
    return sensorValues.value
}