package com.sensor.app.presentation.main

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
        val sensors = listOf(
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_MAGNETIC_FIELD
        )

        sensors.forEach { sensorType ->
            sensorManager.getDefaultSensor(sensorType)?.let { sensor ->
                val delay = if (sensorType == Sensor.TYPE_STEP_COUNTER) {
                    SensorManager.SENSOR_DELAY_FASTEST
                } else {
                    SensorManager.SENSOR_DELAY_NORMAL
                }
                sensorManager.registerListener(this, sensor, delay)
            }
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
                    System.arraycopy(p0.values, 0, accelerometerReading, 0, accelerometerReading.size)
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
