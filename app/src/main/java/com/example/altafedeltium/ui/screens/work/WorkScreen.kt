package com.example.altafedeltium.ui.screens.work

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.altafedeltium.ui.components.JobCard
import com.example.altafedeltium.ui.viewmodel.JobSearchViewModel
import com.example.altafedeltium.ui.theme.AccentText
import com.example.altafedeltium.ui.theme.MainOrange
import com.example.altafedeltium.data.model.JobCategory
import com.example.altafedeltium.data.model.JobSortField
import com.example.altafedeltium.data.model.JobSortDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkScreen(
    viewModel: JobSearchViewModel,
    onApply: (Int) -> Unit,
    onMyApplications: () -> Unit,
    onOpenFavoritesJobs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState
    val filtered = viewModel.filteredPositions()
    var showFiltersPopup by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        val activeFiltersSummary = viewModel.activeFiltersSummary()

        // Intera schermata resa scrollabile: header, barra di ricerca, filtri attivi e lista posizioni
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                // Header card styled like HomeHeaderCard but adapted for Work
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Benvenuto nella sezione Lavoro", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Posizioni salvate: ${uiState.favoriteJobIds.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = onOpenFavoritesJobs) {
                            Text("Apri salvati")
                        }
                    }
                }
            }

            item {
                // ── Barra di ricerca + Bottone Filtri ─────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Tune, contentDescription = null)
                                Column {
                                    Text("Cerca, filtra e ordina", fontWeight = FontWeight.SemiBold)
                                    Text("Apri i filtri dal pulsante accanto alla ricerca.", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = viewModel::onSearchQueryChanged,
                                label = { Text("Cerca ruolo o azienda…") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    cursorColor = AccentText,
                                    focusedBorderColor = AccentText,
                                    focusedLabelColor = AccentText
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CityDropdown(
                                    cities = viewModel.cities,
                                    selected = uiState.selectedCity,
                                    onSelected = viewModel::onCitySelected,
                                    modifier = Modifier.weight(1f)
                                )

                                Button(onClick = { showFiltersPopup = true }, modifier = Modifier.size(52.dp), contentPadding = PaddingValues(0.dp)) {
                                    Icon(Icons.Default.Tune, contentDescription = "Apri filtri")
                                }
                            }
                        }
                    }
                }
            }

            // ── Filtri Attivi (chip rimovibili) ─────────────────────────────
            if (viewModel.hasActiveFilters()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Filtri attivi",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.SemiBold
                            )
                            TextButton(
                                onClick = viewModel::resetFilters,
                                modifier = Modifier.height(28.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Resetta filtri",
                                    modifier = Modifier.size(14.dp),
                                    tint = AccentText
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Resetta tutto", style = MaterialTheme.typography.labelSmall, color = AccentText)
                            }
                        }

                        // Chips singoli per categoria / città / distanza / ricerca
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Categorie selezionate (escludi TUTTI)
                            val categorieMostrate = uiState.selectedCategories.filter { it != JobCategory.TUTTI }
                            items(categorieMostrate) { category ->
                                AssistChip(
                                    onClick = { viewModel.onCategorySelected(category) },
                                    label = { Text(category.label) },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Rimuovi filtro",
                                            modifier = Modifier.size(16.dp),
                                            tint = AccentText
                                        )
                                    },
                                    modifier = Modifier.height(32.dp)
                                )
                            }

                            // Città
                            if (uiState.selectedCity != "Tutte le città") {
                                item {
                                    AssistChip(
                                        onClick = { viewModel.onCitySelected("Tutte le città") },
                                        label = { Text(uiState.selectedCity) },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Rimuovi filtro città", modifier = Modifier.size(16.dp), tint = AccentText)
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }

                            // Distanza
                            if (uiState.maxDistanceKm != viewModel.distanceOptions.maxOrNull()) {
                                item {
                                    AssistChip(
                                        onClick = { viewModel.onMaxDistanceChanged(viewModel.distanceOptions.maxOrNull() ?: 100) },
                                        label = { Text("≤ ${uiState.maxDistanceKm} km") },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Rimuovi filtro distanza", modifier = Modifier.size(16.dp), tint = AccentText)
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }

                            // Tipo contratto (Full-time / Part-time)
                            if (uiState.selectedContractType != "Tutti") {
                                item {
                                    AssistChip(
                                        onClick = { viewModel.onContractTypeSelected("Tutti") },
                                        label = { Text(uiState.selectedContractType) },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Rimuovi filtro contratto", modifier = Modifier.size(16.dp), tint = AccentText)
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }

                            // Ordinamento (campo)
                            if (uiState.selectedSortField != JobSortField.DISTANCE) {
                                item {
                                    AssistChip(
                                        onClick = { viewModel.onSortFieldChanged(JobSortField.DISTANCE) },
                                        label = { Text("Ordina: ${uiState.selectedSortField.label}") },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Rimuovi filtro ordinamento", modifier = Modifier.size(16.dp), tint = AccentText)
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }

                            // Ordinamento (direzione)
                            if (uiState.selectedSortDirection != JobSortDirection.ASC) {
                                item {
                                    AssistChip(
                                        onClick = { viewModel.onSortDirectionChanged(JobSortDirection.ASC) },
                                        label = { Text(uiState.selectedSortDirection.label) },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Rimuovi filtro direzione", modifier = Modifier.size(16.dp), tint = AccentText)
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }

                            // Ricerca testo
                            if (uiState.searchQuery.isNotBlank()) {
                                item {
                                    AssistChip(
                                        onClick = { viewModel.onSearchQueryChanged("") },
                                        label = { Text("\"${uiState.searchQuery}\"") },
                                        trailingIcon = {
                                            Icon(Icons.Default.Close, contentDescription = "Rimuovi ricerca", modifier = Modifier.size(16.dp), tint = AccentText)
                                        },
                                        modifier = Modifier.height(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Lista Posizioni ───────────────────────────────────────
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Nessuna posizione trovata",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Prova ad aumentare la distanza o cambia filtro",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                // mostra un conteggio opzionale e poi tutte le JobCard
                if (activeFiltersSummary.isNotEmpty()) {
                    item {
                        Text(
                            text = "${filtered.size} posizioni trovate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                items(filtered, key = { it.id }) { position ->
                    JobCard(
                        position = position,
                        onApply = { onApply(position.id) },
                        isFavorite = viewModel.isFavorite(position.id),
                        onToggleFavorite = { viewModel.toggleFavorite(position.id) }
                    )
                }
            }

            // Spazio inferiore per evitare che il FAB copra l'ultimo elemento
            item {
                Spacer(modifier = Modifier.height(88.dp))
            }
        }

        // ── Popup Filtri ──────────────────────────────────────────
        if (showFiltersPopup) {
            JobFiltersPopupDialog(
                cities = viewModel.cities,
                selectedCity = uiState.selectedCity,
                maxDistanceKm = uiState.maxDistanceKm,
                distanceOptions = viewModel.distanceOptions,
                categories = viewModel.categories,
                selectedCategories = uiState.selectedCategories,
                selectedContractType = uiState.selectedContractType,
                selectedSortField = uiState.selectedSortField,
                selectedSortDirection = uiState.selectedSortDirection,
                onCitySelected = viewModel::onCitySelected,
                onMaxDistanceChanged = viewModel::onMaxDistanceChanged,
                onCategorySelected = viewModel::onCategorySelected,
                onContractTypeSelected = viewModel::onContractTypeSelected,
                onSortFieldChanged = viewModel::onSortFieldChanged,
                onSortDirectionChanged = viewModel::onSortDirectionChanged,
                onDismiss = { showFiltersPopup = false }
            )
        }

        ExtendedFloatingActionButton(
            onClick = onMyApplications,
            icon = { Icon(Icons.Default.WorkHistory, contentDescription = null) },
            text = { Text("Le mie Candidature") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun JobFiltersPopupDialog(
     cities: List<String>,
     selectedCity: String,
     maxDistanceKm: Int,
     distanceOptions: List<Int>,
     categories: List<com.example.altafedeltium.data.model.JobCategory>,
     selectedCategories: Set<com.example.altafedeltium.data.model.JobCategory>,
     selectedContractType: String,
     selectedSortField: com.example.altafedeltium.data.model.JobSortField,
     selectedSortDirection: com.example.altafedeltium.data.model.JobSortDirection,
     onCitySelected: (String) -> Unit,
     onMaxDistanceChanged: (Int) -> Unit,
     onCategorySelected: (com.example.altafedeltium.data.model.JobCategory) -> Unit,
     onContractTypeSelected: (String) -> Unit,
     onSortFieldChanged: (com.example.altafedeltium.data.model.JobSortField) -> Unit,
     onSortDirectionChanged: (com.example.altafedeltium.data.model.JobSortDirection) -> Unit,
     onDismiss: () -> Unit
 ) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filtri", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Chiudi filtri")
                    }
                }

                // ── Filtro Ordinamento (PRIMO) ────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ordina per", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(com.example.altafedeltium.data.model.JobSortField.entries.toList()) { field ->
                            FilterChip(
                                selected = field == selectedSortField,
                                onClick = { onSortFieldChanged(field) },
                                label = { Text(field.label, style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }

                // ── Direzione Ordinamento (SECONDO) ─────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Direzione", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(com.example.altafedeltium.data.model.JobSortDirection.entries.toList()) { direction ->
                            FilterChip(
                                selected = direction == selectedSortDirection,
                                onClick = { onSortDirectionChanged(direction) },
                                label = { Text(direction.label, style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }

                // ...existing code... (Slider stipendio rimosso come richiesto)

                // (Filtro Città spostato nella barra di ricerca principale)

                // ── Filtro Distanza ───────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Distanza massima", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(distanceOptions) { km ->
                            FilterChip(
                                selected = maxDistanceKm == km,
                                onClick = { onMaxDistanceChanged(km) },
                                label = { Text("≤ $km km", style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }

                // ── Filtro Contratto (Full-time / Part-time)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tipo contratto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    val contractOptions = listOf("Tutti", "Full-time", "Part-time")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(contractOptions) { ct ->
                            FilterChip(
                                selected = selectedContractType == ct,
                                onClick = { onContractTypeSelected(ct) },
                                label = { Text(ct, style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }

                // ── Filtro Ruolo ──────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ruolo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategories.contains(category),
                                onClick = { onCategorySelected(category) },
                                label = { Text(category.label, style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }


                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Applica filtri")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CityDropdown(
    cities: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Città") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = AccentText,
                focusedBorderColor = AccentText,
                focusedLabelColor = AccentText
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city) },
                    onClick = {
                        onSelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}
