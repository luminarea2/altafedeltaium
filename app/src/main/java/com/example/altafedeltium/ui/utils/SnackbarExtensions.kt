package com.example.altafedeltium.ui.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Shows a snackbar and auto-dismisses it after [durationMs].
 * Uses SnackbarDuration.Indefinite for the actual show and programmatically dismisses
 * the current snackbar after the requested timeout.
 */
suspend fun SnackbarHostState.showTimedSnackbar(
    message: String,
    actionLabel: String? = null,
    durationMs: Long = 1100L
) {
    coroutineScope {
        val job = launch {
            this@showTimedSnackbar.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Indefinite
            )
        }

        // wait requested time then dismiss the shown snackbar
        delay(durationMs)
        this@showTimedSnackbar.currentSnackbarData?.dismiss()

        // ensure the show job finishes
        job.join()
    }
}

