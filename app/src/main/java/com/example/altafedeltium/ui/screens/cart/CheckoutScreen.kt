package com.example.altafedeltium.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.height (rimosso: non usato)
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
// import androidx.compose.foundation.lazy.LazyRow (rimosso: non usato)
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Delete
import com.example.altafedeltium.ui.components.MapPickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.altafedeltium.data.model.Address
import com.example.altafedeltium.data.model.PaymentCard
import com.example.altafedeltium.data.model.PaymentMethod
import com.example.altafedeltium.data.model.SupermarketChoice
import com.example.altafedeltium.data.model.SupermarketDistanceBand
import com.example.altafedeltium.ui.components.ConfirmationDialog
import com.example.altafedeltium.ui.components.CustomRadioButton
import com.example.altafedeltium.ui.components.StepIndicator
import com.example.altafedeltium.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private enum class CheckoutStep {
    SUPERMARKET,
    ADDRESS,
    PAYMENT,
    SUMMARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    homeViewModel: HomeViewModel,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit
) {
    val uiState by homeViewModel.uiState
    val selectedAddress = uiState.addresses.find { it.id == uiState.selectedAddressId }
    val selectedCard = uiState.paymentCards.find { it.id == uiState.selectedPaymentCardId }
    val supermarketsForAddress = homeViewModel.supermarketsForSelectedAddressChoices()
    val selectedSupermarketChoice = supermarketsForAddress.find { it.supermarket.id == uiState.selectedSupermarketId }
    val selectedSupermarket = selectedSupermarketChoice?.supermarket
    val deliveryDays = remember { generateDeliveryDays() }

    var currentStep by rememberSaveable { mutableStateOf(CheckoutStep.SUPERMARKET) }
    var selectedPayment by rememberSaveable { mutableStateOf(PaymentMethod.CARD) }
    var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    var showAddAddressForm by rememberSaveable { mutableStateOf(false) }
    var addressLabel by rememberSaveable { mutableStateOf("") }
    var addressStreet by rememberSaveable { mutableStateOf("") }
    var addressCity by rememberSaveable { mutableStateOf("") }
    var addressZip by rememberSaveable { mutableStateOf("") }

    var showAddCardForm by rememberSaveable { mutableStateOf(false) }
    var cardHolder by rememberSaveable { mutableStateOf("") }
    var cardNumber by rememberSaveable { mutableStateOf("") }
    var cardExpiry by rememberSaveable { mutableStateOf("") }
    var cardCvv by rememberSaveable { mutableStateOf("") }

    val canContinue = when (currentStep) {
        // richiedi che il supermercato selezionato sia anche disponibile
        CheckoutStep.SUPERMARKET -> selectedSupermarket != null && (selectedSupermarketChoice?.isAvailable == true)
        CheckoutStep.ADDRESS -> selectedAddress != null
        CheckoutStep.PAYMENT -> selectedPayment != PaymentMethod.CARD || selectedCard != null
        // nella summary richiedi che il supermercato sia selezionato e disponibile
        CheckoutStep.SUMMARY -> selectedSupermarket != null && (selectedSupermarketChoice?.isAvailable == true) && selectedAddress != null && uiState.cartItems.isNotEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Reset any transient checkout UI state and go back to cart immediately
                        showConfirmDialog = false
                        currentStep = CheckoutStep.SUPERMARKET
                        // navigate back to cart
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        },
        bottomBar = {
            CheckoutBottomBar(
                total = homeViewModel.totalAmount(),
                subtotal = homeViewModel.subtotal(),
                deliveryFee = homeViewModel.deliveryFee(),
                buttonLabel = if (currentStep == CheckoutStep.SUMMARY) "Acquista" else "Avanti",
                enabled = canContinue,
                canGoBack = true,
                onClick = {
                    if (!canContinue) {
                        errorMessage = when (currentStep) {
                            CheckoutStep.SUPERMARKET -> "Seleziona un supermercato prima di continuare."
                            CheckoutStep.ADDRESS -> "Seleziona un indirizzo prima di continuare."
                            CheckoutStep.PAYMENT -> if (selectedPayment == PaymentMethod.CARD) {
                                "Seleziona o aggiungi una carta prima di continuare."
                            } else {
                                "Seleziona un metodo di pagamento prima di continuare."
                            }
                            CheckoutStep.SUMMARY -> "Completa il checkout prima di acquistare."
                        }
                        showErrorDialog = true
                        return@CheckoutBottomBar
                    }

                    currentStep = when (currentStep) {
                        CheckoutStep.SUPERMARKET -> CheckoutStep.ADDRESS
                        CheckoutStep.ADDRESS -> CheckoutStep.PAYMENT
                        CheckoutStep.PAYMENT -> CheckoutStep.SUMMARY
                        CheckoutStep.SUMMARY -> {
                            showConfirmDialog = true
                            CheckoutStep.SUMMARY
                        }
                    }
                },
                onBack = {
                    if (showConfirmDialog) {
                        // If a confirmation dialog is visible, simply close it and stay on the same step
                        showConfirmDialog = false
                    } else {
                        // Navigate one step back (SUMMARY -> PAYMENT -> ADDRESS -> SUPERMARKET)
                        when (currentStep) {
                            CheckoutStep.SUPERMARKET -> {
                                // already at first step: navigate back to the cart
                                currentStep = CheckoutStep.SUPERMARKET
                                onBack()
                            }
                            CheckoutStep.ADDRESS -> currentStep = CheckoutStep.SUPERMARKET
                            CheckoutStep.PAYMENT -> currentStep = CheckoutStep.ADDRESS
                            CheckoutStep.SUMMARY -> currentStep = CheckoutStep.PAYMENT
                        }
                    }
                }
            )
        }
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StepIndicator(
                currentStep = currentStep.ordinal + 1,
                totalSteps = 4,
                stepLabels = listOf("Supermercato", "Indirizzo", "Pagamento", "Conferma")
            )

            if (currentStep == CheckoutStep.SUPERMARKET) {
                DeliveryDaySection(
                    days = deliveryDays,
                    selectedDay = uiState.selectedDeliveryDay,
                    onSelect = homeViewModel::setSelectedDeliveryDay
                )
            }

            when (currentStep) {
                CheckoutStep.SUPERMARKET -> {
                    Text(
                        "Seleziona il supermercato da cui effettuare l'ordine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    SupermarketSection(
                        supermarkets = supermarketsForAddress,
                        selectedSupermarketId = selectedSupermarket?.id,
                        onSelect = homeViewModel::setSelectedSupermarket,
                        onUnavailableSelect = { choice ->
                            // mostra un messaggio di errore quando l'utente prova a selezionare un supermercato non disponibile
                            errorMessage = if (choice.distanceKm > 20.0) {
                                "Supermercato troppo lontano per il giorno selezionato."
                            } else {
                                "Supermercato non disponibile per il giorno scelto."
                            }
                            showErrorDialog = true
                        }
                    )
                }

                CheckoutStep.ADDRESS -> {
                    Text(
                        "Seleziona l'indirizzo di consegna",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    AddressSection(
                        addresses = uiState.addresses,
                        selectedAddressId = selectedAddress?.id,
                        onSelect = homeViewModel::setSelectedAddress
                    )
                    OutlinedButton(
                        onClick = { showAddAddressForm = !showAddAddressForm },
                        modifier = Modifier.fillMaxWidth(),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = com.example.altafedeltium.ui.theme.AccentText)
                    ) {
                        Text(if (showAddAddressForm) "Chiudi nuovo indirizzo" else "Aggiungi indirizzo")
                    }
                    if (showAddAddressForm) {
                                AddAddressForm(
                                    label = addressLabel,
                                    street = addressStreet,
                                    city = addressCity,
                                    zipCode = addressZip,
                                    onLabelChange = { addressLabel = it },
                                    onStreetChange = { addressStreet = it },
                                    onCityChange = { addressCity = it },
                                    onZipCodeChange = { addressZip = it },
                                    onSave = { lat, lon ->
                                        val nextId = (uiState.addresses.maxOfOrNull { it.id } ?: 0) + 1
                                        homeViewModel.addAddressWithCoordinates(
                                            label = addressLabel,
                                            street = addressStreet,
                                            city = addressCity,
                                            zipCode = addressZip,
                                            latitude = lat,
                                            longitude = lon
                                        )
                                        homeViewModel.setSelectedAddress(nextId)
                                        showAddAddressForm = false
                                        addressLabel = ""
                                        addressStreet = ""
                                        addressCity = ""
                                        addressZip = ""
                                    }
                                )
                    }
                }

                CheckoutStep.PAYMENT -> {
                    Text(
                        "Metodo di pagamento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    PaymentSection(
                        selectedPayment = selectedPayment,
                        onSelect = { selectedPayment = it }
                    )
                    if (selectedPayment == PaymentMethod.CARD) {
                        CardListSection(
                            cards = uiState.paymentCards,
                            selectedCardId = selectedCard?.id,
                            onSelectCard = homeViewModel::setSelectedPaymentCard,
                            onRemoveCard = homeViewModel::removePaymentCard
                        )
                        OutlinedButton(
                            onClick = { showAddCardForm = !showAddCardForm },
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = com.example.altafedeltium.ui.theme.AccentText)
                        ) {
                            Text(if (showAddCardForm) "Chiudi nuova carta" else "Aggiungi carta")
                        }
                        if (showAddCardForm) {
                            AddCardForm(
                                holderName = cardHolder,
                                cardNumber = cardNumber,
                                expiry = cardExpiry,
                                cvv = cardCvv,
                                onHolderNameChange = { cardHolder = it },
                                onCardNumberChange = { cardNumber = it },
                                onExpiryChange = { cardExpiry = it },
                                onCvvChange = { cardCvv = it },
                                onSave = {
                                    val added = homeViewModel.addPaymentCard(
                                        holderName = cardHolder,
                                        cardNumber = cardNumber,
                                        expiry = cardExpiry,
                                        cvv = cardCvv
                                    )
                                    if (added) {
                                        showAddCardForm = false
                                        cardHolder = ""
                                        cardNumber = ""
                                        cardExpiry = ""
                                        cardCvv = ""
                                    } else {
                                        errorMessage = "Dati carta non validi. Controlla numero, scadenza e CVV."
                                        showErrorDialog = true
                                    }
                                }
                            )
                        }
                    }
                }

                CheckoutStep.SUMMARY -> {
                    Text(
                        "Riepilogo/Conferma ordine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    SummarySection(
                        selectedSupermarket = selectedSupermarket?.name ?: "Non selezionato",
                        selectedSupermarketDistance = selectedSupermarketChoice?.distanceKm,
                        selectedDeliveryDay = uiState.selectedDeliveryDay,
                        selectedAddress = selectedAddress?.let { "${it.street}, ${it.city}" } ?: "Nessun indirizzo",
                        selectedPayment = selectedPayment.label,
                        selectedCard = selectedCard,
                        subtotal = homeViewModel.subtotal(),
                        deliveryFee = homeViewModel.deliveryFee(),
                        total = homeViewModel.totalAmount()
                    )
                }
            }
        }
    }

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = "Conferma ordine",
            message = buildString {
                append("Supermercato: ${selectedSupermarket?.name ?: "-"}\n")
                append("Distanza: ${selectedSupermarketChoice?.distanceKm?.let { "%.1f km".format(it) } ?: "-"}\n")
                append("Giorno di consegna: ${uiState.selectedDeliveryDay}\n")
                append("Consegna: ${selectedAddress?.street ?: "-"}\n")
                append("Pagamento: ${selectedPayment.label}\n")
                if (selectedPayment == PaymentMethod.CARD) {
                    append("Carta: ${selectedCard?.brand ?: "Carta"} ****${selectedCard?.last4 ?: "----"}\n")
                }
                append("Totale: EUR ${"%.2f".format(homeViewModel.totalAmount())}\n\n")
                append("Vuoi confermare l'acquisto?")
            },
            confirmLabel = "Conferma",
            dismissLabel = "Annulla",
            onConfirm = {
                showConfirmDialog = false
                check(!showConfirmDialog)
                val placed = homeViewModel.placeOrder(selectedPayment, selectedSupermarket?.id, uiState.selectedDeliveryDay)
                if (placed) {
                    onOrderPlaced()
                } else {
                    errorMessage = "Impossibile completare l'ordine. Verifica carrello, indirizzo e supermercato."
                    showErrorDialog = true
                }
            },
            onDismiss = {
                showConfirmDialog = false
                check(!showConfirmDialog)
            }
        )
    }

    if (showErrorDialog) {
        ConfirmationDialog(
            title = "Attenzione",
            message = errorMessage,
            confirmLabel = "OK",
            dismissLabel = "Chiudi",
            onConfirm = {
                showErrorDialog = false
                check(!showErrorDialog)
            },
            onDismiss = {
                showErrorDialog = false
                check(!showErrorDialog)
            }
        )
    }
}

@Composable
private fun CheckoutBottomBar(
    total: Double,
    subtotal: Double = 0.0,
    deliveryFee: Double = 0.0,
    buttonLabel: String,
    enabled: Boolean,
    canGoBack: Boolean,
    onClick: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Breakdown dei costi
            if (subtotal > 0.0 || deliveryFee > 0.0) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotale:", style = MaterialTheme.typography.bodySmall)
                        Text("${"%.2f".format(subtotal)} EUR", style = MaterialTheme.typography.bodySmall)
                    }
                    if (deliveryFee > 0.0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Consegna:", style = MaterialTheme.typography.bodySmall)
                            Text("${"%.2f".format(deliveryFee)} EUR", style = MaterialTheme.typography.bodySmall, color = com.example.altafedeltium.ui.theme.AccentText)
                        }
                    }
                }
            }
            
            // Testo totale centrato
            Text(
                text = "Totale: ${"%.2f".format(total)} EUR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Riga con i pulsanti
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsante indietro
                if (canGoBack) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = com.example.altafedeltium.ui.theme.AccentText)
                    ) {
                        Text("Indietro")
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                // Pulsante avanti
                Button(
                    onClick = onClick,
                    enabled = enabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(buttonLabel)
                }
            }
        }
    }
}

@Composable
private fun SupermarketSection(
    supermarkets: List<SupermarketChoice>,
    selectedSupermarketId: Int?,
    onSelect: (Int) -> Unit,
    onUnavailableSelect: (SupermarketChoice) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        supermarkets.forEach { choice ->
            val supermarket = choice.supermarket
            val backgroundColor = when {
                !choice.isAvailable -> MaterialTheme.colorScheme.errorContainer
                choice.band == SupermarketDistanceBand.GREEN -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
            val titleColor = when {
                !choice.isAvailable -> MaterialTheme.colorScheme.onErrorContainer
                choice.band == SupermarketDistanceBand.GREEN -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> com.example.altafedeltium.ui.theme.AccentText // usa AccentText per supermarket non consigliati
            }
            val infoColor = when {
                !choice.isAvailable -> MaterialTheme.colorScheme.onErrorContainer
                choice.band == SupermarketDistanceBand.GREEN -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> com.example.altafedeltium.ui.theme.AccentText
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CustomRadioButton(
                        selected = selectedSupermarketId == supermarket.id,
                        onClick = {
                            if (choice.isAvailable) {
                                onSelect(supermarket.id)
                            } else {
                                // segnala al parent che è stato cliccato un supermercato non disponibile
                                onUnavailableSelect(choice)
                            }
                        },
                        enabled = choice.isAvailable
                    )
                    Column {
                        Text(supermarket.name, fontWeight = FontWeight.SemiBold, color = titleColor)
                        if (choice.band == SupermarketDistanceBand.GREEN) {
                            Text(
                                "Super consigliato",
                                style = MaterialTheme.typography.bodySmall,
                                color = infoColor
                            )
                        }
                        if (!choice.isAvailable) {
                            Text(
                                if (choice.distanceKm > 20.0) "Troppo lontano" else "Non disponibile nel giorno scelto",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Text(
                            "${"%.1f".format(choice.distanceKm)} km • ${deliveryLabel(supermarket.etaMinutes)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = infoColor
                        )
                        Text(
                            supermarket.area,
                            style = MaterialTheme.typography.bodySmall,
                            color = infoColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressSection(
    addresses: List<Address>,
    selectedAddressId: Int?,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        addresses.forEach { address ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CustomRadioButton(
                        selected = selectedAddressId == address.id,
                        onClick = { onSelect(address.id) }
                    )
                    Column {
                        Text(address.label, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Text("${address.street}, ${address.city}", style = MaterialTheme.typography.bodySmall, color = com.example.altafedeltium.ui.theme.AccentText)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentSection(
    selectedPayment: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PaymentMethod.entries.filter { it != PaymentMethod.SATISPAY }.forEach { method ->
            FilterChip(
                selected = selectedPayment == method,
                onClick = { onSelect(method) },
                label = { Text(method.label) }
            )
        }
    }
}

@Composable
private fun DeliveryDaySection(
    days: List<String>,
    selectedDay: String,
    onSelect: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Giorno di consegna",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = com.example.altafedeltium.ui.theme.AccentText
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    days.forEach { day ->
                        val isSelected = selectedDay == day
                        Surface(
                            onClick = { onSelect(day) },
                            modifier = Modifier.padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                            tonalElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                // Overlay con fade effect sulla destra
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(40.dp)
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun CardListSection(
    cards: List<PaymentCard>,
    selectedCardId: Int?,
    onSelectCard: (Int) -> Unit,
    onRemoveCard: (Int) -> Unit
) {
    var cardToRemove by remember { mutableStateOf<PaymentCard?>(null) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        cards.forEach { card ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CustomRadioButton(
                        selected = selectedCardId == card.id,
                        onClick = { onSelectCard(card.id) }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${card.brand} ****${card.last4}", fontWeight = FontWeight.SemiBold)
                        Text("${card.holderName} - Scad. ${card.expiry}", style = MaterialTheme.typography.bodySmall)
                    }
                    // red trash icon to remove card
                    IconButton(onClick = { cardToRemove = card }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Rimuovi carta", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    cardToRemove?.let { card ->
        ConfirmationDialog(
            title = "Rimuovi carta",
            message = "Sei sicuro di voler rimuovere la carta ${card.brand} ****${card.last4}?",
            confirmLabel = "Rimuovi",
            dismissLabel = "Annulla",
            onConfirm = {
                onRemoveCard(card.id)
                cardToRemove = null
            },
            onDismiss = { cardToRemove = null },
            destructiveConfirm = true
        )
    }
}

@Composable
private fun AddAddressForm(
    label: String,
    street: String,
    city: String,
    zipCode: String,
    onLabelChange: (String) -> Unit,
    onStreetChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onZipCodeChange: (String) -> Unit,
    onSave: (Double?, Double?) -> Unit
) {
    var zipError by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Nuovo indirizzo", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = label,
                onValueChange = onLabelChange,
                label = { Text("Etichetta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                )
            )
            OutlinedTextField(
                value = street,
                onValueChange = onStreetChange,
                label = { Text("Via e numero civico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                )
            )
            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("Città") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                )
            )
            OutlinedTextField(
                value = zipCode,
                onValueChange = { onZipCodeChange(it); zipError = null },
                label = { Text("CAP") },
                modifier = Modifier.fillMaxWidth(),
                isError = zipError != null,
                supportingText = { zipError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                )
            )
            var showMapPicker by rememberSaveable { mutableStateOf(false) }
            var pickedLat by rememberSaveable { mutableStateOf<Double?>(null) }
            var pickedLon by rememberSaveable { mutableStateOf<Double?>(null) }

            OutlinedButton(
                onClick = { showMapPicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Icon(Icons.Default.Place, contentDescription = null)
                Text("  Verifica posizione su mappa")
            }

            if (showMapPicker) {
                MapPickerDialog(
                    initialLat = pickedLat,
                    initialLon = pickedLon,
                    onDismiss = { showMapPicker = false },
                    onConfirm = { lat, lon ->
                        pickedLat = lat
                        pickedLon = lon
                        showMapPicker = false
                    }
                )
            }
            // mostra le coordinate scelte (stesso feedback presente nell'editor indirizzi del profilo)
            if (pickedLat != null && pickedLon != null) {
                val lat = pickedLat
                val lon = pickedLon
                Text("Entrata impostata: ${"%.5f".format(lat)} , ${"%.5f".format(lon)}", modifier = Modifier.padding(top = 4.dp))
            }
            Button(
                onClick = {
                    val zErr = if (!zipCode.matches(Regex("^\\d{5}$"))) "Il CAP deve essere composto da 5 cifre" else null
                    zipError = zErr
                    if (label.isNotBlank() && street.isNotBlank() && city.isNotBlank() && zErr == null) {
                        onSave(pickedLat, pickedLon)
                    }
                },
                enabled = label.isNotBlank() && street.isNotBlank() && city.isNotBlank() && zipCode.matches(Regex("^\\d{5}$")),
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Salva indirizzo", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
private fun AddCardForm(
    holderName: String,
    cardNumber: String,
    expiry: String,
    cvv: String,
    onHolderNameChange: (String) -> Unit,
    onCardNumberChange: (String) -> Unit,
    onExpiryChange: (String) -> Unit,
    onCvvChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Nuova carta", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = holderName,
                onValueChange = onHolderNameChange,
                label = { Text("Intestatario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                )
            )
            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                label = { Text("Numero carta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                    focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = expiry,
                    onValueChange = onExpiryChange,
                    label = { Text("Scadenza MM/AA") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                        focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                        focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                    )
                )
                OutlinedTextField(
                    value = cvv,
                    onValueChange = onCvvChange,
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = com.example.altafedeltium.ui.theme.AccentText,
                        focusedBorderColor = com.example.altafedeltium.ui.theme.AccentText,
                        focusedLabelColor = com.example.altafedeltium.ui.theme.AccentText
                    )
                )
            }
            Button(
                onClick = onSave,
                enabled = holderName.isNotBlank() && cardNumber.length >= 13 && expiry.length == 5 && cvv.length >= 3,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Aggiungi carta")
            }
        }
    }
}

@Composable
private fun SummarySection(
    selectedSupermarket: String,
    selectedSupermarketDistance: Double?,
    selectedDeliveryDay: String,
    selectedAddress: String,
    selectedPayment: String,
    selectedCard: PaymentCard?,
    subtotal: Double,
    deliveryFee: Double,
    total: Double
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("Supermercato selezionato:")
            Text(
                if (selectedSupermarketDistance != null) {
                    "$selectedSupermarket (${String.format(Locale.ITALIAN, "%.1f km", selectedSupermarketDistance)})"
                } else {
                    selectedSupermarket
                },
                fontWeight = FontWeight.SemiBold
            )
            Text("Giorno di consegna:")
            Text(selectedDeliveryDay, fontWeight = FontWeight.SemiBold)
            Text("Consegna:")
            Text(selectedAddress, fontWeight = FontWeight.SemiBold)
            Text("Metodo di pagamento:")
            Text(selectedPayment, fontWeight = FontWeight.SemiBold)
            if (selectedPayment == PaymentMethod.CARD.label) {
                Text("Carta:")
                Text(
                    selectedCard?.let { "${it.brand} ****${it.last4}" } ?: "Nessuna carta selezionata",
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text("Subtotale: EUR ${"%.2f".format(subtotal)}")
            Text("Consegna: EUR ${"%.2f".format(deliveryFee)}")
            Text("Totale: EUR ${"%.2f".format(total)}", fontWeight = FontWeight.Bold)
        }
    }
}

private fun generateDeliveryDays(): List<String> {
    val calendar = Calendar.getInstance(Locale.ITALIAN)
    return listOf(
        "Oggi",
        "Domani",
        SimpleDateFormat("EEE dd MMM", Locale.ITALIAN).format((calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 2) }.time),
        SimpleDateFormat("EEE dd MMM", Locale.ITALIAN).format((calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 3) }.time)
    )
}

private fun deliveryLabel(etaMinutes: Int): String = when {
    etaMinutes <= 30 -> "Consegna oggi • circa ${etaMinutes} min"
    etaMinutes <= 45 -> "Consegna domani • circa ${etaMinutes} min"
    else -> "Consegna domani • circa ${etaMinutes} min"
}

