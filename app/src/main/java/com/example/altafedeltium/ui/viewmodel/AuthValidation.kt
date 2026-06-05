package com.example.altafedeltium.ui.viewmodel

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Inserisci l'email"
        !emailRegex.matches(email) -> "Email non valida"
        else -> null
    }
}

fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Inserisci la password"
        password.length < 6 -> "La password deve contenere almeno 6 caratteri"
        else -> null
    }
}

