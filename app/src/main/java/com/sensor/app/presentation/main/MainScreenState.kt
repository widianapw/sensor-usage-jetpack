package com.sensor.app.presentation.main

data class MainScreenState(
    val light: Float = 0f,
    val accelerometerX: Float = 0f,
    val accelerometerY: Float = 0f,
    val accelerometerZ: Float = 0f,
    val gyroscopeX: Float = 0f,
    val gyroscopeY: Float = 0f,
    val gyroscopeZ: Float = 0f,
    val stepCounter: Float = 0f,
    val magneticX: Float = 0f,
    val magneticY: Float = 0f,
    val magneticZ: Float = 0f,
    val azimuth: Float = 0f,
)
