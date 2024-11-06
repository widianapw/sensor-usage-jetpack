package com.sensor.app.presentation.history.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.sensor.app.data.schema.History
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryItem(history: History, onDeleteItemClicked: (History) -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = history.title,
                style = MaterialTheme.typography.titleLarge
            )

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.format(Date(history.timestamp))

            Text(
                text = "Recorded at: $date",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Display clickable paths for each sensor data
            SensorFileItem("Accelerometer Data", history.accelerometerPath, context)
            SensorFileItem("Gyroscope Data", history.gyroscopePath, context)
            SensorFileItem("Magnetic Field Data", history.magneticPath, context)
            SensorFileItem("Light Data", history.lightPath, context)
            SensorFileItem("Step Counter Data", history.stepCounterPath, context)

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onDeleteItemClicked(history)
                }
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun SensorFileItem(label: String, filePath: String, context: Context) {
    if (filePath.isNotEmpty()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    openFile(context, filePath)
                }
                .padding(vertical = 4.dp)
        )
    }
}

fun openFile(context: Context, filePath: String) {
    val file = File(filePath)
    if (!file.exists()) {
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        return
    }

    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "text/csv")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_SHORT).show()
    }
}
