package com.example.altafedeltium.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.altafedeltium.data.model.Product
import com.example.altafedeltium.data.model.SortDirection
import com.example.altafedeltium.data.model.SortField
import com.example.altafedeltium.ui.theme.WarmOrangeGradientLight
import com.example.altafedeltium.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onOpenProduct: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by homeViewModel.uiState
    val filteredProducts = homeViewModel.filteredProducts()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOrangeGradientLight),
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
                selectedCategory = uiState.selectedCategory,
                categories = homeViewModel.categories,
                selectedSortField = uiState.selectedSortField,
                selectedSortDirection = uiState.selectedSortDirection,
                maxPriceFilter = uiState.maxPriceFilter,
                maxPriceLimit = homeViewModel.maxPriceLimit,
                onSearchQueryChanged = homeViewModel::onSearchQueryChanged,
                onCategorySelected = homeViewModel::onCategorySelected,
                onSortFieldChanged = homeViewModel::onSortFieldChanged,
                onSortDirectionChanged = homeViewModel::onSortDirectionChanged,
                onMaxPriceChanged = { homeViewModel.onMaxPriceChanged(it) }
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
    selectedCategory: String,
    categories: List<String>,
    selectedSortField: SortField,
    selectedSortDirection: SortDirection,
    maxPriceFilter: Double,
    maxPriceLimit: Double,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSortFieldChanged: (SortField) -> Unit,
    onSortDirectionChanged: (SortDirection) -> Unit,
    onMaxPriceChanged: (Double) -> Unit
) {
    var showFiltersPopup by remember { mutableStateOf(false) }
    val activeFiltersText = "${selectedSortField.label} (${selectedSortDirection.label})  •  $selectedCategory"

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
                    singleLine = true
                )
                Button(onClick = { showFiltersPopup = true }, modifier = Modifier.size(52.dp), contentPadding = PaddingValues(0.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = "Apri filtri")
                }
            }

            Text("Filtri attivi: $activeFiltersText", style = MaterialTheme.typography.bodySmall)
        }
    }

    if (showFiltersPopup) {
        FiltersPopupDialog(
            selectedCategory = selectedCategory,
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
    selectedCategory: String,
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
                    Text("Ordina per", fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(SortField.entries.toList()) { field ->
                            FilterChip(
                                selected = field == selectedSortField,
                                onClick = { onSortFieldChanged(field) },
                                label = { Text(field.label) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Direzione", fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(SortDirection.entries.toList()) { direction ->
                            FilterChip(
                                selected = direction == selectedSortDirection,
                                onClick = { onSortDirectionChanged(direction) },
                                label = { Text(direction.label) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Categoria", fontWeight = FontWeight.Medium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { category ->
                            FilterChip(
                                selected = category == selectedCategory,
                                onClick = { onCategorySelected(category) },
                                label = { Text(category) }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Prezzo massimo: EUR ${"%.2f".format(maxPriceFilter)}")
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
            .padding(horizontal = 16.dp)
            .clickable { onOpenDetail() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
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
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.name,
                        modifier = Modifier
                            .size(74.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(product.category, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("EUR ${"%.2f".format(product.price)}", fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Preferito",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (cartQuantity <= 0) {
                        Button(onClick = onAdd, enabled = product.stock > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Aggiungi al carrello")
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onDecreaseQuantity) {
                                Icon(Icons.Default.Remove, contentDescription = "Riduci quantità")
                            }
                            Text(
                                text = cartQuantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = onIncreaseQuantity,
                                enabled = product.stock > 0
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Aumenta quantità")
                            }
                        }
                    }
                }
            }
        }
    }
}
