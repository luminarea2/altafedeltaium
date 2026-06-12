package com.example.altafedeltium.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import com.example.altafedeltium.ui.theme.AccentText
import com.example.altafedeltium.ui.theme.OrangePrimary
import com.example.altafedeltium.ui.theme.OrangeOnPrimary
import com.example.altafedeltium.data.model.Address
import com.example.altafedeltium.data.model.Order
import com.example.altafedeltium.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

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
    // show a confirmation dialog after saving profile data
    var showProfileSavedDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(0.dp),
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
                    // wrap the viewmodel update so we can show feedback to the user
                    onSave = { f, l, e, p ->
                        homeViewModel.updateUserProfile(f, l, e, p)
                        showProfileSavedDialog = true
                    }
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
                // use the theme error color to represent a destructive action (red)
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onError
                )
                Text("Logout", color = MaterialTheme.colorScheme.onError)
            }
        }
        }

        // place SnackbarHost above the bottom navigation (bottom-center)
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            SnackbarHost(hostState = snackbarHostState)
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
            onDismiss = { showLogoutConfirmation = false },
            destructiveConfirm = true,
            // rendiamo il pulsante "Resta" dello stesso stile del pulsante "Salva modifiche"
            dismissAsPrimary = true
        )
    }

    // show a small snackbar (same style used when adding a product to cart) after save
    LaunchedEffect(showProfileSavedDialog) {
        if (showProfileSavedDialog) {
            snackbarHostState.showSnackbar("✓ Dati aggiornati correttamente!", duration = SnackbarDuration.Short)
            showProfileSavedDialog = false
        }
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
                            tint = AccentText
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
                onClick = { onSectionSelected(ProfileSection.PERSONAL_DATA) },
                rounded = RoundedCornerType.TOP
            )
            HorizontalDivider(color = Color(0x66B76E38))
            ProfileMenuRow(
                title = ProfileSection.ADDRESSES.title,
                isSelected = selectedSection == ProfileSection.ADDRESSES,
                icon = Icons.Default.Place,
                onClick = { onSectionSelected(ProfileSection.ADDRESSES) },
                rounded = RoundedCornerType.NONE
            )
            HorizontalDivider(color = Color(0x66B76E38))
            ProfileMenuRow(
                title = ProfileSection.ORDERS.title,
                isSelected = selectedSection == ProfileSection.ORDERS,
                icon = Icons.Default.ShoppingBag,
                onClick = { onSectionSelected(ProfileSection.ORDERS) },
                rounded = RoundedCornerType.BOTTOM
            )
        }
    }
}

private enum class RoundedCornerType { ALL, TOP, BOTTOM, NONE }

@Composable
private fun ProfileMenuRow(
    title: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    rounded: RoundedCornerType = RoundedCornerType.ALL
) {
    val shape = when (rounded) {
        RoundedCornerType.ALL -> RoundedCornerShape(12.dp)
        RoundedCornerType.TOP -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
        RoundedCornerType.BOTTOM -> RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 12.dp, bottomStart = 12.dp)
        RoundedCornerType.NONE -> RoundedCornerShape(0.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
                .background(
                color = if (isSelected) OrangePrimary else Color.Transparent,
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) OrangeOnPrimary else MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp),
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) OrangeOnPrimary else MaterialTheme.colorScheme.onSecondaryContainer
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = if (isSelected) OrangeOnPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
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
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = AccentText,
                    focusedBorderColor = AccentText,
                    focusedLabelColor = AccentText
                )
            )
            OutlinedTextField(
                value = editedLastName,
                onValueChange = { editedLastName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Cognome") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = AccentText,
                    focusedBorderColor = AccentText,
                    focusedLabelColor = AccentText
                )
            )
            OutlinedTextField(
                value = editedEmail,
                onValueChange = { editedEmail = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = AccentText,
                    focusedBorderColor = AccentText,
                    focusedLabelColor = AccentText
                )
            )
            OutlinedTextField(
                value = editedPhone,
                onValueChange = { editedPhone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Telefono") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = AccentText,
                    focusedBorderColor = AccentText,
                    focusedLabelColor = AccentText
                )
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
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
                // allow saving profile even if phone is still empty
                enabled = editedFirstName.isNotBlank() && editedLastName.isNotBlank() && editedEmail.isNotBlank()
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
                    modifier = Modifier.fillMaxWidth(),
                    // testo più scuro per miglior contrasto sullo sfondo grigio
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
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

    // Allineiamo l'editor indirizzo del profilo alla versione usata nel checkout:
    // - bottone mappa full-width
    // - mostra coordinate scelte come testo separato
    // - pulsante Salva full-width con stessa label e condizioni del checkout
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
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

            OutlinedTextField(value = label, onValueChange = { label = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Etichetta") }, colors = OutlinedTextFieldDefaults.colors(cursorColor = AccentText, focusedBorderColor = AccentText, focusedLabelColor = AccentText))
            OutlinedTextField(value = street, onValueChange = { street = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Via e numero civico") }, colors = OutlinedTextFieldDefaults.colors(cursorColor = AccentText, focusedBorderColor = AccentText, focusedLabelColor = AccentText))
            OutlinedTextField(value = city, onValueChange = { city = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Città") }, colors = OutlinedTextFieldDefaults.colors(cursorColor = AccentText, focusedBorderColor = AccentText, focusedLabelColor = AccentText))
            OutlinedTextField(value = zipCode, onValueChange = { zipCode = it }, modifier = Modifier.fillMaxWidth(), label = { Text("CAP") }, colors = OutlinedTextFieldDefaults.colors(cursorColor = AccentText, focusedBorderColor = AccentText, focusedLabelColor = AccentText))

            OutlinedButton(
                onClick = { showMapPicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Icon(Icons.Default.Place, contentDescription = null)
                Text("  Verifica posizione su mappa")
            }

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

            // mostra le coordinate scelte (come nel checkout)
            if (latitude != null && longitude != null) {
                val lat = latitude
                val lon = longitude
                Text("Entrata impostata: ${"%.5f".format(lat)} , ${"%.5f".format(lon)}", modifier = Modifier.padding(top = 4.dp))
            }

            Button(
                onClick = { onSave(label, street, city, zipCode, latitude, longitude) },
                enabled = label.isNotBlank() && street.isNotBlank() && city.isNotBlank() && zipCode.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Salva indirizzo", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            // Manteniamo comunque un pulsante Annulla per chiudere l'editor nel profilo,
            // ma lo posizioniamo sotto il Salva e in stile outlined, così da non sovrapporre
            // la UX usata nel checkout che non ha cancel inline.
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
            ) {
                Text("Annulla")
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
    var showRemoveDialog by rememberSaveable { mutableStateOf(false) }
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
                // Mostriamo l'etichetta e (se presente) il badge "Predefinito" sulla stessa riga,
                // mentre l'indirizzo completo va su una riga separata sotto.
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(address.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        if (address.isDefault) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = AccentText, modifier = Modifier.size(18.dp))
                                Text("Predefinito", modifier = Modifier.padding(start = 4.dp), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Text("${address.street}, ${address.city} ${address.zipCode}")
                }
                // spazio a destra preservato (in caso servano altri elementi in futuro)
                Spacer(modifier = Modifier.width(8.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
                ) {
                    Text("Modifica")
                }
                OutlinedButton(
                    onClick = { showRemoveDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
                ) {
                    Text("Rimuovi")
                }
            }

            // rimuoviamo il pulsante "Usa per consegna" e facciamo prendere al
            // pulsante "Rendi predefinito" tutto lo spazio; inoltre lo coloriamo
            // con lo stesso colore che aveva prima il pulsante di consegna.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onSetDefault,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = if (address.isDefault) "Già predefinito" else "Rendi predefinito",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    if (showRemoveDialog) {
        ConfirmationDialog(
            title = "Rimuovi indirizzo",
            message = "Sei sicuro di voler rimuovere l'indirizzo \"${address.label}: ${address.street}, ${address.city}\"?",
            confirmLabel = "Rimuovi",
            dismissLabel = "Annulla",
            onConfirm = {
                showRemoveDialog = false
                onRemove()
            },
            onDismiss = { showRemoveDialog = false },
            destructiveConfirm = true
        )
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

