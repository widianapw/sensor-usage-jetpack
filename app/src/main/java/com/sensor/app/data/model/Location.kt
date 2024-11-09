package com.sensor.app.data.model

data class LocationListItem(
    val latitude: Float = 0f,
    val longitude: Float = 0f,
    val timestamp: Long? = 0
): CSVExportable {
    override fun getCsvBodyRow(): String {
        return "$latitude,$longitude,$timestamp"
    }

    override fun getCsvHeaderRow(): String {
        return "latitude,longitude,timestamp"
    }
}