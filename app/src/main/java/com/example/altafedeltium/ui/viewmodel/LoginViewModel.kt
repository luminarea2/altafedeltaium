package com.example.altafedeltium.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = mutableStateOf(LoginUiState())
    val uiState: State<LoginUiState> = _uiState

    fun onEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null, formError = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, passwordError = null, formError = null)
    }

    fun submit(): Boolean {
        val current = _uiState.value
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)
        val authUser = if (emailError == null && passwordError == null) {
            AuthSessionStore.login(current.email, current.password)
        } else {
            null
        }
        val authError = if (emailError == null && passwordError == null && authUser == null) {
            "Email o password non corretti"
        } else {
            null
        }

        _uiState.value = current.copy(
            emailError = emailError,
            passwordError = passwordError,
            formError = when {
                emailError != null || passwordError != null -> {
                "Controlla i campi evidenziati"
                }

                authError != null -> authError
                else -> null
            }
        )

        return emailError == null && passwordError == null && authError == null
    }
}
