package com.example.altafedeltium.ui.components

import androidx.compose.foundation.background
// ...existing code...
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.altafedeltium.ui.theme.AccentText

@Composable
fun StepIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier,
    totalSteps: Int = 3,
    stepLabels: List<String> = listOf("Dati", "CV", "Video")
) {
    val activeColor = AccentText
    val doneColor = AccentText.copy(alpha = 0.5f)
    val idleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)

    // Two-row layout: top row with circles and connectors (connectors vertically centered with circles),
    // bottom row with labels aligned under each circle. Fixed-width slots for circles guarantee exact alignment.
    Column(modifier = modifier.fillMaxWidth()) {
        // Top row: circles and connecting lines
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            (1..totalSteps).forEach { step ->
                val isActive = step == currentStep
                val isDone = step < currentStep
                val circleColor = when {
                    isDone -> doneColor
                    isActive -> activeColor
                    else -> idleColor
                }

                // Fixed width slot for the circle so positions align with labels below
                Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.Center) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(circleColor)
                    ) {
                        Text(
                            text = if (isDone) "✓" else step.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Connector line (flexible) centered vertically relative to the circles
                if (step < totalSteps) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .padding(horizontal = 4.dp)
                            .background(if (step < currentStep) doneColor else lineColor)
                    )
                }
            }
        }

        // Bottom row: labels aligned exactly under each circle
        Row(modifier = Modifier.fillMaxWidth()) {
            (1..totalSteps).forEach { step ->
                // Label slot matches the width of the circle slot above
                Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = stepLabels.getOrElse(step - 1) { "Step $step" },
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (step == currentStep) activeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = if (step == currentStep) FontWeight.Bold else FontWeight.Normal
                    )
                }

                if (step < totalSteps) {
                    // Spacer matching the connector above
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

