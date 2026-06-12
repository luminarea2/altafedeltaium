package com.example.altafedeltium.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.altafedeltium.ui.theme.AccentText

@Composable
 fun ConfirmationDialog(
     title: String,
     message: String,
     confirmLabel: String = "Conferma",
     dismissLabel: String = "Annulla",
     onConfirm: () -> Unit,
     onDismiss: () -> Unit,
     // when true, render the confirm button using the theme error color (destructive action)
     destructiveConfirm: Boolean = false,
     // when true, render the dismiss button with primary filled style (same as "Salva" buttons)
     dismissAsPrimary: Boolean = false
 ) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        },
         confirmButton = {
             if (destructiveConfirm) {
                 Button(
                     onClick = onConfirm,
                     colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                 ) {
                     Text(confirmLabel, color = MaterialTheme.colorScheme.onError)
                 }
             } else {
                 Button(onClick = onConfirm) {
                     Text(confirmLabel)
                 }
             }
         },
         dismissButton = {
             if (dismissAsPrimary) {
                 Button(
                     onClick = onDismiss,
                     colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                 ) {
                     Text(dismissLabel, color = MaterialTheme.colorScheme.onPrimary)
                 }
             } else {
                 OutlinedButton(
                     onClick = onDismiss,
                     colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentText)
                 ) {
                     Text(dismissLabel)
                 }
             }
         }
    )
}

