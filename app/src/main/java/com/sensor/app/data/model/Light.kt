package com.sensor.app.data.model

data class LightListItem(
    val light: Float = 0f,
    val timestamp: Long? = 0
): CSVExportable {
    override fun getCsvBodyRow(): String {
        return "$light,$timestamp"
    }

    override fun getCsvHeaderRow(): String {
        return "light,timestamp"
    }

}