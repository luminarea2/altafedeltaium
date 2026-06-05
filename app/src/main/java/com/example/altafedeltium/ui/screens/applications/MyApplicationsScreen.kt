package com.example.altafedeltium.ui.screens.applications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altafedeltium.data.model.ApplicationStatus
import com.example.altafedeltium.data.model.JobApplication
import com.example.altafedeltium.ui.viewmodel.MyApplicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplicationsScreen(
    onBack: () -> Unit,
    viewModel: MyApplicationsViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    // Refresh whenever screen is shown
    LaunchedEffect(Unit) { viewModel.refresh() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Le mie Candidature") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.applications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Text(
                        "Nessuna candidatura ancora",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Cerca una posizione e candidati in 3 semplici step!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Hai ${uiState.applications.size} candidatura${if (uiState.applications.size == 1) "" else "e"} attiva${if (uiState.applications.size == 1) "" else "e"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                items(uiState.applications, key = { it.id }) { application ->
                    ApplicationStatusCard(application = application)
                }
            }
        }
    }
}

@Composable
private fun ApplicationStatusCard(application: JobApplication) {
    val (bgColor, iconTint, icon) = statusVisuals(application.status)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: company + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = application.position.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = application.position.company,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Status
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stato:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = application.status.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = iconTint
                )
            }

            // Description from status
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = application.status.description,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Date + city
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Inviata il: ${application.appliedDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    "${application.position.city} · ${application.position.distanceKm} km",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private data class StatusVisuals(
    val backgroundColor: Color,
    val iconTint: Color,
    val icon: ImageVector
)

@Composable
private fun statusVisuals(status: ApplicationStatus): StatusVisuals {
    return when (status) {
        ApplicationStatus.INVIATA -> StatusVisuals(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            iconTint = MaterialTheme.colorScheme.secondary,
            icon = Icons.AutoMirrored.Filled.Send
        )
        ApplicationStatus.IN_REVISIONE -> StatusVisuals(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            iconTint = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.HourglassEmpty
        )
        ApplicationStatus.CONTATTATO -> StatusVisuals(
            backgroundColor = Color(0xFFE8F5E9),
            iconTint = Color(0xFF2E7D32),
            icon = Icons.Default.Email
        )
    }
}

