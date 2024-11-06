package com.sensor.app.data.model

interface CSVExportable {
    fun getCsvBodyRow(): String
    fun getCsvHeaderRow(): String
}