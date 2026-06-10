package com.example.altafedeltium.data.mock

import com.example.altafedeltium.R
import com.example.altafedeltium.data.model.Product

object ProductMockData {
    val products = listOf(
        Product(1, "Mele Golden", 1.99, "Frutta", imageRes = R.drawable.melegialle, description = "Mele croccanti e dolci, ideali per merenda."),
        Product(2, "Banane", 1.49, "Frutta", imageRes = R.drawable.banane, description = "Banane mature al punto giusto, ricche di potassio."),
        Product(3, "Latte Intero", 1.39, "Latticini", imageRes = R.drawable.latteintero, description = "Latte fresco intero da allevamenti locali."),
        Product(4, "Yogurt Bianco", 2.29, "Latticini", imageRes = R.drawable.yogurtbianco, description = "Yogurt naturale senza zuccheri aggiunti."),
        Product(5, "Pasta Penne", 0.99, "Dispensa", imageRes = R.drawable.pastapenne, description = "Pasta di semola di grano duro trafilata al bronzo."),
        Product(6, "Passata di Pomodoro", 1.29, "Dispensa", imageRes = R.drawable.passatapomodoro, description = "Passata vellutata di pomodori 100% italiani."),
        Product(7, "Petto di Pollo", 6.99, "Macelleria", imageRes = R.drawable.pettodipollo, description = "Fettine di petto di pollo tenero e magro."),
        Product(8, "Salmone Fresco", 9.49, "Pescheria", imageRes = R.drawable.salmonefresco, description = "Trancio di salmone fresco, ricco di Omega-3.")
    )
}
