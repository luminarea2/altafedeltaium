package com.example.altafedeltium.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.altafedeltium.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favorites: List<Product>,
    onBack: () -> Unit,
    onOpenProduct: (Int) -> Unit,
    onRemoveFavorite: (Int) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferiti") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Non hai ancora preferiti", style = MaterialTheme.typography.titleMedium)
                Text("Apri un prodotto e premi il cuore per salvarlo")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites, key = { it.id }) { product ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text("EUR ${"%.2f".format(product.price)}")

                            Button(onClick = { onOpenProduct(product.id) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Apri prodotto")
                            }
                            Button(onClick = { onAddToCart(product) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Aggiungi al carrello")
                            }
                            IconButton(onClick = { onRemoveFavorite(product.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Rimuovi")
                            }
                        }
                    }
                }
            }
        }
    }
}

