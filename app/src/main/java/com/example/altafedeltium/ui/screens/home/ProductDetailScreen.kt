package com.example.altafedeltium.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import com.example.altafedeltium.ui.utils.showTimedSnackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.altafedeltium.data.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    cartQuantity: Int,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isAddingToCart = remember { mutableStateOf(value = false) }
    // track previous cart quantity to show removal message when quantity goes 1->0
    val prevQuantity = remember { mutableStateOf(cartQuantity) }
    // remember previous favorite state to show feedback when toggled
    val previousFavorite = remember { mutableStateOf(isFavorite) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TopAppBar(
                title = { Text(product.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Preferito"
                        )
                    }
                }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
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
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(product.category, style = MaterialTheme.typography.labelLarge)
            Text("EUR ${"%.2f".format(product.price)}", style = MaterialTheme.typography.headlineSmall)
            Text(product.description, style = MaterialTheme.typography.bodyLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (cartQuantity <= 0) {
                    Button(
                                onClick = {
                                            // only show add-to-cart snackbar if product was not already in cart
                                            val wasInCart = cartQuantity > 0
                                            onAddToCart(product)
                                            if (!wasInCart) isAddingToCart.value = true
                                        },
                        enabled = product.stock > 0,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(" Aggiungi")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onDecreaseQuantity(product.id) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Riduci quantità")
                        }
                        Text(
                            text = cartQuantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { onIncreaseQuantity(product.id) },
                            enabled = product.stock > 0
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Aumenta quantità")
                        }
                    }
                }
            }

            // Display availability
            Text(
                text = "Disponibilità: ${product.stock} pezzi",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (product.stock > 0) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.error
            )

            // Show snackbar message when item is added
            LaunchedEffect(isAddingToCart.value) {
                if (isAddingToCart.value) {
                    snackbarHostState.showTimedSnackbar("✓ Prodotto aggiunto al carrello!")
                    isAddingToCart.value = false
                }
            }

            // detect removal from cart (quantity 1->0)
            LaunchedEffect(cartQuantity) {
                if (prevQuantity.value > 0 && cartQuantity == 0) {
                    snackbarHostState.showTimedSnackbar("✓ Prodotto rimosso dal carrello")
                }
                prevQuantity.value = cartQuantity
            }

            // Show snackbar when favorite status changes (added/removed)
            LaunchedEffect(isFavorite) {
                if (isFavorite != previousFavorite.value) {
                    if (isFavorite) {
                        snackbarHostState.showTimedSnackbar("✓ Aggiunto ai preferiti")
                    } else {
                        snackbarHostState.showTimedSnackbar("✓ Rimosso dai preferiti")
                    }
                    previousFavorite.value = isFavorite
                }
            }
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
