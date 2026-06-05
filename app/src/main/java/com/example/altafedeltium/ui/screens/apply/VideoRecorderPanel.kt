// Video composables have been consolidated into ApplicationScreen.kt
package com.example.altafedeltium.ui.screens.apply

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.altafedeltium.ui.viewmodel.ApplicationViewModel
import com.example.altafedeltium.ui.viewmodel.VideoRecordingState
import kotlinx.coroutines.delay

@Composable
fun VideoRecorderPanel(
    viewModel: ApplicationViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState

    // Countdown tick every second while recording
    val isRecording = uiState.videoState == VideoRecordingState.RECORDING
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000L)
                viewModel.onRecordingTick()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (uiState.videoState) {
            VideoRecordingState.IDLE -> IdleVideoPanel(onStartRecording = viewModel::onStartRecording)
            VideoRecordingState.RECORDING -> RecordingVideoPanel(
                countdownSeconds = uiState.videoCountdownSeconds,
                onStop = viewModel::onStopRecordingManually
            )
            VideoRecordingState.RECORDED -> RecordedVideoPanel(
                onRetake = viewModel::onRetakeVideo
            )
        }
    }
}

// ──────────────────────────────────────────────
// IDLE – prima della registrazione
// ──────────────────────────────────────────────

@Composable
private fun IdleVideoPanel(onStartRecording: () -> Unit) {
    // Instructions
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "💡 Cosa dire nel video?",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            listOf(
                "1️⃣  Chi sei: nome, età e dove abiti",
                "2️⃣  La tua esperienza: ruoli e settori in cui hai lavorato",
                "3️⃣  Perché vuoi questa posizione: motivazione e disponibilità"
            ).forEach { tip ->
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                "Non preoccuparti di essere perfetto. La spontaneità è apprezzata!",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Camera preview placeholder
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "La fotocamera si attiverà\nquando avvii la registrazione",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }

    // Record button
    Button(
        onClick = onStartRecording,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
    ) {
        Icon(
            Icons.Default.FiberManualRecord,
            contentDescription = null,
            tint = Color.White
        )
        Text(
            "  Registra Video di 30s",
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// ──────────────────────────────────────────────
// RECORDING – durante la registrazione
// ──────────────────────────────────────────────

@Composable
private fun RecordingVideoPanel(
    countdownSeconds: Int,
    onStop: () -> Unit
) {
    // Pulsing red dot animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    // Camera preview area with recording overlay
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1A2E))
            .border(2.dp, Color.Red, RoundedCornerShape(12.dp))
    ) {
        // REC indicator top-left
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red, CircleShape)
                    .alpha(alpha)
            )
            Text("REC", color = Color.White, style = MaterialTheme.typography.labelSmall)
        }

        // Countdown timer center
        Text(
            text = "${countdownSeconds}s",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            color = if (countdownSeconds <= 5) Color.Red else Color.White
        )

        // Instructions overlay bottom
        Text(
            text = "Stai andando bene! Continua a parlare…",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }

    // Stop button
    Button(
        onClick = onStop,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(Icons.Default.Stop, contentDescription = null)
        Text("  Ferma e Salva il Video")
    }
}

// ──────────────────────────────────────────────
// RECORDED – video completato
// ──────────────────────────────────────────────

@Composable
private fun RecordedVideoPanel(onRetake: () -> Unit) {
    // Success confirmation card
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(52.dp)
            )
            Text(
                "Video registrato! 🎉",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Text(
                "Ottimo lavoro! Il tuo video di presentazione è pronto per essere inviato insieme alla candidatura.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Color(0xFF388E3C)
            )
        }
    }

    // Retake option
    OutlinedButton(
        onClick = onRetake,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("🔄  Non mi piace, registro di nuovo")
    }

    // Nudge to proceed
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
                text = "💪 Sei pronto! Clicca \"Invia Candidatura\" per completare il processo.",
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

