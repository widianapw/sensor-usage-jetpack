package com.sensor.app.presentation.main

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.sensor.app.presentation.history.HistoryRoute


@Serializable
object MainRoute

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    val BODY_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Manifest.permission.ACTIVITY_RECOGNITION
    } else {
        Manifest.permission.BODY_SENSORS
    }

    val permissionToRequest = arrayOf(
        BODY_PERMISSION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            Log.d("MAINSCREEN", "MainScreen: permissions: $permissions")
            if (permissions.all { it.value }) {
                Log.d("MAINSCREEN", "MainScreen: All permissions granted")
                viewModel.initStepCounter()
                viewModel.initLocationTracker()
            }
        }
    )


// Handle permission request and result
    LaunchedEffect(Unit) {
        permissionLauncher.launch(permissionToRequest)
    }

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
                    title = "Location",
                    content = "Lat: ${state.latitude}\nLong: ${state.longitude}",
                    icon = Icons.Default.LocationOn
                )
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
