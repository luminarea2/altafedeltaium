package com.example.altafedeltium.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// New Palette based on #FFBF70
val MainOrange = Color(0xFFFFBF70)
val MainOrangeDark = Color(0xFFD98C40) // Darker shade for contrast
val MainOrangeLight = Color(0xFFFFDAB0) // Lighter shade

// High Contrast Orange Palette for Dark Mode
val Orange80 = Color(0xFFFFD19A)
val OrangeGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Orange40 = Color(0xFFD98C40)
val OrangeGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Material 3 Color Scheme based on #FFBF70
val OrangePrimary = Color(0xFFFFBF70)
val OrangeOnPrimary = Color(0xFF4D2600) // High contrast dark brown on light orange
val OrangePrimaryContainer = Color(0xFFFFDAB0)
val OrangeOnPrimaryContainer = Color(0xFF2B1400)

val OrangeSecondary = Color(0xFF765A41)
val OrangeOnSecondary = Color(0xFFFFFFFF)
val OrangeSecondaryContainer = Color(0xFFFFDDBF)
val OrangeOnSecondaryContainer = Color(0xFF2B1705)

val OrangeTertiary = Color(0xFF5A6234)
val OrangeOnTertiary = Color(0xFFFFFFFF)

val OrangeBackground = Color(0xFFFFFBFF)
val OrangeSurface = Color(0xFFFFFBFF)

val WarmOrange = Color(0xFFFFBF70)
val WarmOrangeLight = Color(0xFFFFDAB0)

// Gradient based on #FFBF70 with high contrast against white text/icons
// We use a darker version for the bottom of the gradient to ensure white readability
val WarmOrangeGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFBF70), // The requested color
        Color(0xFFD98C40)  // Darker version for contrast
    )
)
