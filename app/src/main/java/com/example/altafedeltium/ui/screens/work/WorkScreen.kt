package com.example.altafedeltium.ui.screens.work

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.altafedeltium.ui.components.JobCard
import com.example.altafedeltium.ui.viewmodel.JobSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkScreen(
    viewModel: JobSearchViewModel,
    onApply: (Int) -> Unit,
    onMyApplications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState
    val filtered = viewModel.filteredPositions()
    var showFiltersPopup by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Opportunità di Lavoro") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onMyApplications,
                icon = { Icon(Icons.Default.WorkHistory, contentDescription = null) },
                text = { Text("Le mie Candidature") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // ── Barra di ricerca + Bottone Filtri ─────────────────────────
             Row(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(horizontal = 16.dp),
                 horizontalArrangement = Arrangement.spacedBy(8.dp),
                 verticalAlignment = Alignment.CenterVertically
             ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    label = { Text("Cerca ruolo o azienda…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
                Button(onClick = { showFiltersPopup = true }, modifier = Modifier.size(52.dp), contentPadding = PaddingValues(0.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = "Apri filtri")
                }
            }

            // ── Lista Posizioni ───────────────────────────────────────
            if (filtered.isEmpty()) {
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
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${filtered.size} posizione${if (filtered.size == 1) "" else "i"} trovata${if (filtered.size == 1) "" else "e"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    items(filtered, key = { it.id }) { position ->
                        JobCard(
                            position = position,
                            onApply = { onApply(position.id) }
                        )
                    }
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
                      selectedCategory = uiState.selectedCategory,
                      selectedSortField = uiState.selectedSortField,
                      selectedSortDirection = uiState.selectedSortDirection,
                      onCitySelected = viewModel::onCitySelected,
                      onMaxDistanceChanged = viewModel::onMaxDistanceChanged,
                      onCategorySelected = viewModel::onCategorySelected,
                      onSortFieldChanged = viewModel::onSortFieldChanged,
                      onSortDirectionChanged = viewModel::onSortDirectionChanged,
                      onDismiss = { showFiltersPopup = false }
                  )
             }
        }
    }
}

@Composable
private fun JobFiltersPopupDialog(
     cities: List<String>,
     selectedCity: String,
     maxDistanceKm: Int,
     distanceOptions: List<Int>,
     categories: List<com.example.altafedeltium.data.model.JobCategory>,
     selectedCategory: com.example.altafedeltium.data.model.JobCategory,
     selectedSortField: com.example.altafedeltium.data.model.JobSortField,
     selectedSortDirection: com.example.altafedeltium.data.model.JobSortDirection,
     onCitySelected: (String) -> Unit,
     onMaxDistanceChanged: (Int) -> Unit,
     onCategorySelected: (com.example.altafedeltium.data.model.JobCategory) -> Unit,
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
                    Text("Ordina per", fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(com.example.altafedeltium.data.model.JobSortField.entries.toList()) { field ->
                            FilterChip(
                                selected = field == selectedSortField,
                                onClick = { onSortFieldChanged(field) },
                                label = { Text(field.label) }
                            )
                        }
                    }
                }

                // ── Direzione Ordinamento (SECONDO) ─────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Direzione", fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(com.example.altafedeltium.data.model.JobSortDirection.entries.toList()) { direction ->
                            FilterChip(
                                selected = direction == selectedSortDirection,
                                onClick = { onSortDirectionChanged(direction) },
                                label = { Text(direction.label) }
                            )
                        }
                    }
                }

                // ...existing code... (Slider stipendio rimosso come richiesto)

                // ── Filtro Città ──────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Città", fontWeight = FontWeight.Medium)
                    CityDropdown(
                        cities = cities,
                        selected = selectedCity,
                        onSelected = onCitySelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ── Filtro Distanza ───────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Distanza massima", fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(distanceOptions) { km ->
                            FilterChip(
                                selected = maxDistanceKm == km,
                                onClick = { onMaxDistanceChanged(km) },
                                label = { Text("≤ $km km") }
                            )
                        }
                    }
                }

                // ── Filtro Ruolo ──────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ruolo", fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            FilterChip(
                                selected = category == selectedCategory,
                                onClick = { onCategorySelected(category) },
                                label = { Text(category.label) }
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
                .fillMaxWidth()
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

