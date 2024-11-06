package com.sensor.app.presentation.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sensor.app.presentation.record.RecordRoute
import kotlinx.serialization.Serializable

@Serializable
object MainRoute

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    navController: NavController
) {

    val state by viewModel.state.collectAsState()

    Scaffold { padding->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            Text(
                text = "Light: ${state.light} lux",
            )

            Text(
                text = "Steps Since Last Reboot: ${state.stepCounter}",
            )

            Text(
                text = "Accelerometer:\n X: ${state.accelerometerX}\nY: ${state.accelerometerY}\nZ: ${state.accelerometerZ}",
            )

            Text(
                text = "Gyroscope:\nX: ${state.gyroscopeX}\nY: ${state.gyroscopeY}\nZ: ${state.gyroscopeZ}",
            )

            Button(
                onClick = {
                    navController.navigate(RecordRoute)
                }
            ) {
                Text("Record Data")
            }
        }
    }
}