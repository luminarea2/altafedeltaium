package com.example.altafedeltium.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val imageRes: Int? = null,
    val icon: ImageVector? = null,
    val description: String = "",
    val stock: Int = 10
)
