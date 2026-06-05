package com.example.altafedeltium.data.model

data class CartItem(
    val product: Product,
    val quantity: Int
)

data class Address(
    val id: Int,
    val label: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
)

data class SupermarketOption(
    val id: Int,
    val name: String,
    val area: String,
    val etaMinutes: Int,
    val isAvailable: Boolean = true,
    val isRecommended: Boolean = false,
    val deliveryZones: List<String> = emptyList(),
    val recommendedZones: List<String> = emptyList()
)

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val username: String,
    val loyaltyCode: String,
    val memberSince: String
)

data class PaymentCard(
    val id: Int,
    val holderName: String,
    val last4: String,
    val expiry: String,
    val brand: String
)

enum class PaymentMethod(val label: String) {
    CARD("Carta"),
    CASH_ON_DELIVERY("Pagamento alla consegna"),
    SATISPAY("Satispay")
}

enum class SortField(val label: String) {
    NAME("Nome"),
    PRICE("Prezzo"),
    CATEGORY("Categoria")
}

enum class SortDirection(val label: String) {
    ASC("Crescente"),
    DESC("Decrescente")
}

data class Order(
    val id: Int,
    val items: List<CartItem>,
    val total: Double,
    val supermarketName: String,
    val deliveryDay: String,
    val address: Address,
    val paymentMethod: PaymentMethod,
    val createdAt: String,
    val status: String
)

