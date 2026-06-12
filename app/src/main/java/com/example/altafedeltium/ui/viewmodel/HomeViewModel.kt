package com.example.altafedeltium.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.altafedeltium.data.mock.ProductMockData
import com.example.altafedeltium.data.model.Address
import com.example.altafedeltium.data.model.CartItem
import com.example.altafedeltium.data.model.Order
import com.example.altafedeltium.data.model.PaymentCard
import com.example.altafedeltium.data.model.PaymentMethod
import com.example.altafedeltium.data.model.Product
import com.example.altafedeltium.data.model.SortDirection
import com.example.altafedeltium.data.model.SortField
import com.example.altafedeltium.data.model.SupermarketOption
import com.example.altafedeltium.data.model.SupermarketChoice
import com.example.altafedeltium.data.model.SupermarketDistanceBand
import com.example.altafedeltium.data.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val products: List<Product> = ProductMockData.products,
    val selectedCategories: Set<String> = setOf("Tutti"),
    val searchQuery: String = "",
    val maxPriceFilter: Double = ProductMockData.products.maxOfOrNull { it.price } ?: 0.0,
    val selectedSortField: SortField = SortField.NAME,
    val selectedSortDirection: SortDirection = SortDirection.ASC,
    val favoriteProductIds: Set<Int> = emptySet(),
    val cartItems: List<CartItem> = emptyList(),
    val supermarkets: List<SupermarketOption> = listOf(
        SupermarketOption(
            1,
            "SuperSpan Castellabate Centro",
            "Castellabate",
            25,
            latitude = 42.1119,
            longitude = 14.7072,
            availableDays = listOf("Oggi", "Domani"),
            deliveryZones = listOf("Vasto", "San Salvo"),
            recommendedZones = listOf("Vasto")
        ),
        SupermarketOption(
            2,
            "SpesaClick Santa Maria",
            "Santa Maria di Castellabate",
            35,
            latitude = 42.1965,
            longitude = 14.9450,
            availableDays = listOf("Domani"),
            deliveryZones = listOf("Castellabate", "Santa Maria di Castellabate"),
            recommendedZones = listOf("Castellabate")
        ),
        SupermarketOption(
            3,
            "Market Facile Agropoli",
            "Agropoli",
            42,
            latitude = 40.3495,
            longitude = 15.0030,
            availableDays = listOf("Domani"),
            deliveryZones = listOf("San Salvo"),
            recommendedZones = listOf("San Salvo")
        ),
        SupermarketOption(
            4,
            "Fresh Point Vasto Marina",
            "Vasto Marina",
            18,
            latitude = 42.1037,
            longitude = 14.7423,
            availableDays = listOf("Oggi", "Domani"),
            deliveryZones = listOf("Vasto", "Vasto Marina"),
            recommendedZones = listOf("Vasto")
        ),
        SupermarketOption(
            5,
            "Market24 Centro Città",
            "Centro Città",
            30,
            latitude = 42.1580,
            longitude = 14.7560,
            availableDays = listOf("Oggi", "Domani"),
            deliveryZones = listOf("Vasto", "San Salvo", "Centro Città"),
            recommendedZones = listOf("San Salvo")
        ),
        SupermarketOption(
            6,
            "Daily Shop Nord",
            "Zona Nord",
            48,
            latitude = 42.2700,
            longitude = 14.6400,
            availableDays = listOf("Domani"),
            deliveryZones = listOf("Zona Nord", "Agropoli"),
            recommendedZones = listOf("Agropoli")
        ),
        SupermarketOption(
            7,
            "Easy Market San Salvo",
            "San Salvo",
            14,
            latitude = 42.0458,
            longitude = 14.7319,
            availableDays = listOf("Oggi", "Domani"),
            deliveryZones = listOf("San Salvo", "Vasto"),
            recommendedZones = listOf("San Salvo")
        ),
        SupermarketOption(
            8,
            "Hyper Discount Trignina",
            "Trignina",
            55,
            latitude = 42.3400,
            longitude = 14.4700,
            availableDays = listOf("Domani"),
            deliveryZones = listOf("Trignina"),
            recommendedZones = listOf()
        )
    ),
    val selectedSupermarketId: Int? = 1,
    val selectedDeliveryDay: String = "Oggi",
    val addresses: List<Address> = listOf(
        Address(1, "Casa", "Via Roma 24", "Vasto", "66054", latitude = 42.1127, longitude = 14.7063, isDefault = true),
        Address(2, "Lavoro", "Via Europa 8", "San Salvo", "66050", latitude = 42.0462, longitude = 14.7307)
    ),
    val selectedAddressId: Int? = 1,
    val paymentCards: List<PaymentCard> = listOf(
        PaymentCard(id = 1, holderName = "Paolo Cortellesi", last4 = "9012", expiry = "12/34", brand = "Mastercard"),
        PaymentCard(id = 2, holderName = "Paolo Cortellesi", last4 = "4321", expiry = "11/29", brand = "Visa")
    ),
    val selectedPaymentCardId: Int? = 1,
    val userProfile: UserProfile = UserProfile(
        firstName = "Paolo",
        lastName = "Cortellesi",
        email = "paolo.cortellesi@email.it",
        // default phone must be empty: do not show placeholder/sample number in profile until user fills it
        phone = "",
        username = "Paolo123",
        loyaltyCode = "AF-10293-PA",
        memberSince = "Feb 2024"
    ),
    val orders: List<Order> = emptyList()
)

class HomeViewModel : ViewModel() {
    private val _uiState = mutableStateOf(HomeUiState())
    val uiState: State<HomeUiState> = _uiState

    val categories: List<String>
        get() = listOf("Tutti") + _uiState.value.products.map { it.category }.distinct()

    val maxPriceLimit: Double
        get() = _uiState.value.products.maxOfOrNull { it.price } ?: 0.0

    fun onCategorySelected(category: String) {
        val current = _uiState.value.selectedCategories.toMutableSet()
        
        if (category == "Tutti") {
            // Se seleziona "Tutti", deseleziona tutte le altre
            _uiState.value = _uiState.value.copy(selectedCategories = setOf("Tutti"))
        } else {
            // Se era selezionato il "Tutti", rimuovilo
            if (current.contains("Tutti")) {
                current.remove("Tutti")
            }
            
            // Toggle della categoria selezionata
            if (current.contains(category)) {
                current.remove(category)
                // Se rimane vuoto, seleziona "Tutti"
                if (current.isEmpty()) {
                    current.add("Tutti")
                }
            } else {
                current.add(category)
            }
            _uiState.value = _uiState.value.copy(selectedCategories = current)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onMaxPriceChanged(price: Double) {
        _uiState.value = _uiState.value.copy(maxPriceFilter = price)
    }

    fun onSortFieldChanged(sortField: SortField) {
        _uiState.value = _uiState.value.copy(selectedSortField = sortField)
    }

    fun onSortDirectionChanged(direction: SortDirection) {
        _uiState.value = _uiState.value.copy(selectedSortDirection = direction)
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCategories = setOf("Tutti"),
            searchQuery = "",
            maxPriceFilter = maxPriceLimit,
            selectedSortField = SortField.NAME,
            selectedSortDirection = SortDirection.ASC
        )
    }

    fun filteredProducts(): List<Product> {
        val current = _uiState.value
        val filtered = current.products.filter { product ->
            val matchesCategory = current.selectedCategories.contains("Tutti") || 
                current.selectedCategories.contains(product.category)
            val matchesQuery = current.searchQuery.isBlank() ||
                product.name.contains(current.searchQuery, ignoreCase = true)
            val matchesPrice = product.price <= current.maxPriceFilter
            matchesCategory && matchesQuery && matchesPrice
        }

        return when (current.selectedSortField) {
            SortField.NAME -> {
                if (current.selectedSortDirection == SortDirection.ASC) {
                    filtered.sortedBy { it.name.lowercase(Locale.ITALIAN) }
                } else {
                    filtered.sortedByDescending { it.name.lowercase(Locale.ITALIAN) }
                }
            }

            SortField.PRICE -> {
                if (current.selectedSortDirection == SortDirection.ASC) {
                    filtered.sortedBy { it.price }
                } else {
                    filtered.sortedByDescending { it.price }
                }
            }

            SortField.CATEGORY -> {
                if (current.selectedSortDirection == SortDirection.ASC) {
                    filtered.sortedWith(
                        compareBy<Product> { it.category.lowercase(Locale.ITALIAN) }
                            .thenBy { it.name.lowercase(Locale.ITALIAN) }
                    )
                } else {
                    filtered.sortedWith(
                        compareByDescending<Product> { it.category.lowercase(Locale.ITALIAN) }
                            .thenByDescending { it.name.lowercase(Locale.ITALIAN) }
                    )
                }
            }
        }
    }

    fun getProductById(productId: Int): Product? = _uiState.value.products.find { it.id == productId }

    fun isFavorite(productId: Int): Boolean = _uiState.value.favoriteProductIds.contains(productId)

    fun toggleFavorite(productId: Int) {
        val favorites = _uiState.value.favoriteProductIds.toMutableSet()
        if (!favorites.add(productId)) favorites.remove(productId)
        _uiState.value = _uiState.value.copy(favoriteProductIds = favorites)
    }

    // Check whether a product is currently in the cart
    fun isInCart(productId: Int): Boolean = _uiState.value.cartItems.any { it.product.id == productId }

    fun cartItemQuantity(productId: Int): Int =
        _uiState.value.cartItems.firstOrNull { it.product.id == productId }?.quantity ?: 0

    fun favoriteProducts(): List<Product> {
        val ids = _uiState.value.favoriteProductIds
        return _uiState.value.products.filter { ids.contains(it.id) }
    }

    fun addToCart(product: Product) {
        // Check if product has available stock
        if (product.stock <= 0) return

        val existing = _uiState.value.cartItems.firstOrNull { it.product.id == product.id }
        if (existing != null) {
            increaseQuantity(product.id)
            return
        }

        // Decrease stock when adding to cart
        decreaseProductStock(product.id)

        _uiState.value = _uiState.value.copy(
            cartItems = _uiState.value.cartItems + CartItem(product = product, quantity = 1)
        )
    }

    private fun decreaseProductStock(productId: Int) {
        _uiState.value = _uiState.value.copy(
            products = _uiState.value.products.map { product ->
                if (product.id == productId) {
                    product.copy(stock = (product.stock - 1).coerceAtLeast(0))
                } else {
                    product
                }
            }
        )
    }

    fun increaseQuantity(productId: Int) {
        // Check if we can increase quantity (stock available)
        val product = _uiState.value.products.find { it.id == productId }
        if (product == null || product.stock <= 0) return

        // Decrease stock when increasing quantity
        decreaseProductStock(productId)

        _uiState.value = _uiState.value.copy(
            cartItems = _uiState.value.cartItems.map {
                if (it.product.id == productId) it.copy(quantity = it.quantity + 1) else it
            }
        )
    }

    fun decreaseQuantity(productId: Int) {
        // Increase stock when decreasing quantity (item goes back to available stock)
        increaseProductStock(productId)

        val updated = _uiState.value.cartItems.mapNotNull {
            if (it.product.id != productId) return@mapNotNull it
            val next = it.quantity - 1
            if (next <= 0) null else it.copy(quantity = next)
        }
        _uiState.value = _uiState.value.copy(cartItems = updated)
    }

    private fun increaseProductStock(productId: Int) {
        _uiState.value = _uiState.value.copy(
            products = _uiState.value.products.map { product ->
                if (product.id == productId) {
                    product.copy(stock = product.stock + 1)
                } else {
                    product
                }
            }
        )
    }

    fun removeFromCart(productId: Int) {
        val cartItem = _uiState.value.cartItems.find { it.product.id == productId }
        if (cartItem != null) {
            // Restore stock for all units being removed
            repeat(cartItem.quantity) {
                increaseProductStock(productId)
            }
        }
        _uiState.value = _uiState.value.copy(
            cartItems = _uiState.value.cartItems.filterNot { it.product.id == productId }
        )
    }

    fun cartItemsCount(): Int = _uiState.value.cartItems.sumOf { it.quantity }

    fun subtotal(): Double = _uiState.value.cartItems.sumOf { it.product.price * it.quantity }

    fun deliveryFee(): Double = if (_uiState.value.cartItems.isEmpty()) 0.0 else 2.99

    fun totalAmount(): Double = subtotal() + deliveryFee()

    fun setSelectedAddress(addressId: Int) {
        val nextState = _uiState.value.copy(selectedAddressId = addressId)
        val nextSelection = supermarketsForAddress(nextState.addresses.find { it.id == addressId }, nextState.selectedDeliveryDay)
            .firstOrNull { it.isAvailable }
            ?.supermarket
            ?: supermarketsForAddress(nextState.addresses.find { it.id == addressId }, nextState.selectedDeliveryDay).firstOrNull()?.supermarket

        _uiState.value = nextState.copy(selectedSupermarketId = nextSelection?.id ?: nextState.selectedSupermarketId)
    }

    fun setSelectedSupermarket(supermarketId: Int) {
        val supermarket = _uiState.value.supermarkets.find { it.id == supermarketId }
        if (supermarket != null && supermarketsForSelectedAddress().any { it.id == supermarketId && it.isAvailable }) {
            _uiState.value = _uiState.value.copy(selectedSupermarketId = supermarketId)
        }
    }

    fun supermarketsForSelectedAddress(): List<SupermarketOption> {
        return supermarketsForAddress(selectedAddress(), _uiState.value.selectedDeliveryDay)
            .map { it.supermarket }
    }

    fun supermarketsForSelectedAddressChoices(): List<SupermarketChoice> {
        return supermarketsForAddress(selectedAddress(), _uiState.value.selectedDeliveryDay)
    }

    fun setSelectedDeliveryDay(day: String) {
        val nextState = _uiState.value.copy(selectedDeliveryDay = day)
        val nextChoices = supermarketsForAddress(nextState.addresses.find { it.id == nextState.selectedAddressId }, day)
        val currentSelected = nextChoices.firstOrNull { it.supermarket.id == nextState.selectedSupermarketId }
        val fallback = nextChoices.firstOrNull { it.isAvailable } ?: nextChoices.firstOrNull()
        _uiState.value = nextState.copy(
            selectedSupermarketId = if (currentSelected?.isAvailable == true) currentSelected.supermarket.id else fallback?.supermarket?.id
        )
    }

    fun setSelectedPaymentCard(cardId: Int) {
        if (_uiState.value.paymentCards.any { it.id == cardId }) {
            _uiState.value = _uiState.value.copy(selectedPaymentCardId = cardId)
        }
    }

    private fun selectedAddress(): Address? = _uiState.value.addresses.find { it.id == _uiState.value.selectedAddressId }

    private fun normalize(value: String?): String = value.orEmpty().trim().lowercase(Locale.ITALIAN)

    private fun supermarketsForAddress(address: Address?, selectedDay: String): List<SupermarketChoice> {
        val latitude = address?.latitude
        val longitude = address?.longitude

        return _uiState.value.supermarkets.map { supermarket ->
            val distanceKm = if (latitude != null && longitude != null && supermarket.latitude != null && supermarket.longitude != null) {
                haversineKm(latitude, longitude, supermarket.latitude, supermarket.longitude)
            } else {
                Double.POSITIVE_INFINITY
            }

            val dayAvailable = supermarket.availableDays.isEmpty() || supermarket.availableDays.any { normalize(it) == normalize(selectedDay) }
            val zoneAvailable = if (address == null) {
                true
            } else {
                supermarket.deliveryZones.isEmpty() || supermarket.deliveryZones.any { zone ->
                    val normalizedZone = normalize(zone)
                    val city = normalize(address.city)
                    val zip = normalize(address.zipCode)
                    normalizedZone == city || normalizedZone == zip || city.contains(normalizedZone) || normalizedZone.contains(city)
                }
            }

            val isAvailable = dayAvailable && zoneAvailable
            val band = when {
                !isAvailable -> SupermarketDistanceBand.RED
                distanceKm < 5.0 -> SupermarketDistanceBand.GREEN
                distanceKm <= 20.0 -> SupermarketDistanceBand.WHITE
                else -> SupermarketDistanceBand.RED
            }

            val recommended = isAvailable && band == SupermarketDistanceBand.GREEN && (
                supermarket.recommendedZones.isEmpty() ||
                    address == null ||
                    supermarket.recommendedZones.any { zone -> normalize(zone) == normalize(address.city) || normalize(zone) == normalize(address.zipCode) }
            )

            SupermarketChoice(
                supermarket = supermarket.copy(
                    isAvailable = isAvailable,
                    isRecommended = recommended
                ),
                distanceKm = distanceKm,
                band = band,
                isAvailable = isAvailable,
                isRecommended = recommended
            )
        }.sortedWith(
            compareBy<SupermarketChoice> { choice ->
                when (choice.band) {
                    SupermarketDistanceBand.GREEN -> 0
                    SupermarketDistanceBand.WHITE -> 1
                    SupermarketDistanceBand.RED -> 2
                }
            }.thenBy { it.distanceKm }
        ).let { sortedChoices ->
            // Ensure at least one supermarket is always available to avoid a state
            // in which all choices are marked non-available (which can happen if
            // address/day matching logic is too strict). If none are available,
            // mark the closest supermarket as available (fallback behaviour).
            if (sortedChoices.any { it.isAvailable }) {
                sortedChoices
            } else {
                sortedChoices.mapIndexed { index, choice ->
                    if (index == 0) {
                        choice.copy(
                            supermarket = choice.supermarket.copy(isAvailable = true),
                            isAvailable = true,
                            band = if (choice.band == SupermarketDistanceBand.RED) SupermarketDistanceBand.WHITE else choice.band
                        )
                    } else choice
                }
            }
        }
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2).let { sinLat ->
            val sinLon = kotlin.math.sin(dLon / 2)
            sinLat * sinLat + kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) * sinLon * sinLon
        }
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }

    fun addPaymentCard(holderName: String, cardNumber: String, expiry: String, cvv: String): Boolean {
        val cleanNumber = cardNumber.filter(Char::isDigit)
        val cleanCvv = cvv.filter(Char::isDigit)
        val normalizedExpiry = expiry.trim()
        if (holderName.isBlank() || cleanNumber.length !in 13..19 || cleanCvv.length !in 3..4 || !isValidExpiry(normalizedExpiry)) {
            return false
        }

        val nextId = (_uiState.value.paymentCards.maxOfOrNull { it.id } ?: 0) + 1
        val brand = cardBrandFromNumber(cleanNumber)
        val card = PaymentCard(
            id = nextId,
            holderName = holderName.trim(),
            last4 = cleanNumber.takeLast(4),
            expiry = normalizedExpiry,
            brand = brand
        )

        _uiState.value = _uiState.value.copy(
            paymentCards = _uiState.value.paymentCards + card,
            selectedPaymentCardId = card.id
        )
        return true
    }

    @Suppress("unused")
    private fun addAddress(label: String, street: String, city: String, zipCode: String) {
        addAddressWithCoordinates(
            label = label,
            street = street,
            city = city,
            zipCode = zipCode,
            latitude = null,
            longitude = null
        )
    }

    fun addAddressWithCoordinates(
        label: String,
        street: String,
        city: String,
        zipCode: String,
        latitude: Double?,
        longitude: Double?
    ) {
        val nextId = (_uiState.value.addresses.maxOfOrNull { it.id } ?: 0) + 1
        val newAddress = Address(
            id = nextId,
            label = label,
            street = street,
            city = city,
            zipCode = zipCode,
            latitude = latitude,
            longitude = longitude,
            isDefault = _uiState.value.addresses.isEmpty()
        )
        _uiState.value = _uiState.value.copy(addresses = _uiState.value.addresses + newAddress)
    }

    fun removeAddress(addressId: Int) {
        val remaining = _uiState.value.addresses.filterNot { it.id == addressId }
        val normalized = if (remaining.isNotEmpty() && remaining.none { it.isDefault }) {
            remaining.mapIndexed { index, address -> address.copy(isDefault = index == 0) }
        } else {
            remaining
        }
        val selected = _uiState.value.selectedAddressId
        _uiState.value = _uiState.value.copy(
            addresses = normalized,
            selectedAddressId = when {
                selected == addressId -> normalized.firstOrNull()?.id
                normalized.any { it.id == selected } -> selected
                else -> normalized.firstOrNull()?.id
            }
        )
    }

    fun setDefaultAddress(addressId: Int) {
        val updated = _uiState.value.addresses.map {
            it.copy(isDefault = it.id == addressId)
        }
        _uiState.value = _uiState.value.copy(addresses = updated, selectedAddressId = addressId)
    }

    @Suppress("unused")
    private fun updateAddress(addressId: Int, label: String, street: String, city: String, zipCode: String) {
        updateAddressWithCoordinates(
            addressId = addressId,
            label = label,
            street = street,
            city = city,
            zipCode = zipCode,
            latitude = null,
            longitude = null
        )
    }

    fun updateAddressWithCoordinates(
        addressId: Int,
        label: String,
        street: String,
        city: String,
        zipCode: String,
        latitude: Double?,
        longitude: Double?
    ) {
        _uiState.value = _uiState.value.copy(
            addresses = _uiState.value.addresses.map { address ->
                if (address.id == addressId) {
                    address.copy(
                        label = label,
                        street = street,
                        city = city,
                        zipCode = zipCode,
                        latitude = latitude ?: address.latitude,
                        longitude = longitude ?: address.longitude
                    )
                } else {
                    address
                }
            }
        )
    }

    fun updateUserProfile(firstName: String, lastName: String, email: String, phone: String) {
        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone
            )
        )

        // Persist the change to the authenticated session store if a user is logged in
        try {
            AuthSessionStore.updateCurrentUser(firstName = firstName, lastName = lastName, email = email, phone = phone)
        } catch (_: Exception) {
            // Non fatale: se lo store non è inizializzato o non c'è un utente corrente, ignoriamo
        }
    }

    fun syncProfileFromAuthenticatedUser(fullName: String, email: String) {
        val cleanName = fullName.trim()
        val parts = cleanName.split(" ").filter { it.isNotBlank() }
        val firstName = parts.firstOrNull() ?: cleanName
        val lastName = parts.drop(1).joinToString(" ").ifBlank { "-" }
        val username = email.substringBefore("@").ifBlank { firstName }

        // Try to get phone from AuthSessionStore if present
        val phone = try { AuthSessionStore.currentUser?.phone ?: "" } catch (_: Exception) { "" }

        _uiState.value = _uiState.value.copy(
            userProfile = _uiState.value.userProfile.copy(
                firstName = firstName,
                lastName = lastName,
                email = email.trim().lowercase(Locale.ITALIAN),
                username = username,
                phone = phone
            )
        )
    }

    fun placeOrder(
        paymentMethod: PaymentMethod,
        supermarketId: Int? = _uiState.value.selectedSupermarketId,
        deliveryDay: String? = null
    ): Boolean {
        val state = _uiState.value
        val actualDeliveryDay = deliveryDay ?: state.selectedDeliveryDay
        val supermarket = state.supermarkets.find { it.id == supermarketId }
        val address = state.addresses.find { it.id == state.selectedAddressId } ?: state.addresses.firstOrNull()
        if (state.cartItems.isEmpty() || address == null || supermarket == null) return false

        val order = Order(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            items = state.cartItems,
            total = totalAmount(),
            supermarketName = supermarket.name,
            deliveryDay = actualDeliveryDay,
            address = address,
            paymentMethod = paymentMethod,
            createdAt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ITALIAN).format(Date()),
            status = "Confermato"
        )

        _uiState.value = state.copy(
            orders = listOf(order) + state.orders,
            cartItems = emptyList()
        )
        return true
    }

    /**
     * Clear user-specific transient state (used on logout).
     * Currently clears the cart so a new user won't see the previous user's items.
     */
    fun clearSession() {
        _uiState.value = _uiState.value.copy(
            cartItems = emptyList()
        )
    }
}

private fun cardBrandFromNumber(cardNumber: String): String = when {
    cardNumber.startsWith("4") -> "Visa"
    cardNumber.startsWith("5") -> "Mastercard"
    cardNumber.startsWith("34") || cardNumber.startsWith("37") -> "Amex"
    else -> "Carta"
}

private fun isValidExpiry(expiry: String): Boolean {
    val regex = Regex("^(0[1-9]|1[0-2])/[0-9]{2}$")
    return regex.matches(expiry)
}

