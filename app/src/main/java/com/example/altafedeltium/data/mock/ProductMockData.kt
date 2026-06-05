package com.example.altafedeltium.data.mock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.Spa
import com.example.altafedeltium.data.model.Product

object ProductMockData {
    val products = listOf(
        Product(1, "Mele Golden", 1.99, "Frutta", icon = Icons.Default.Spa, description = "Mele croccanti e dolci, ideali per merenda."),
        Product(2, "Banane", 1.49, "Frutta", icon = Icons.Default.Agriculture, description = "Banane mature al punto giusto, ricche di potassio."),
        Product(3, "Latte Intero", 1.39, "Latticini", icon = Icons.Default.LocalDrink, description = "Latte fresco intero da allevamenti locali."),
        Product(4, "Yogurt Bianco", 2.29, "Latticini", icon = Icons.Default.Egg, description = "Yogurt naturale senza zuccheri aggiunti."),
        Product(5, "Pasta Penne", 0.99, "Dispensa", icon = Icons.Default.BakeryDining, description = "Pasta di semola di grano duro trafilata al bronzo."),
        Product(6, "Passata di Pomodoro", 1.29, "Dispensa", icon = Icons.Default.Kitchen, description = "Passata vellutata di pomodori 100% italiani."),
        Product(7, "Petto di Pollo", 6.99, "Macelleria", icon = Icons.Default.BreakfastDining, description = "Fettine di petto di pollo tenero e magro."),
        Product(8, "Salmone Fresco", 9.49, "Pescheria", icon = Icons.Default.SetMeal, description = "Trancio di salmone fresco, ricco di Omega-3.")
    )
}
