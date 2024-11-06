package com.sensor.app.presentation.record

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sensor.app.MyApplication
import com.sensor.app.data.model.AccelerometerListItem
import com.sensor.app.data.model.CSVExportable
import com.sensor.app.data.model.ExportFileSensorTypeEnum
import com.sensor.app.data.model.GyroscopeListItem
import com.sensor.app.data.model.LightListItem
import com.sensor.app.data.model.MagneticListItem
import com.sensor.app.data.model.StepCounterListItem
import com.sensor.app.data.schema.History
import io.realm.kotlin.UpdatePolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException

class RecordViewModel(
    application: Application
) : AndroidViewModel(application), SensorEventListener {

    val _state = MutableStateFlow(RecordScreenState())
    val state = _state
    private val realm = MyApplication.realm
    private val gyroscopeList = mutableListOf<GyroscopeListItem>()
    private val stepCounterList = mutableListOf<StepCounterListItem>()
    private val lightList = mutableListOf<LightListItem>()
    private val accelerometerList = mutableListOf<AccelerometerListItem>()
    private val magneticList = mutableListOf<MagneticListItem>()

    val context = application
    private val sensorManager: SensorManager =
        getSystemService(context, SensorManager::class.java) as SensorManager

    init {
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }

        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometerSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        val magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        magneticSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun onTitleChange(newTitle: String) {
        _state.value = _state.value.copy(title = newTitle)
    }

    private fun clearData() {
        gyroscopeList.clear()
        stepCounterList.clear()
        lightList.clear()
        accelerometerList.clear()
        magneticList.clear()
        _state.value = RecordScreenState()
    }

    fun startRecording() {
        if (state.value.title.isBlank()) {
            // Update state to show that recording was attempted without a title
            _state.value = _state.value.copy(isRecordingAttempted = true)
            return
        }
        _state.value = _state.value.copy(isRecording = true, isRecordingAttempted = false)
        _state.value = _state.value.copy(isRecording = true)
    }

    fun stopRecording() {
        _state.value = _state.value.copy(isRecording = false)
        exportDataToCSV()
//        clearData()
    }

    private fun exportDataToCSV() {
        val timeMillis = System.currentTimeMillis()
        val history = History().apply {
            timestamp = timeMillis
            title = _state.value.title
        }
        ExportFileSensorTypeEnum.entries.forEach { sensorType ->
            val dataList = when (sensorType) {
                ExportFileSensorTypeEnum.ACCELEROMETER -> accelerometerList
                ExportFileSensorTypeEnum.GYROSCOPE -> gyroscopeList
                ExportFileSensorTypeEnum.LIGHT -> lightList
                ExportFileSensorTypeEnum.STEP_COUNTER -> stepCounterList
                ExportFileSensorTypeEnum.MAGNETIC -> magneticList
            }
            val fileName = "${_state.value.title}_${sensorType.type}_${timeMillis}.csv"
            writeCsv(fileName, dataList)

            // Get the absolute file path
            val csvFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val filePath = csvFile.absolutePath

            when (sensorType) {
                ExportFileSensorTypeEnum.ACCELEROMETER -> history.accelerometerPath = filePath
                ExportFileSensorTypeEnum.GYROSCOPE -> history.gyroscopePath = filePath
                ExportFileSensorTypeEnum.LIGHT -> history.lightPath = filePath
                ExportFileSensorTypeEnum.STEP_COUNTER -> history.stepCounterPath = filePath
                ExportFileSensorTypeEnum.MAGNETIC -> history.magneticPath = filePath
            }
        }

        viewModelScope.launch {
            realm.write {
                copyToRealm(history, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        when (p0?.sensor?.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val steps = p0.values[0]
                if (_state.value.isRecording) {
                    val currentStep = steps - _state.value.lastStepCounter
                    stepCounterList.add(
                        StepCounterListItem(
                            currentStep,
                            System.currentTimeMillis()
                        )
                    )
                    _state.value = _state.value.copy(currentStep = currentStep)
                } else {
                    _state.value = _state.value.copy(lastStepCounter = steps)
                }
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]
                if (_state.value.isRecording) {
                    accelerometerList.add(
                        AccelerometerListItem(
                            x,
                            y,
                            z,
                            System.currentTimeMillis()
                        )
                    )
                    _state.value = _state.value.copy(
                        accelerometerX = x,
                        accelerometerY = y,
                        accelerometerZ = z
                    )
                }
            }

            Sensor.TYPE_GYROSCOPE -> {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]
                if (_state.value.isRecording) {
                    gyroscopeList.add(GyroscopeListItem(x, y, z, System.currentTimeMillis()))
                    _state.value = _state.value.copy(
                        gyroscopeX = x,
                        gyroscopeY = y,
                        gyroscopeZ = z
                    )
                }
            }

            Sensor.TYPE_LIGHT -> {
                val light = p0.values[0]
                if (_state.value.isRecording) {
                    lightList.add(LightListItem(light, System.currentTimeMillis()))
                    _state.value = _state.value.copy(light = light)
                }
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]
                if (_state.value.isRecording) {
                    magneticList.add(MagneticListItem(x, y, z, System.currentTimeMillis()))
                    _state.value = _state.value.copy(
                        magneticX = x,
                        magneticY = y,
                        magneticZ = z
                    )
                }
            }


        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }

    // General CSV export function
    private fun writeCsv(
        fileName: String,
        dataList: List<CSVExportable>
    ) {
        val csvFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            FileWriter(csvFile).use { writer ->
                // Write headers using the first item's header method
                if (dataList.isNotEmpty()) {
                    writer.appendLine(dataList.first().getCsvHeaderRow())
                }

                // Write data rows
                dataList.forEach { item ->
                    writer.appendLine(item.getCsvBodyRow())
                }

                writer.flush()
            }
            Toast.makeText(
                context,
                "$fileName exported to ${csvFile.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error exporting $fileName", Toast.LENGTH_LONG).show()
        }
    }


}