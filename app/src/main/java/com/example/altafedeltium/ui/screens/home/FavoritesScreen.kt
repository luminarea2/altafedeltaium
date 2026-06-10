package com.example.altafedeltium.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 24.dp,
                        end = 24.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Non hai ancora preferiti", style = MaterialTheme.typography.titleMedium)
                Text("Apri un prodotto e premi il cuore per salvarlo")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 16.dp,
                        end = 16.dp,
                        bottom = innerPadding.calculateBottomPadding()
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites, key = { it.id }) { product ->
                    FavoriteProductCard(
                        product = product,
                        onOpen = { onOpenProduct(product.id) },
                        onAddToCart = { onAddToCart(product) },
                        onRemove = { onRemoveFavorite(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteProductCard(
    product: Product,
    onOpen: () -> Unit,
    onAddToCart: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onOpen
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "EUR ${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onAddToCart) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Aggiungi al carrello",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Rimuovi dai preferiti",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

