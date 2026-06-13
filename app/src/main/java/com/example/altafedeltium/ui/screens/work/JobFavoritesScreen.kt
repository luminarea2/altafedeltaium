package com.example.altafedeltium.ui.screens.work

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.altafedeltium.data.model.JobPosition
import com.example.altafedeltium.ui.components.ConfirmationDialog
import com.example.altafedeltium.ui.components.JobCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobFavoritesScreen(
    favorites: List<JobPosition>,
    onBack: () -> Unit,
    onOpenJob: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onApply: (Int) -> Unit
) {
    var jobToRemove by remember { mutableStateOf<JobPosition?>(null) }
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Offerte Salvate") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                }
            }
        )

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(24.dp)) {
                    Text("Non hai ancora offerte salvate", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Scegli un'offerta e premi il segnalibro per salvarla",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites, key = { it.id }) { job ->
                    JobCard(
                        position = job,
                        onApply = { onApply(job.id) },
                        isFavorite = true,
                        onToggleFavorite = { jobToRemove = job }
                    )
                }
            }
        }
    }

    jobToRemove?.let { j ->
        ConfirmationDialog(
            title = "Rimuovi offerta salvata",
            message = "Sei sicuro di voler rimuovere \"${j.title}\" dai salvati?",
            confirmLabel = "Rimuovi",
            dismissLabel = "Annulla",
            onConfirm = {
                onToggleFavorite(j.id)
                jobToRemove = null
            },
            onDismiss = { jobToRemove = null },
            destructiveConfirm = true
        )
    }
}



