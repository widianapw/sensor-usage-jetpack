package com.sensor.app.data.model

data class AccelerometerListItem(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val timestamp: Long? = 0
): CSVExportable {
    override fun getCsvBodyRow(): String {
        return "$x,$y,$z,$timestamp"
    }

    override fun getCsvHeaderRow(): String {
        return "x,y,z,timestamp"
    }

}