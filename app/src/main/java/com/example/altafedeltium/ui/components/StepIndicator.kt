package com.example.altafedeltium.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int = 3,
    stepLabels: List<String> = listOf("Dati", "CV", "Video"),
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val doneColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    val idleColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (1..totalSteps).forEach { step ->
            val isActive = step == currentStep
            val isDone = step < currentStep
            val circleColor = when {
                isDone -> doneColor
                isActive -> activeColor
                else -> idleColor
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                Text(
                    text = stepLabels.getOrElse(step - 1) { "Step $step" },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) activeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                )
            }

            // Connector line between steps
            if (step < totalSteps) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (step < currentStep) doneColor else lineColor)
                        .align(Alignment.Top)
                        .padding(top = 17.dp)
                )
            }
        }
    }
}

