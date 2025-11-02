// com.example.travelvault.ui.components/WavyHeader.kt
package com.example.travelvault.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom header composable that draws a "wavy" background shape.
 * The content is layered on top of the wave.
 */
@Composable
fun WavyHeader(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp, // The total height of the header
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    // We'll use the 'surface' color (your dark card color) for the wave
    val waveColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = contentAlignment
    ) {
        // The Canvas allows us to draw custom 2D graphics
        Canvas(modifier = Modifier.fillMaxSize()) {
            // A Path defines the lines and curves of a shape
            val path = Path().apply {
                // Start at the top-left corner
                moveTo(0f, 0f)
                // Draw a line to the top-right corner
                lineTo(size.width, 0f)
                // Draw a line down to the bottom-right (80% of the height)
                lineTo(size.width, size.height * 0.8f)

                // --- This is the "Wave" ---
                // We use a quadratic Bézier curve to create a dip.
                //
                quadraticBezierTo(
                    x1 = size.width / 2, // The "control point" (middle-x)
                    y1 = size.height,     // The "control point" (bottom-y, creates the dip)
                    x2 = 0f,              // The end point (bottom-left-x)
                    y2 = size.height * 0.8f // The end point (bottom-left-y)
                )

                // Draw a line up to close the shape at the top-left
                close()
            }

            // Draw the path we just defined
            drawPath(
                path = path,
                color = waveColor,
                style = Fill
            )
        }

        // This is where the content (e.g., your "Upcoming Trips" text)
        // will be placed, on top of the Canvas.
        content()
    }
}