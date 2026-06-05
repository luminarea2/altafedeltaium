package com.example.altafedeltium.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val formError: String? = null
)

class RegisterViewModel : ViewModel() {
    private val _uiState = mutableStateOf(RegisterUiState())
    val uiState: State<RegisterUiState> = _uiState

    fun onNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(name = value, nameError = null, formError = null)
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
        val nameError = if (current.name.isBlank()) "Inserisci il nome" else null
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)
        val confirmPasswordError = when {
            current.confirmPassword.isBlank() -> "Conferma la password"
            current.confirmPassword != current.password -> "Le password non coincidono"
            else -> null
        }

        _uiState.value = current.copy(
            nameError = nameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            formError = if (nameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
                "Controlla i campi evidenziati"
            } else {
                null
            }
        )

        return nameError == null && emailError == null && passwordError == null && confirmPasswordError == null
    }
}
