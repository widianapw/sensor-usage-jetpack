package com.sensor.app.presentation.record

data class RecordScreenState(
    val isRecording: Boolean = false,
    val lastStepCounter: Float = 0f,
    val title: String = "Record Screen"
)