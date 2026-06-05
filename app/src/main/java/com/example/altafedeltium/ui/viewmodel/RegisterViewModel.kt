package com.example.altafedeltium.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val formError: String? = null
)

class RegisterViewModel : ViewModel() {
    private val _uiState = mutableStateOf(RegisterUiState())
    val uiState: State<RegisterUiState> = _uiState

    fun onFirstNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, firstNameError = null, formError = null)
    }

    fun onLastNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value, lastNameError = null, formError = null)
    }

    fun onEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null, formError = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, passwordError = null, formError = null)
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, confirmPasswordError = null, formError = null)
    }

    fun submit(): Boolean {
        val current = _uiState.value
        val firstNameError = if (current.firstName.isBlank()) "Inserisci il nome" else null
        val lastNameError = if (current.lastName.isBlank()) "Inserisci il cognome" else null
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)
        val confirmPasswordError = when {
            current.confirmPassword.isBlank() -> "Conferma la password"
            current.confirmPassword != current.password -> "Le password non coincidono"
            else -> null
        }

        if (firstNameError != null || lastNameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            _uiState.value = current.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                formError = "Controlla i campi evidenziati"
            )
            return false
        }

        val result = AuthSessionStore.register(current.firstName, current.lastName, current.email, current.password)
        
        return if (result.isSuccess) {
            true
        } else {
            _uiState.value = current.copy(
                formError = result.exceptionOrNull()?.message ?: "Errore durante la registrazione"
            )
            false
        }
    }
}
