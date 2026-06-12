package com.example.altafedeltium.ui.viewmodel

import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.altafedeltium.data.mock.JobMockData
import com.example.altafedeltium.data.model.ApplicationStatus
import com.example.altafedeltium.data.model.JobApplication
import com.example.altafedeltium.data.model.JobPosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class VideoRecordingState { IDLE, RECORDING, RECORDED }

data class ApplicationUiState(
    val jobPosition: JobPosition? = null,
    val currentStep: Int = 1,

    // Step 1 – Dati personali
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val fullNameError: String? = null,
    val phoneError: String? = null,
    val emailError: String? = null,

    // Step 2 – Caricamento CV
    val cvFileName: String? = null,
    val cvUploaded: Boolean = false,

    // Step 3 – Video Presentazione
    val videoState: VideoRecordingState = VideoRecordingState.IDLE,
    val videoCountdownSeconds: Int = 30,
    val videoOptOut: Boolean = false,
    // new flags for upload confirmation flow
    val videoAwaitingUploadConfirmation: Boolean = false,
    val videoAccepted: Boolean = false,

    // Conferma invio
    val showConfirmDialog: Boolean = false,
    val submitted: Boolean = false
)

class ApplicationViewModel : ViewModel() {

    private val _uiState = mutableStateOf(ApplicationUiState())
    val uiState: State<ApplicationUiState> = _uiState

    init {
        AuthSessionStore.currentUser?.let { user ->
            _uiState.value = _uiState.value.copy(
                fullName = user.fullName,
                email = user.email
            )
        }
    }

    fun setJob(jobId: Int) {
        if (_uiState.value.jobPosition != null) return
        val job = JobMockData.positions.find { it.id == jobId }
        _uiState.value = _uiState.value.copy(jobPosition = job)
    }

    // ──────── Step 1 ────────

    fun onFullNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(
            fullName = value,
            fullNameError = null
        )
    }

    fun onPhoneChanged(value: String) {
        _uiState.value = _uiState.value.copy(phone = value, phoneError = null)
    }

    fun onEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(email = value, emailError = null)
    }

    fun validateStepOne(): Boolean {
        val s = _uiState.value
        val nameError = when {
            s.fullName.isBlank() -> "Inserisci il tuo nome e cognome per procedere"
            s.fullName.trim().length < 3 -> "Il nome deve avere almeno 3 caratteri"
            else -> null
        }
        val phoneError = when {
            s.phone.isBlank() -> "Il numero di telefono è necessario, ti contatteremo lì!"
            s.phone.replace(" ", "").replace("+", "").length < 9 ->
                "Controlla il numero: deve avere almeno 9 cifre"
            else -> null
        }
        val emailError = when {
            s.email.isBlank() -> "Inserisci la tua email per ricevere aggiornamenti"
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() ->
                "Controlla l'email (es. nome@email.it)"
            else -> null
        }
        _uiState.value = s.copy(
            fullNameError = nameError,
            phoneError = phoneError,
            emailError = emailError
        )
        return nameError == null && phoneError == null && emailError == null
    }

    // ──────── Step 2 – CV ────────

    fun onCvSelected(fileName: String) {
        _uiState.value = _uiState.value.copy(cvFileName = fileName, cvUploaded = true)
    }

    fun onCvRemoved() {
        _uiState.value = _uiState.value.copy(cvFileName = null, cvUploaded = false)
    }

    // ──────── Step 3 – Video ────────

    fun onStartRecording() {
        _uiState.value = _uiState.value.copy(
            videoState = VideoRecordingState.RECORDING,
            videoCountdownSeconds = 30
        )
    }

    fun onRecordingTick() {
        val remaining = _uiState.value.videoCountdownSeconds
        if (remaining > 1) {
            _uiState.value = _uiState.value.copy(videoCountdownSeconds = remaining - 1)
        } else {
            _uiState.value = _uiState.value.copy(
                videoState = VideoRecordingState.RECORDED,
                videoCountdownSeconds = 0,
                // when recording finishes, wait for user confirmation to upload
                videoAwaitingUploadConfirmation = true,
                videoAccepted = false
            )
        }
    }

    fun onStopRecordingManually() {
        _uiState.value = _uiState.value.copy(
            videoState = VideoRecordingState.RECORDED,
            videoOptOut = false,
            videoAwaitingUploadConfirmation = true,
            videoAccepted = false
        )
    }

    fun onRetakeVideo() {
        _uiState.value = _uiState.value.copy(
            videoState = VideoRecordingState.IDLE,
            videoCountdownSeconds = 30,
            videoOptOut = false,
            videoAwaitingUploadConfirmation = false,
            videoAccepted = false
        )
    }

    fun onSkipVideo() {
        _uiState.value = _uiState.value.copy(videoOptOut = true, videoAwaitingUploadConfirmation = false)
    }

    // User accepts that the recorded video should be uploaded/attached to the application
    fun onAcceptVideo() {
        _uiState.value = _uiState.value.copy(videoAccepted = true, videoAwaitingUploadConfirmation = false)
    }

    // User rejects uploading the recorded video (opt-out)
    fun onRejectVideo() {
        // Return to IDLE (show the "Registra Video" button) when the user rejects uploading the current video
        _uiState.value = _uiState.value.copy(
            videoState = VideoRecordingState.IDLE,
            videoCountdownSeconds = 30,
            videoOptOut = false,
            videoAwaitingUploadConfirmation = false,
            videoAccepted = false
        )
    }

    // ──────── Navigazione tra Step ────────

    fun onNextStep() {
        when (_uiState.value.currentStep) {
            1 -> {
                if (validateStepOne()) {
                    _uiState.value = _uiState.value.copy(currentStep = 2)
                }
            }
            2 -> _uiState.value = _uiState.value.copy(currentStep = 3)
            3 -> _uiState.value = _uiState.value.copy(showConfirmDialog = true)
        }
    }

    fun onPreviousStep() {
        val step = _uiState.value.currentStep
        if (step > 1) _uiState.value = _uiState.value.copy(currentStep = step - 1)
    }

    // ──────── Invio finale ────────

    fun onConfirmSubmit() {
        val date = SimpleDateFormat("dd MMM yyyy", Locale.ITALIAN).format(Date())
        val s = _uiState.value
        val position = s.jobPosition ?: return
        val application = JobApplication(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            position = position,
            status = ApplicationStatus.INVIATA,
            appliedDate = date,
            applicantName = s.fullName.trim()
        )
        JobMockData.myApplications.add(0, application)
        _uiState.value = s.copy(showConfirmDialog = false, submitted = true)
    }

    fun onDismissConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = false)
    }
}

