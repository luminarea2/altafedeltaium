package com.example.altafedeltium.data.model

enum class ApplicationStatus(
    val label: String,
    val description: String,
    val colorKey: String   // "pending" | "review" | "contacted"
) {
    INVIATA(
        label = "Inviata",
        description = "La tua candidatura è stata ricevuta con successo. Tieniti pronto!",
        colorKey = "pending"
    ),
    IN_REVISIONE(
        label = "In Revisione",
        description = "Il tuo profilo è in fase di valutazione. Puoi stare tranquillo.",
        colorKey = "review"
    ),
    CONTATTATO(
        label = "Contattato! 🎉",
        description = "L'azienda vuole conoscerti! Controlla la tua email e il telefono.",
        colorKey = "contacted"
    )
}

data class JobApplication(
    val id: Int,
    val position: JobPosition,
    val status: ApplicationStatus,
    val appliedDate: String,
    val applicantName: String = ""
)

