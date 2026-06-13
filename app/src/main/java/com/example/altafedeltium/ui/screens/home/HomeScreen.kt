package com.example.altafedeltium.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.example.altafedeltium.ui.utils.showTimedSnackbar
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.altafedeltium.data.model.Product
import com.example.altafedeltium.data.model.SortDirection
import com.example.altafedeltium.data.model.SortField
import com.example.altafedeltium.ui.viewmodel.HomeViewModel
import com.example.altafedeltium.ui.theme.AccentText

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onOpenProduct: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by homeViewModel.uiState
    val filteredProducts = homeViewModel.filteredProducts()
    val snackbarHostState = remember { SnackbarHostState() }
    // track previous quantities to detect first-add and full-remove
    val prevQuantities = remember { mutableStateMapOf<Int, Int>() }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
        item {
            HomeHeaderCard(
                favoritesCount = uiState.favoriteProductIds.size,
                cartCount = homeViewModel.cartItemsCount(),
                onOpenFavorites = onOpenFavorites
            )
        }

        item {
            SearchFiltersCard(
                searchQuery = uiState.searchQuery,
                selectedCategories = uiState.selectedCategories,
                categories = homeViewModel.categories,
                selectedSortField = uiState.selectedSortField,
                selectedSortDirection = uiState.selectedSortDirection,
                maxPriceFilter = uiState.maxPriceFilter,
                maxPriceLimit = homeViewModel.maxPriceLimit,
                onSearchQueryChanged = homeViewModel::onSearchQueryChanged,
                onCategorySelected = homeViewModel::onCategorySelected,
                onSortFieldChanged = homeViewModel::onSortFieldChanged,
                onSortDirectionChanged = homeViewModel::onSortDirectionChanged,
                onMaxPriceChanged = { homeViewModel.onMaxPriceChanged(it) },
                onResetFilters = homeViewModel::resetFilters
            )
        }

        items(filteredProducts, key = { it.id }) { product ->
            val cartQuantity = uiState.cartItems.firstOrNull { it.product.id == product.id }?.quantity ?: 0
            ProductCard(
                product = product,
                isFavorite = homeViewModel.isFavorite(product.id),
                cartQuantity = cartQuantity,
                onToggleFavorite = { homeViewModel.toggleFavorite(product.id) },
                onOpenDetail = { onOpenProduct(product.id) },
                onAdd = { homeViewModel.addToCart(product) },
                onIncreaseQuantity = { homeViewModel.increaseQuantity(product.id) },
                onDecreaseQuantity = { homeViewModel.decreaseQuantity(product.id) }
            )
        }
    }

        // snackbar host overlay
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

    // observe cart items map changes and show snackbar only on 0->>0 (first add) and >0->0 (removed)
    LaunchedEffect(Unit) {
        snapshotFlow { uiState.cartItems.associate { it.product.id to it.quantity } }
            .collect { currentMap ->
                for (product in filteredProducts) {
                    val prev = prevQuantities[product.id] ?: 0
                    val curr = currentMap[product.id] ?: 0
                    if (prev == 0 && curr > 0) {
                        snackbarHostState.showTimedSnackbar("✓ Prodotto aggiunto al carrello!")
                    } else if (prev > 0 && curr == 0) {
                        snackbarHostState.showTimedSnackbar("✓ Prodotto rimosso dal carrello")
                    }
                    prevQuantities[product.id] = curr
                }
            }
    }
}

@Composable
private fun HomeHeaderCard(
    favoritesCount: Int,
    cartCount: Int,
    onOpenFavorites: () -> Unit
) {
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
            Text("Benvenuto nel tuo supermercato", style = MaterialTheme.typography.titleLarge)
            Text(
                "Prodotti salvati: $favoritesCount  •  Nel carrello: $cartCount",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = onOpenFavorites) {
                Text("Apri preferiti")
            }
        }
    }
}

@Composable
private fun SearchFiltersCard(
    searchQuery: String,
    selectedCategories: Set<String>,
    categories: List<String>,
    selectedSortField: SortField,
    selectedSortDirection: SortDirection,
    maxPriceFilter: Double,
    maxPriceLimit: Double,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSortFieldChanged: (SortField) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onMaxPriceChanged: (Double) -> Unit,
    onResetFilters: () -> Unit
) {
    var showFiltersPopup by remember { mutableStateOf(false) }
    
    // Verificare se ci sono filtri attivi (diversi dai default)
    val hasActiveFilters = selectedCategories != setOf("Tutti") || 
        selectedSortField != SortField.NAME || 
        selectedSortDirection != SortDirection.ASC ||
        searchQuery.isNotBlank() ||
        maxPriceFilter < maxPriceLimit

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Tune, contentDescription = null)
                Column {
                    Text("Cerca, filtra e ordina", fontWeight = FontWeight.SemiBold)
                    Text("Apri i filtri dal pulsante accanto alla ricerca.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier.fillMaxWidth(0.82f),
                    label = { Text("Cerca prodotto") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = AccentText,
                        focusedBorderColor = AccentText,
                        focusedLabelColor = AccentText
                    )
                )
                Button(onClick = { showFiltersPopup = true }, modifier = Modifier.size(52.dp), contentPadding = PaddingValues(0.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = "Apri filtri")
                }
            }
        }
    }

    // ── Filtri Attivi (mostra solo se ci sono filtri) ──────────────────
    if (hasActiveFilters) {
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
                    onClick = onResetFilters,
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
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Mostra le categorie selezionate (escluso "Tutti")
                val categorieMostrate = selectedCategories.filter { it != "Tutti" }
                items(categorieMostrate) { category ->
                    AssistChip(
                        onClick = { onCategorySelected(category) },
                        label = { Text(category) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = AccentText
                            )
                        },
                        modifier = Modifier.height(32.dp)
                    )
                }
                if (selectedSortField != SortField.NAME) {
                    item {
                        AssistChip(
                            onClick = { onSortFieldChanged(SortField.NAME) },
                            label = { Text("Ordina: ${selectedSortField.label}") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentText
                                )
                            },
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
                if (selectedSortDirection != SortDirection.ASC) {
                    item {
                        AssistChip(
                            onClick = { onSortDirectionChanged(SortDirection.ASC) },
                            label = { Text(selectedSortDirection.label) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentText
                                )
                            },
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
                if (searchQuery.isNotBlank()) {
                    item {
                        AssistChip(
                            onClick = { onSearchQueryChanged("") },
                            label = { Text("\"$searchQuery\"") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentText
                                )
                            },
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
                if (maxPriceFilter < maxPriceLimit) {
                    item {
                        AssistChip(
                            onClick = { onMaxPriceChanged(maxPriceLimit) },
                            label = { Text("Max: EUR ${"%.2f".format(maxPriceFilter)}") },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = AccentText
                                )
                            },
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
            }
        }
    }

    if (showFiltersPopup) {
        FiltersPopupDialog(
            selectedCategories = selectedCategories,
            categories = categories,
            selectedSortField = selectedSortField,
            selectedSortDirection = selectedSortDirection,
            maxPriceFilter = maxPriceFilter,
            maxPriceLimit = maxPriceLimit,
            onCategorySelected = onCategorySelected,
            onSortFieldChanged = onSortFieldChanged,
            onSortDirectionChanged = onSortDirectionChanged,
            onMaxPriceChanged = onMaxPriceChanged,
            onDismiss = { showFiltersPopup = false }
        )
    }
}

@Composable
private fun FiltersPopupDialog(
    selectedCategories: Set<String>,
    categories: List<String>,
    selectedSortField: SortField,
    selectedSortDirection: SortDirection,
    maxPriceFilter: Double,
    maxPriceLimit: Double,
    onCategorySelected: (String) -> Unit,
    onSortFieldChanged: (SortField) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onMaxPriceChanged: (Double) -> Unit,
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

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ordina per", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(SortField.entries.toList()) { field ->
                            FilterChip(
                                selected = field == selectedSortField,
                                onClick = { onSortFieldChanged(field) },
                                label = { Text(field.label, style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Direzione", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(SortDirection.entries.toList()) { direction ->
                            FilterChip(
                                selected = direction == selectedSortDirection,
                                onClick = { onSortDirectionChanged(direction) },
                                label = { Text(direction.label, style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Categoria", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategories.contains(category),
                                onClick = { onCategorySelected(category) },
                                label = { Text(category, style = MaterialTheme.typography.bodyLarge) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Prezzo massimo: EUR ${"%.2f".format(maxPriceFilter)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Slider(
                        value = maxPriceFilter.toFloat(),
                        onValueChange = { onMaxPriceChanged(it.toDouble()) },
                        valueRange = 0f..maxPriceLimit.toFloat()
                    )
                }

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Applica filtri")
                }
            }
        }
    }
}


@Composable
private fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    cartQuantity: Int,
    onToggleFavorite: () -> Unit,
    onOpenDetail: () -> Unit,
    onAdd: () -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onOpenDetail() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (product.imageRes != null) {
                            Image(
                                painter = painterResource(id = product.imageRes),
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (product.icon != null) {
                            Icon(
                                imageVector = product.icon,
                                contentDescription = product.name,
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "EUR ${"%.2f".format(product.price)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = com.example.altafedeltium.ui.theme.AccentText,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Preferito",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (cartQuantity <= 0) {
                        Button(
                            onClick = onAdd,
                            enabled = product.stock > 0,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart, 
                                contentDescription = null, 
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 4.dp)
                        ) {
                            IconButton(
                                onClick = onDecreaseQuantity,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove, 
                                    contentDescription = "Riduci",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = cartQuantity.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = onIncreaseQuantity,
                                enabled = product.stock > 0,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add, 
                                    contentDescription = "Aumenta",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
