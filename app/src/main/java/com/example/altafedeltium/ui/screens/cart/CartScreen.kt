package com.example.altafedeltium.ui.screens.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.altafedeltium.ui.viewmodel.HomeViewModel

@Composable
fun CartScreen(
    homeViewModel: HomeViewModel,
    onCheckout: () -> Unit
) {
    val uiState by homeViewModel.uiState
    val cartItems = uiState.cartItems

    Scaffold(
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Totale:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "EUR ${"%.2f".format(homeViewModel.totalAmount())}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        Button(
                            onClick = onCheckout,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Vai al Checkout")
                        }
                    }
                }
            }
        }
     ) { paddingValues ->
         if (cartItems.isEmpty()) {
             Column(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(
                         top = paddingValues.calculateTopPadding(),
                         start = 24.dp,
                         end = 24.dp,
                         bottom = paddingValues.calculateBottomPadding()
                     ),
                 verticalArrangement = Arrangement.Center,
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 Text(
                     text = "Il tuo carrello è vuoto",
                     style = MaterialTheme.typography.titleMedium
                 )
             }
         } else {
             LazyColumn(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(
                         top = paddingValues.calculateTopPadding(),
                         start = 16.dp,
                         end = 16.dp,
                         bottom = paddingValues.calculateBottomPadding()
                     ),
                 verticalArrangement = Arrangement.spacedBy(8.dp)
             ) {
                items(cartItems) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.product.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = "EUR ${"%.2f".format(item.product.price)}")
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { homeViewModel.decreaseQuantity(item.product.id) }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Diminuisci")
                                }
                                Text(
                                    text = item.quantity.toString(),
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                IconButton(onClick = { homeViewModel.increaseQuantity(item.product.id) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Aumenta")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
