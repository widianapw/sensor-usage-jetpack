package com.sensor.app.data.model

enum class ExportFileSensorTypeEnum(val type: String? = null) {
    ACCELEROMETER("Accelerometer"),
    GYROSCOPE("Gyroscope"),
    LIGHT("Light"),
    STEP_COUNTER("StepCounter"),
    MAGNETIC("Magnetic")
}