package com.sensor.app.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sensor.app.presentation.main.components.Compass
import com.sensor.app.presentation.main.components.SensorDataCard
import com.sensor.app.presentation.record.RecordRoute
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.sensor.app.presentation.history.HistoryRoute


@Serializable
object MainRoute

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Display the compass
            Compass(azimuth = state.azimuth)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(RecordRoute)
                }
            ) {
                Text("Record Data")
            }

            Button(
                onClick = {
                    navController.navigate(HistoryRoute)
                }
            ) {
                Text("History")
            }

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
                    title = "Steps Since Last Reboot",
                    content = "${state.stepCounter}",
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

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}
