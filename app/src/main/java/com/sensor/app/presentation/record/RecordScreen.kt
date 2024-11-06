package com.sensor.app.presentation.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
object RecordRoute

@Composable
fun RecordScreen(
    navController: NavController
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {

            Text("Record Screen")

            Button(
                onClick = {

                }
            ) {
                Text("Start Recording")
            }
        }
    }
}