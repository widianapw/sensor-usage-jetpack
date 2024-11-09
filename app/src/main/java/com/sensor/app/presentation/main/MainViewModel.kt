package com.sensor.app.presentation.main

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
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

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    init {
        val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Manifest.permission.ACTIVITY_RECOGNITION
        } else {
            Manifest.permission.BODY_SENSORS
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            context, requiredPermission
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun initStepCounter() {
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    fun initLocationTracker() {
        // Start location tracking
// Check if location permissions are granted
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            // Create location request
            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // 10 seconds
                fastestInterval = 1000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                smallestDisplacement = 1f
            }

            // Define the location callback
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        _state.value = _state.value.copy(
                            latitude = location.latitude.toFloat(),
                            longitude = location.longitude.toFloat()
                        )
                        Log.d(
                            "MainViewModel",
                            "Location updated: ${location.latitude}, ${location.longitude}"
                        )
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    super.onLocationAvailability(locationAvailability)
                    Log.d(
                        "MainViewModel",
                        "Location availability: ${locationAvailability.isLocationAvailable}"
                    )
                }
            }

            // Request location updates
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, null // Looper
            )
        } else {
            Log.d("MainViewModel", "Location permissions not granted.")
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
                        p0.values, 0, accelerometerReading, 0, accelerometerReading.size
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
                        magneticX = p0.values[0], magneticY = p0.values[1], magneticZ = p0.values[2]
                    )
                }
            }

            // Compute the azimuth angle
            val rotationMatrix = FloatArray(9)
            val success = SensorManager.getRotationMatrix(
                rotationMatrix, null, accelerometerReading, magnetometerReading
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
        Log.d("MainViewModel", "onCleared Executed")
        sensorManager.unregisterListener(this)
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
