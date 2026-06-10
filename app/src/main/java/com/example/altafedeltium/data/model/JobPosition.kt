package com.example.altafedeltium.data.model

enum class JobCategory(val label: String) {
    TUTTI("Tutti i ruoli"),
    MAGAZZINO("Magazzino"),
    VENDITE("Addetto Vendite"),
    LOGISTICA("Logistica"),
    CASSA("Cassa"),
    REPARTO("Reparto"),
    SUPERVISIONE("Supervisione")
}

enum class JobSortField(val label: String) {
    DISTANCE("Distanza"),
    TITLE("Titolo"),
    SALARY("Stipendio")
}

enum class JobSortDirection(val label: String) {
    ASC("Crescente"),
    DESC("Decrescente")
}

data class JobPosition(
    val id: Int,
    val title: String,
    val company: String,
    val city: String,
    val distanceKm: Int,
    val contractType: String,
    val category: JobCategory,
    val description: String,
    val requiredExperience: String,
    val salary: String? = null,
    val isUrgent: Boolean = false
)

