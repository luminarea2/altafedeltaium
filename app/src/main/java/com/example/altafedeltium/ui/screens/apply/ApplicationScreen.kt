package com.example.altafedeltium.ui.screens.apply

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
// ...removed unused Material3 imports (top app bar / scaffold / slider)
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.altafedeltium.ui.theme.AccentText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altafedeltium.ui.components.ConfirmationDialog
import com.example.altafedeltium.ui.components.StepIndicator
import com.example.altafedeltium.ui.viewmodel.ApplicationViewModel
import com.example.altafedeltium.ui.viewmodel.HomeViewModel
import com.example.altafedeltium.ui.viewmodel.VideoRecordingState
import kotlinx.coroutines.delay
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.filled.Replay

// ══════════════════════════════════════════════════════════
//  ApplicationScreen – entry point (3-step funnel)
// ══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(
    jobId: Int,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ApplicationViewModel = viewModel(),
    homeViewModel: HomeViewModel? = null
) {
    val uiState by viewModel.uiState

    LaunchedEffect(jobId) { viewModel.setJob(jobId) }
    LaunchedEffect(uiState.submitted) { if (uiState.submitted) onSuccess() }

    // if a phone is present in the user's profile, prefill the application phone field
    homeViewModel?.let { hv ->
        val homeState by hv.uiState
        LaunchedEffect(homeState.userProfile.phone) {
            if (uiState.phone.isBlank() && homeState.userProfile.phone.isNotBlank()) {
                viewModel.onPhoneChanged(homeState.userProfile.phone)
            }
        }
    }

    if (uiState.showConfirmDialog) {
        ConfirmationDialog(
            title = "Inviare la candidatura?",
            message = "Sei sicuro di voler inviare la candidatura a ${uiState.jobPosition?.company}?\n\nUna volta inviata non potrai modificarla.",
            confirmLabel = "Sì, Invia!",
            dismissLabel = "Aspetta",
            onConfirm = viewModel::onConfirmSubmit,
            onDismiss = viewModel::onDismissConfirmDialog
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar (prima era topBar di Scaffold)
        // Header background forced to white and label fixed to "Candidatura" per UI requirement
        Surface(
            color = Color.White,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (uiState.currentStep > 1) viewModel.onPreviousStep() else onBack()
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
                Text(
                    text = "Candidatura",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            StepIndicator(
                currentStep = uiState.currentStep,
                stepLabels = listOf("Dati Personali", "Carica CV", "Video")
            )

            Spacer(modifier = Modifier.height(4.dp))

            uiState.jobPosition?.let { job ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(job.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            "${job.company} · ${job.city} (${job.distanceKm} km)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            when (uiState.currentStep) {
                1 -> StepOnePersonalData(viewModel = viewModel)
                2 -> StepTwoCvUpload(viewModel = viewModel)
                3 -> StepThreeVideo(viewModel = viewModel)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.currentStep > 1) {
                    OutlinedButton(
                        onClick = viewModel::onPreviousStep,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
                    ) {
                        Text("Indietro")
                    }
                }

                // abilita il pulsante principale solo se i requisiti del passo corrente sono soddisfatti
                // Nota: per il passo 3 l'utente deve aver ACCETTATO esplicitamente il video oppure aver scelto di non registrarlo
                val primaryEnabled = when (uiState.currentStep) {
                    1 -> true // la validazione viene eseguita onNextStep()
                    2 -> uiState.cvUploaded
                    3 -> uiState.videoAccepted || uiState.videoOptOut
                    else -> true
                }

                Button(onClick = viewModel::onNextStep, modifier = Modifier.weight(1f), enabled = primaryEnabled) {
                    Text(if (uiState.currentStep == 3) "Invia Candidatura" else "Avanti →")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════
//  STEP 1 – Dati Personali
// ══════════════════════════════════════════════════════════

@Composable
private fun StepOnePersonalData(viewModel: ApplicationViewModel) {
    val uiState by viewModel.uiState
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Step 1: I tuoi dati di contatto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "Inserisci le tue informazioni per procedere con la candidatura.",
            style = MaterialTheme.typography.bodySmall,
            color = AccentText
        )
        OutlinedTextField(
            value = uiState.fullName,
            onValueChange = viewModel::onFullNameChanged,
            label = { Text("Nome e Cognome *") },
            placeholder = { Text("Es. Mario Rossi") },
            isError = uiState.fullNameError != null,
            supportingText = {
                uiState.fullNameError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = AccentText,
                focusedBorderColor = AccentText,
                focusedLabelColor = AccentText
            )
        )
            OutlinedTextField(
            value = uiState.phone,
            onValueChange = viewModel::onPhoneChanged,
            label = { Text("Numero di Telefono *") },
            placeholder = { Text("Es. 333 123 4567") },
            isError = uiState.phoneError != null,
            supportingText = {
                uiState.phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = AccentText,
                focusedBorderColor = AccentText,
                focusedLabelColor = AccentText
            )
        )
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            label = { Text("Email *") },
            placeholder = { Text("Es. mario.rossi@email.it") },
            isError = uiState.emailError != null,
            supportingText = {
                uiState.emailError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = AccentText,
                focusedBorderColor = AccentText,
                focusedLabelColor = AccentText
            )
        )
    }
}

// ══════════════════════════════════════════════════════════
//  STEP 2 – Caricamento CV
// ══════════════════════════════════════════════════════════

@Composable
private fun StepTwoCvUpload(viewModel: ApplicationViewModel) {
    val uiState by viewModel.uiState
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment
                ?.substringAfterLast("/")
                ?.substringAfterLast("%2F")
                ?: "curriculum.pdf"
            viewModel.onCvSelected(fileName)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Step 2: Carica il tuo CV", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "Carica il tuo curriculum in formato PDF. Il selezionatore lo esaminerà prima del colloquio.",
            style = MaterialTheme.typography.bodySmall,
            color = AccentText
        )
        if (!uiState.cvUploaded) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(48.dp), tint = AccentText)
                    Text("Nessun CV caricato", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = AccentText)
                    Text("Formato accettato: PDF", style = MaterialTheme.typography.labelSmall, color = AccentText)
                    Button(onClick = { pdfPickerLauncher.launch("application/pdf") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Seleziona CV dal dispositivo")
                    }
                }
            }
            TextButton(
                onClick = { viewModel.onCvSelected("curriculum_non_caricato.pdf") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Salta per ora", color = AccentText) }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CV caricato con successo! ✅", style = MaterialTheme.typography.titleSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text(uiState.cvFileName ?: "curriculum.pdf", style = MaterialTheme.typography.bodySmall, color = Color(0xFF388E3C))
                    }
                    IconButton(onClick = viewModel::onCvRemoved) {
                        Icon(Icons.Default.Close, contentDescription = "Rimuovi CV", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Text(
                    "✔ È il file corretto? Puoi rimuoverlo con la X e caricarne un altro.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════
//  STEP 3 – Video Presentazione (wrapper)
// ══════════════════════════════════════════════════════════

@Composable
private fun StepThreeVideo(viewModel: ApplicationViewModel) {
    val uiState by viewModel.uiState

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Step 3: Video Presentazione", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            "Un video di 30 secondi aumenta del 70% le probabilità di essere contattato. Non serve essere perfetti!",
            style = MaterialTheme.typography.bodySmall,
            // leggermente più scuro per miglior contrasto (mantiene tinta primaria arancione)
            color = AccentText
        )

        when {
            uiState.videoOptOut -> {
                // feedback simile al pannello registrato ma per l'opt-out
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = AccentText, modifier = Modifier.size(52.dp))
                        Text("Hai scelto di non registrare il video", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "Puoi comunque inviare la candidatura. Se cambi idea, premi 'Ho cambiato idea, fammi registrare' per registrare il video.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = AccentText
                        )
                    }
                }
                Button(onClick = viewModel::onRetakeVideo, modifier = Modifier.fillMaxWidth()) {
                    Text("Ho cambiato idea, fammi registrare")
                }
            }

            uiState.videoState == VideoRecordingState.RECORDED -> {
                VideoRecordedPanel(viewModel = viewModel)
            }

            else -> {
                VideoRecorderPanel(viewModel = viewModel)
                // mostra il pulsante di skip solo se non si sta registrando un video
                if (uiState.videoState != VideoRecordingState.RECORDING) {
                    TextButton(
                        onClick = viewModel::onSkipVideo,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salta per ora", color = AccentText)
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════
//  VIDEO RECORDER PANEL
// ══════════════════════════════════════════════════════════

@Composable
private fun VideoRecorderPanel(viewModel: ApplicationViewModel) {
    val uiState by viewModel.uiState
    val isRecording = uiState.videoState == VideoRecordingState.RECORDING

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (true) {
                delay(1000L)
                viewModel.onRecordingTick()
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (uiState.videoState) {
            VideoRecordingState.IDLE -> VideoIdlePanel(onStartRecording = viewModel::onStartRecording)
            VideoRecordingState.RECORDING -> VideoRecordingPanel(
                countdownSeconds = uiState.videoCountdownSeconds,
                onStop = viewModel::onStopRecordingManually
            )
            VideoRecordingState.RECORDED -> VideoRecordedPanel(viewModel = viewModel)
        }
    }
}

@Composable
private fun VideoIdlePanel(onStartRecording: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("💡 Cosa dire nel video?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            listOf(
                "1️⃣  Chi sei: nome, età e dove abiti",
                "2️⃣  La tua esperienza: ruoli e settori in cui hai lavorato",
                "3️⃣  Perché vuoi questa posizione: motivazione e disponibilità"
            ).forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
             Text(
                "Non preoccuparti di essere perfetto. La spontaneità è apprezzata!",
                style = MaterialTheme.typography.labelSmall,
                color = AccentText,
                fontWeight = FontWeight.Medium
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A1A2E)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("La fotocamera si attiverà\nquando avvii la registrazione", textAlign = TextAlign.Center, color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall)
        }
    }
    Button(
        onClick = onStartRecording,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
    ) {
        Icon(Icons.Default.FiberManualRecord, contentDescription = null, tint = Color.White)
        Text("  Registra Video di 30s", color = Color.White, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun VideoRecordingPanel(countdownSeconds: Int, onStop: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(animation = tween(600), repeatMode = RepeatMode.Reverse),
        label = "dotAlpha"
    )
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A1A2E))
            .border(2.dp, Color.Red, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(10.dp).background(Color.Red, CircleShape).alpha(dotAlpha))
            Text("REC", color = Color.White, style = MaterialTheme.typography.labelSmall)
        }
        Text(
            text = "${countdownSeconds}s",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            color = if (countdownSeconds <= 5) Color.Red else Color.White
        )
        Text(
            text = "Stai andando bene! Continua a parlare…",
            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White, style = MaterialTheme.typography.labelSmall
        )
    }
    Button(
        onClick = onStop,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Icon(Icons.Default.Stop, contentDescription = null)
        Text("  Ferma e Salva il Video")
    }
}

@Composable
private fun VideoRecordedPanel(viewModel: ApplicationViewModel) {
    val uiState by viewModel.uiState
    val onRetake = viewModel::onRetakeVideo

    // If user already accepted the video, show a green confirmation message in place of the review
    if (uiState.videoAccepted) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(52.dp))
                    Text("Video registrato! 🎉", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text(
                        "Ottimo lavoro! Il tuo video di presentazione è pronto per essere inviato insieme alla candidatura.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF388E3C)
                    )
                }
            }

            // Allow user to change their mind and re-record after accepting
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
            ) {
                Text("Registra di nuovo")
            }
        }
        return
    }

    // --- Mock video player (sfondo nero) ---
    val totalSeconds = 30
    var progress by remember { mutableStateOf(0f) } // 0..1
    var isPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying && progress < 1f) {
                delay(300L)
                val increment = (300f / 1000f) / totalSeconds.toFloat()
                progress = (progress + increment).coerceAtMost(1f)
                if (progress >= 1f) isPlaying = false
            }
        }
    }

    fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%d:%02d".format(m, s)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Always show overlay controls while reviewing so user can replay/seek
        val isFinished = progress >= 1f
        IconButton(
            onClick = {
                if (isFinished) {
                    progress = 0f
                    isPlaying = true
                } else {
                    isPlaying = !isPlaying
                }
            },
            modifier = Modifier
                .size(72.dp)
                .background(Color.Black.copy(alpha = 0.35f), CircleShape)
        ) {
            Icon(
                when {
                    isFinished -> Icons.Default.Replay
                    isPlaying -> Icons.Default.Pause
                    else -> Icons.Default.PlayArrow
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        // Bottom controls: time / progress track / total
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val elapsed = (progress * totalSeconds).toInt()
            Text(formatTime(elapsed), color = Color.White, style = MaterialTheme.typography.labelSmall)
            var barWidthPx by remember { mutableStateOf(0) }
            var draggingAllowed by remember { mutableStateOf(false) }
            val density = LocalDensity.current
            val touchSlopPx = with(density) { 24.dp.toPx() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp, end = 8.dp, top = 6.dp)
                    .height(6.dp)
                    .onSizeChanged { barWidthPx = it.width }
                    .clip(RoundedCornerShape(3.dp))
                    .background(AccentText.copy(alpha = 0.12f))
                    .pointerInput(Unit) {
                        detectTapGestures { offset: Offset ->
                            if (barWidthPx > 0) {
                                val newProgress = (offset.x.coerceIn(0f, barWidthPx.toFloat()) / barWidthPx.toFloat())
                                progress = newProgress.coerceIn(0f, 1f)
                                isPlaying = false
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset: Offset ->
                                if (barWidthPx > 0) {
                                    val filledEnd = progress * barWidthPx
                                    draggingAllowed = offset.x <= (filledEnd + touchSlopPx)
                                } else draggingAllowed = false
                            },
                            onDrag = { change: PointerInputChange, _ ->
                                if (!draggingAllowed) return@detectDragGestures
                                val x = change.position.x
                                if (barWidthPx > 0) {
                                    val newProgress = (x.coerceIn(0f, barWidthPx.toFloat()) / barWidthPx.toFloat())
                                    progress = newProgress.coerceAtMost(1f).coerceAtLeast(0f)
                                    isPlaying = false
                                }
                            },
                            onDragEnd = { draggingAllowed = false },
                            onDragCancel = { draggingAllowed = false }
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(AccentText)
                )
            }
            Text(formatTime(totalSeconds), color = Color.White, style = MaterialTheme.typography.labelSmall)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // If we are awaiting user's decision, show prompt with two buttons and disable sending until user chooses
    if (uiState.videoAwaitingUploadConfirmation) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Vuoi caricare il video?", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { viewModel.onRejectVideo() }) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = AccentText)
                        Text("  Rifiuta", color = AccentText)
                    }
                    Button(onClick = { viewModel.onAcceptVideo() }) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Text("  Accetta")
                    }
                }
            }
        }
    } else {
        // default: allow retake and show the "Sei pronto!" nudger
        OutlinedButton(
            onClick = onRetake,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
        ) {
            Text("Registra di nuovo")
        }

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Text(
                text = "Sei pronto! Clicca \"Invia Candidatura\" per completare il processo.",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

