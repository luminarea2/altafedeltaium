package com.example.altafedeltium.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.altafedeltium.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Crea account", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Compila i campi per registrarti", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = uiState.firstName,
            onValueChange = viewModel::onFirstNameChanged,
            label = { Text("Nome") },
            isError = uiState.firstNameError != null,
            supportingText = {
                uiState.firstNameError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.lastName,
            onValueChange = viewModel::onLastNameChanged,
            label = { Text("Cognome") },
            isError = uiState.lastNameError != null,
            supportingText = {
                uiState.lastNameError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChanged,
            label = { Text("Email") },
            isError = uiState.emailError != null,
            supportingText = {
                uiState.emailError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChanged,
            label = { Text("Password") },
            isError = uiState.passwordError != null,
            supportingText = {
                uiState.passwordError?.let { Text(it) }
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChanged,
            label = { Text("Conferma Password") },
            isError = uiState.confirmPasswordError != null,
            supportingText = {
                uiState.confirmPasswordError?.let { Text(it) }
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        uiState.formError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (viewModel.submit()) {
                    onRegisterSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrati")
        }

        TextButton(
            onClick = onGoToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hai gia un account? Torna al login")
        }
    }
}
