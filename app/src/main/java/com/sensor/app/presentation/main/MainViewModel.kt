package com.sensor.app.presentation.main

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(
    application: Application
) : AndroidViewModel(application), SensorEventListener {
    private val _state = MutableStateFlow(MainScreenState())
    val state = _state
    val context = application
    private val sensorManager: SensorManager =
        getSystemService(context, SensorManager::class.java) as SensorManager

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    init {
        val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACTIVITY_RECOGNITION
        } else {
            Manifest.permission.BODY_SENSORS
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            requiredPermission
        ) == PackageManager.PERMISSION_GRANTED

        val sensors = listOf(
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_MAGNETIC_FIELD
        )

        sensors.forEach { sensorType ->
            sensorManager.getDefaultSensor(sensorType)?.let { sensor ->
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    fun initStepCounter() {
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { p0 ->
            when (p0.sensor.type) {
                Sensor.TYPE_LIGHT -> {
                    val light = p0.values[0]
                    _state.value = _state.value.copy(light = light)
                }

                Sensor.TYPE_ACCELEROMETER -> {
                    System.arraycopy(
                        p0.values,
                        0,
                        accelerometerReading,
                        0,
                        accelerometerReading.size
                    )
                    _state.value = _state.value.copy(
                        accelerometerX = p0.values[0],
                        accelerometerY = p0.values[1],
                        accelerometerZ = p0.values[2]
                    )
                }

                Sensor.TYPE_GYROSCOPE -> {
                    _state.value = _state.value.copy(
                        gyroscopeX = p0.values[0],
                        gyroscopeY = p0.values[1],
                        gyroscopeZ = p0.values[2]
                    )
                }

                Sensor.TYPE_STEP_COUNTER -> {
                    val steps = p0.values[0]
                    _state.value = _state.value.copy(stepCounter = steps)
                }

                Sensor.TYPE_MAGNETIC_FIELD -> {
                    System.arraycopy(p0.values, 0, magnetometerReading, 0, magnetometerReading.size)
                    _state.value = _state.value.copy(
                        magneticX = p0.values[0],
                        magneticY = p0.values[1],
                        magneticZ = p0.values[2]
                    )
                }
            }

            // Compute the azimuth angle
            val rotationMatrix = FloatArray(9)
            val success = SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )
            if (success) {
                val orientationAngles = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                _state.value = _state.value.copy(azimuth = azimuth)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle sensor accuracy changes if needed
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
