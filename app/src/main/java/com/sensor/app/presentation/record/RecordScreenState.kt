package com.sensor.app.presentation.record

data class RecordScreenState(
    val isRecording: Boolean = false,
    val isRecordingAttempted: Boolean = false,
    val lastStepCounter: Float = 0f,
    val currentStep: Float = 0f,
    val light: Float = 0f,
    val gyroscopeX: Float = 0f,
    val gyroscopeY: Float = 0f,
    val gyroscopeZ: Float = 0f,
    val accelerometerX: Float = 0f,
    val accelerometerY: Float = 0f,
    val accelerometerZ: Float = 0f,
    val magneticX: Float = 0f,
    val magneticY: Float = 0f,
    val magneticZ: Float = 0f,
    val latitude: Float = 0f,
    val longitude: Float = 0f,
    val title: String = ""
)