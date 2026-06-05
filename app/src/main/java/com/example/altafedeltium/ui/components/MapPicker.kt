package com.example.altafedeltium.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlin.math.roundToInt

@Composable
fun MapPickerDialog(
    initialLat: Double?,
    initialLon: Double?,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    val startLat = initialLat ?: 42.1127
    val startLon = initialLon ?: 14.7063
    val startPosition = startLat to startLon

    var selectedPosition by remember { mutableStateOf(startPosition) }
    var mapSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

    fun resetSelection() {
        selectedPosition = startPosition
    }

    fun updateFromTap(tap: Offset) {
        val width = mapSize.width.coerceAtLeast(1)
        val height = mapSize.height.coerceAtLeast(1)
        val normalizedX = (tap.x / width.toFloat()).coerceIn(0f, 1f)
        val normalizedY = (tap.y / height.toFloat()).coerceIn(0f, 1f)

        val deltaLon = (normalizedX - 0.5f) * 0.012
        val deltaLat = (0.5f - normalizedY) * 0.012
        selectedPosition = (startLat + deltaLat) to (startLon + deltaLon)
    }

    fun markerOffset(): IntOffset {
        val width = mapSize.width.coerceAtLeast(1).toFloat()
        val height = mapSize.height.coerceAtLeast(1).toFloat()
        val deltaLon = selectedPosition.second - startLon
        val deltaLat = selectedPosition.first - startLat
        val x = ((deltaLon / 0.012f) + 0.5f) * width
        val y = (0.5f - (deltaLat / 0.012f)) * height
        return IntOffset((x - 12f).roundToInt(), (y - 24f).roundToInt())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Controlla il punto di consegna") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(4.dp)) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Questa è una mappa finta di conferma: tocca il punto esatto dell'ingresso, poi conferma.",
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF4F0E8))
                        .border(1.dp, Color(0xFFD8CFC0), RoundedCornerShape(22.dp))
                        .onSizeChanged { mapSize = it }
                        .pointerInput(Unit) {
                            detectTapGestures { tap -> updateFromTap(tap) }
                        }
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val road = Color(0xFFC1B7A6)
                        val accent = Color(0xFFE4DDCF)
                        val park = Color(0xFFDCE8D7)
                        val river = Color(0xFFCFE3F5)

                        drawRect(color = park, topLeft = Offset(size.width * 0.08f, size.height * 0.12f), size = androidx.compose.ui.geometry.Size(size.width * 0.18f, size.height * 0.16f))
                        drawRect(color = river, topLeft = Offset(size.width * 0.72f, size.height * 0.06f), size = androidx.compose.ui.geometry.Size(size.width * 0.18f, size.height * 0.24f))

                        drawLine(road, Offset(size.width * 0.10f, size.height * 0.20f), Offset(size.width * 0.90f, size.height * 0.20f), strokeWidth = 18f, cap = StrokeCap.Round)
                        drawLine(road, Offset(size.width * 0.20f, size.height * 0.08f), Offset(size.width * 0.20f, size.height * 0.92f), strokeWidth = 16f, cap = StrokeCap.Round)
                        drawLine(road, Offset(size.width * 0.62f, size.height * 0.08f), Offset(size.width * 0.62f, size.height * 0.92f), strokeWidth = 16f, cap = StrokeCap.Round)
                        drawLine(road, Offset(size.width * 0.12f, size.height * 0.70f), Offset(size.width * 0.92f, size.height * 0.70f), strokeWidth = 18f, cap = StrokeCap.Round)
                        drawLine(accent, Offset(size.width * 0.10f, size.height * 0.50f), Offset(size.width * 0.90f, size.height * 0.50f), strokeWidth = 4f)
                        drawLine(accent, Offset(size.width * 0.40f, size.height * 0.08f), Offset(size.width * 0.40f, size.height * 0.92f), strokeWidth = 4f)
                    }

                    Box(
                        modifier = Modifier
                            .offset { markerOffset() }
                            .size(24.dp)
                    ) {
                        Text(
                            text = "📍",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Text(
                        text = "Tocca qui per scegliere l'ingresso",
                        color = Color(0xFF6A6258),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                    )
                }

                Text(
                    text = "Posizione scelta: ${"%.5f".format(selectedPosition.first)} , ${"%.5f".format(selectedPosition.second)}",
                    modifier = Modifier.padding(top = 2.dp)
                )

                OutlinedButton(onClick = { resetSelection() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Riporta al centro")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(selectedPosition.first, selectedPosition.second)
            }) { Text("Conferma posizione") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}