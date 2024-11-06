package com.sensor.app.presentation.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sensor.app.presentation.history.HistoryRoute
import com.sensor.app.presentation.main.components.SensorDataCard
import kotlinx.serialization.Serializable

@Serializable
object RecordRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    viewModel: RecordViewModel = viewModel(),
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState() // Add this line

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Data") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState), // Make the Column scrollable
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Input Field
            OutlinedTextField(
                value = state.title,
                onValueChange = { newTitle ->
                    viewModel.onTitleChange(newTitle)
                },
                label = { Text("Title") },
                isError = state.title.isBlank() && state.isRecordingAttempted,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.title.isBlank() && state.isRecordingAttempted) {
                Text(
                    text = "Title is required",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start/Stop Recording Button
            Button(
                onClick = {
                    if (state.isRecording) {
                        viewModel.stopRecording()
                        navController.navigate(HistoryRoute)
                    } else {
                        viewModel.startRecording()
                    }
                },
                enabled = state.isRecording || state.title.isNotBlank(), // Enable only if title is not blank or recording is active
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isRecording) "Stop Recording" else "Start Recording")
            }

            Button(
                enabled = !state.isRecording,
                onClick = {
                    navController.navigate(HistoryRoute)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View History")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display Sensor Data
            // Use a Column instead of LazyColumn to ensure smooth scrolling within the parent scroll view
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SensorDataCard(
                    title = "Light",
                    content = "${state.light} lux",
                    icon = Icons.Default.WbSunny
                )

                SensorDataCard(
                    title = "Current Steps",
                    content = "${state.currentStep}",
                    icon = Icons.Default.DirectionsWalk
                )

                SensorDataCard(
                    title = "Accelerometer",
                    content = "X: ${state.accelerometerX}\nY: ${state.accelerometerY}\nZ: ${state.accelerometerZ}",
                    icon = Icons.Default.DeviceHub
                )

                SensorDataCard(
                    title = "Gyroscope",
                    content = "X: ${state.gyroscopeX}\nY: ${state.gyroscopeY}\nZ: ${state.gyroscopeZ}",
                    icon = Icons.Default.RotateRight
                )

                SensorDataCard(
                    title = "Magnetic Field",
                    content = "X: ${state.magneticX}\nY: ${state.magneticY}\nZ: ${state.magneticZ}",
                    icon = Icons.Default.Explore
                )
            }
        }
    }
}
