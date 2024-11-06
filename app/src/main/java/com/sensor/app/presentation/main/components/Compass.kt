package com.sensor.app.presentation.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun Compass(azimuth: Float) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Canvas(
        modifier = Modifier
            .size(200.dp)
    ) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2
        val center = Offset(x = size.width / 2, y = size.height / 2)

        // Draw the outer circle
        drawCircle(
            color = primaryColor,
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
        )

        // Rotate the canvas to draw the compass needle
        rotate(-azimuth, pivot = center) {
            // Draw the compass needle (north)
            drawLine(
                color = Color.Red,
                start = center,
                end = Offset(x = center.x, y = center.y - radius + 20.dp.toPx()),
                strokeWidth = 8.dp.toPx()
            )

            // Draw the compass tail (south)
            drawLine(
                color = Color.Gray,
                start = center,
                end = Offset(x = center.x, y = center.y + radius - 20.dp.toPx()),
                strokeWidth = 8.dp.toPx()
            )
        }
    }
}
