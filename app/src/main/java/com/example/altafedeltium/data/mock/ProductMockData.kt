package com.example.altafedeltium.data.mock

import com.example.altafedeltium.R
import com.example.altafedeltium.data.model.Product

object ProductMockData {
    val products = listOf(
        Product(1, "Mele Golden", 1.99, "Frutta", R.drawable.ic_launcher_foreground),
        Product(2, "Banane", 1.49, "Frutta", R.drawable.ic_launcher_foreground),
        Product(3, "Latte Intero", 1.39, "Latticini", R.drawable.ic_launcher_foreground),
        Product(4, "Yogurt Bianco", 2.29, "Latticini", R.drawable.ic_launcher_foreground),
        Product(5, "Pasta Penne", 0.99, "Dispensa", R.drawable.ic_launcher_foreground),
        Product(6, "Passata di Pomodoro", 1.29, "Dispensa", R.drawable.ic_launcher_foreground),
        Product(7, "Petto di Pollo", 6.99, "Macelleria", R.drawable.ic_launcher_foreground),
        Product(8, "Salmone Fresco", 9.49, "Pescheria", R.drawable.ic_launcher_foreground)
    )
}
