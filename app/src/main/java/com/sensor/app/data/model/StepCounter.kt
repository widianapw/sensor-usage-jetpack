package com.sensor.app.data.model

data class StepCounterListItem(
    val stepCounter: Float = 0f,
    val timestamp: Long? = 0
): CSVExportable {
    override fun getCsvBodyRow(): String {
        return "$stepCounter,$timestamp"
    }

    override fun getCsvHeaderRow(): String {
        return "stepCounter,timestamp"
    }

}