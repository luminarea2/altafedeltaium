package com.example.altafedeltium.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.altafedeltium.ui.components.ConfirmationDialog
import com.example.altafedeltium.ui.components.MapPickerDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.altafedeltium.data.model.Address
import com.example.altafedeltium.data.model.Order
import com.example.altafedeltium.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.altafedeltium.ui.theme.WarmOrangeGradientLight

private val ProfileAccentColor = Color(0xFFF57C4B)

private enum class ProfileSection(val title: String) {
    PERSONAL_DATA("I miei dati"),
    ADDRESSES("I miei indirizzi"),
    ORDERS("I miei ordini")
}

@Composable
fun ProfileScreen(
    homeViewModel: HomeViewModel,
    onOpenOrderHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by homeViewModel.uiState
    var selectedSection by rememberSaveable { mutableStateOf(ProfileSection.PERSONAL_DATA) }
    var showLogoutConfirmation by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmOrangeGradientLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ProfileHeroCard(
                username = uiState.userProfile.username,
                loyaltyCode = uiState.userProfile.loyaltyCode,
                memberSince = uiState.userProfile.memberSince,
                onEditProfile = { selectedSection = ProfileSection.PERSONAL_DATA }
            )
        }

        item {
            ProfileMenuCard(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it }
            )
        }

        item {
            when (selectedSection) {
                ProfileSection.PERSONAL_DATA -> PersonalDataSection(
                    firstName = uiState.userProfile.firstName,
                    lastName = uiState.userProfile.lastName,
                    email = uiState.userProfile.email,
                    phone = uiState.userProfile.phone,
                    loyaltyCode = uiState.userProfile.loyaltyCode,
                    memberSince = uiState.userProfile.memberSince,
                    onSave = homeViewModel::updateUserProfile
                )

                ProfileSection.ADDRESSES -> AddressesSection(
                    addresses = uiState.addresses,
                    selectedAddressId = uiState.selectedAddressId,
                    onAddAddress = { label, street, city, zip, lat, lon ->
                        homeViewModel.addAddressWithCoordinates(label, street, city, zip, lat, lon)
                    },
                    onUpdateAddress = { id, label, street, city, zip, lat, lon ->
                        homeViewModel.updateAddressWithCoordinates(id, label, street, city, zip, lat, lon)
                    },
                    onRemoveAddress = homeViewModel::removeAddress,
                    onSetDefault = homeViewModel::setDefaultAddress,
                    onSelectAddress = homeViewModel::setSelectedAddress
                )

                ProfileSection.ORDERS -> OrdersSection(
                    orders = uiState.orders,
                    onOpenOrderHistory = onOpenOrderHistory
                )
            }
        }

        item {
            Button(
                onClick = { showLogoutConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Logout")
            }
        }
    }

    if (showLogoutConfirmation) {
        ConfirmationDialog(
            title = "Conferma logout",
            message = "Sei sicuro di voler uscire dal tuo account?",
            confirmLabel = "Esci",
            dismissLabel = "Resta",
            onConfirm = {
                showLogoutConfirmation = false
                onLogout()
            },
            onDismiss = { showLogoutConfirmation = false }
        )
    }
}

@Composable
private fun ProfileHeroCard(
    username: String,
    loyaltyCode: String,
    memberSince: String,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "SuperSpan",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Profilo di $username",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Box(contentAlignment = Alignment.TopEnd) {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.padding(top = 4.dp, end = 4.dp)
                ) {
                    IconButton(onClick = onEditProfile, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifica profilo",
                            tint = ProfileAccentColor
                        )
                    }
                }
            }

            Text(
                text = "Codice fedeltà: $loyaltyCode • Iscritto da $memberSince",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ProfileMenuCard(
    selectedSection: ProfileSection,
    onSectionSelected: (ProfileSection) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileMenuRow(
                title = ProfileSection.PERSONAL_DATA.title,
                isSelected = selectedSection == ProfileSection.PERSONAL_DATA,
                icon = Icons.Default.Person,
                onClick = { onSectionSelected(ProfileSection.PERSONAL_DATA) }
            )
            HorizontalDivider(color = Color(0x66B76E38))
            ProfileMenuRow(
                title = ProfileSection.ADDRESSES.title,
                isSelected = selectedSection == ProfileSection.ADDRESSES,
                icon = Icons.Default.Place,
                onClick = { onSectionSelected(ProfileSection.ADDRESSES) }
            )
            HorizontalDivider(color = Color(0x66B76E38))
            ProfileMenuRow(
                title = ProfileSection.ORDERS.title,
                isSelected = selectedSection == ProfileSection.ORDERS,
                icon = Icons.Default.ShoppingBag,
                onClick = { onSectionSelected(ProfileSection.ORDERS) }
            )
        }
    }
}

@Composable
private fun ProfileMenuRow(
    title: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun PersonalDataSection(
    firstName: String,
    lastName: String,
    email: String,
    phone: String,
    loyaltyCode: String,
    memberSince: String,
    onSave: (String, String, String, String) -> Unit
) {
    var editedFirstName by remember(firstName) { mutableStateOf(firstName) }
    var editedLastName by remember(lastName) { mutableStateOf(lastName) }
    var editedEmail by remember(email) { mutableStateOf(email) }
    var editedPhone by remember(phone) { mutableStateOf(phone) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("I miei dati", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Aggiorna i tuoi dati anagrafici e tieni sempre corrette email e telefono.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = editedFirstName,
                onValueChange = { editedFirstName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nome") },
                singleLine = true
            )
            OutlinedTextField(
                value = editedLastName,
                onValueChange = { editedLastName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cognome") },
                singleLine = true
            )
            OutlinedTextField(
                value = editedEmail,
                onValueChange = { editedEmail = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true
            )
            OutlinedTextField(
                value = editedPhone,
                onValueChange = { editedPhone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Telefono") },
                singleLine = true
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5EC)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Codice fedeltà", fontWeight = FontWeight.SemiBold)
                        Text(loyaltyCode)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Iscritto da", fontWeight = FontWeight.SemiBold)
                        Text(memberSince)
                    }
                }
            }

            Button(
                onClick = { onSave(editedFirstName, editedLastName, editedEmail, editedPhone) },
                modifier = Modifier.fillMaxWidth(),
                enabled = editedFirstName.isNotBlank() && editedLastName.isNotBlank() && editedEmail.isNotBlank() && editedPhone.isNotBlank()
            ) {
                Text("Salva modifiche")
            }
        }
    }
}

@Composable
private fun AddressesSection(
    addresses: List<Address>,
    selectedAddressId: Int?,
    onAddAddress: (String, String, String, String, Double?, Double?) -> Unit,
    onUpdateAddress: (Int, String, String, String, String, Double?, Double?) -> Unit,
    onRemoveAddress: (Int) -> Unit,
    onSetDefault: (Int) -> Unit,
    onSelectAddress: (Int) -> Unit
) {
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var addressToEdit by remember { mutableStateOf<Address?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("I miei indirizzi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Inserisci via e numero civico, poi controlla sulla mappa il punto esatto dell'ingresso.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedButton(
                    onClick = {
                        addressToEdit = null
                        showEditor = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aggiungi indirizzo")
                }

                if (showEditor) {
                    AddressEditorCard(
                        initialAddress = addressToEdit,
                        onDismiss = {
                            showEditor = false
                            addressToEdit = null
                        },
                        onSave = { label, street, city, zipCode, lat, lon ->
                            val currentAddress = addressToEdit
                            if (currentAddress == null) {
                                onAddAddress(label, street, city, zipCode, lat, lon)
                            } else {
                                onUpdateAddress(currentAddress.id, label, street, city, zipCode, lat, lon)
                            }
                            showEditor = false
                            addressToEdit = null
                        }
                    )
                }
            }
        }

        if (addresses.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Text(
                    text = "Non hai ancora salvato indirizzi. Aggiungine uno per velocizzare il checkout.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            addresses.forEach { address ->
                AddressCard(
                    address = address,
                    isSelectedForDelivery = address.id == selectedAddressId,
                    onEdit = {
                        addressToEdit = address
                        showEditor = true
                    },
                    onRemove = { onRemoveAddress(address.id) },
                    onSetDefault = { onSetDefault(address.id) },
                    onSelectForDelivery = { onSelectAddress(address.id) }
                )
            }
        }
    }
}

@Composable
private fun AddressEditorCard(
    initialAddress: Address?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Double?, Double?) -> Unit
) {
    var label by remember(initialAddress?.id) { mutableStateOf(initialAddress?.label.orEmpty()) }
    var street by remember(initialAddress?.id) { mutableStateOf(initialAddress?.street.orEmpty()) }
    var city by remember(initialAddress?.id) { mutableStateOf(initialAddress?.city.orEmpty()) }
    var zipCode by remember(initialAddress?.id) { mutableStateOf(initialAddress?.zipCode.orEmpty()) }
    var latitude by remember(initialAddress?.id) { mutableStateOf(initialAddress?.latitude) }
    var longitude by remember(initialAddress?.id) { mutableStateOf(initialAddress?.longitude) }
    var showMapPicker by remember { mutableStateOf(false) }

    if (showMapPicker) {
        MapPickerDialog(
            initialLat = latitude,
            initialLon = longitude,
            onDismiss = { showMapPicker = false },
            onConfirm = { lat, lon ->
                latitude = lat
                longitude = lon
                showMapPicker = false
            }
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F0)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (initialAddress == null) "Nuovo indirizzo" else "Modifica indirizzo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(value = label, onValueChange = { label = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Etichetta") })
            OutlinedTextField(value = street, onValueChange = { street = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Via e numero civico") })
            OutlinedTextField(value = city, onValueChange = { city = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Città") })
            OutlinedTextField(value = zipCode, onValueChange = { zipCode = it }, modifier = Modifier.fillMaxWidth(), label = { Text("CAP") })

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { showMapPicker = true }, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Place, contentDescription = null)
                    Text("  Verifica posizione su mappa")
                }
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Annulla")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSave(label, street, city, zipCode, latitude, longitude) },
                    modifier = Modifier.weight(1f),
                    enabled = label.isNotBlank() && street.isNotBlank() && city.isNotBlank() && zipCode.isNotBlank()
                ) {
                    Text("Salva")
                }
                if (latitude != null && longitude != null) {
                    Text("Entrata impostata: ${"%.5f".format(latitude)} , ${"%.5f".format(longitude)}", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// MapPickerDialog is provided by `ui.components.MapPickerDialog` (Google Maps Compose)

@Composable
private fun AddressCard(
    address: Address,
    isSelectedForDelivery: Boolean,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onSetDefault: () -> Unit,
    onSelectForDelivery: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(address.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("${address.street}, ${address.city} ${address.zipCode}")
                }
                if (address.isDefault) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(18.dp))
                        Text("Predefinito", modifier = Modifier.padding(start = 4.dp), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text("Modifica")
                }
                OutlinedButton(onClick = onRemove, modifier = Modifier.weight(1f)) {
                    Text("Rimuovi")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSelectForDelivery, modifier = Modifier.weight(1f)) {
                    Text(if (isSelectedForDelivery) "Consegna selezionata" else "Usa per consegna")
                }
                OutlinedButton(onClick = onSetDefault, modifier = Modifier.weight(1f)) {
                    Text(if (address.isDefault) "Già predefinito" else "Rendi predefinito")
                }
            }
        }
    }
}

@Composable
private fun OrdersSection(
    orders: List<Order>,
    onOpenOrderHistory: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("I miei ordini", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Totale ordini effettuati: ${orders.size}")
                Text(
                    "Controlla velocemente i tuoi ultimi acquisti o apri lo storico completo.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(onClick = onOpenOrderHistory, modifier = Modifier.fillMaxWidth()) {
                    Text("Apri storico completo")
                }
            }
        }

        if (orders.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Text(
                    text = "Non hai ancora ordini confermati. Quando completerai il checkout, li vedrai qui.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            orders.take(3).forEach { order ->
                RecentOrderCard(order = order)
            }
        }
    }
}

@Composable
private fun RecentOrderCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Ordine #${order.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(order.createdAt)
            Text("Stato: ${order.status}")
            Text("Supermercato: ${order.supermarketName}")
            Text("Consegna: ${order.deliveryDay}")
            Text("Articoli: ${order.items.sumOf { it.quantity }}")
            Text("Totale: EUR ${"%.2f".format(order.total)}")
        }
    }
}

