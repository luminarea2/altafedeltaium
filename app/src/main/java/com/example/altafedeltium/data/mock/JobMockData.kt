package com.example.altafedeltium.data.mock

import com.example.altafedeltium.data.model.ApplicationStatus
import com.example.altafedeltium.data.model.JobApplication
import com.example.altafedeltium.data.model.JobCategory
import com.example.altafedeltium.data.model.JobPosition

object JobMockData {

    val positions = listOf(
        JobPosition(
            id = 1,
            title = "Magazziniere",
            company = "Coop Adriatica",
            city = "Vasto",
            distanceKm = 3,
            contractType = "Full-time",
            category = JobCategory.MAGAZZINO,
            description = "Gestione ricevimento merci, stoccaggio scaffali e inventario settimanale. Esperienza con transpallet richiesta. Turni diurni, dal lunedì al sabato.",
            requiredExperience = "1+ anni",
            salary = "1.200 – 1.400 €/mese",
            isUrgent = true
        ),
        JobPosition(
            id = 2,
            title = "Addetto Vendite",
            company = "Esselunga",
            city = "Lanciano",
            distanceKm = 28,
            contractType = "Part-time",
            category = JobCategory.VENDITE,
            description = "Assistenza alla clientela, rifornimento scaffali, gestione ordini reparto freschi. Disponibilità nei weekend preferibile.",
            requiredExperience = "Anche prima esperienza",
            salary = "800 – 1.000 €/mese"
        ),
        JobPosition(
            id = 3,
            title = "Operatore Logistico",
            company = "GLS Group",
            city = "Pescara",
            distanceKm = 68,
            contractType = "Full-time",
            category = JobCategory.LOGISTICA,
            description = "Gestione spedizioni in entrata e uscita, coordinamento corrieri, utilizzo gestionale SAP. Patente B richiesta.",
            requiredExperience = "2+ anni logistica",
            salary = "1.400 – 1.700 €/mese"
        ),
        JobPosition(
            id = 4,
            title = "Cassiere/a",
            company = "Lidl Italia",
            city = "Vasto",
            distanceKm = 5,
            contractType = "Part-time",
            category = JobCategory.CASSA,
            description = "Gestione cassa, assistenza clienti, controllo scadenze prodotti e riordino reparto. Formazione interna fornita.",
            requiredExperience = "Anche prima esperienza",
            salary = "750 – 950 €/mese"
        ),
        JobPosition(
            id = 5,
            title = "Responsabile Reparto Surgelati",
            company = "Conad",
            city = "San Salvo",
            distanceKm = 12,
            contractType = "Full-time",
            category = JobCategory.REPARTO,
            description = "Coordinamento team di 3 persone, gestione ordini, controllo scadenze e reso fornitori. Si valutano candidati interni al settore GDO.",
            requiredExperience = "3+ anni GDO",
            salary = "1.500 – 1.800 €/mese",
            isUrgent = true
        ),
        JobPosition(
            id = 6,
            title = "Addetto Magazzino Notte",
            company = "Carrefour",
            city = "Ortona",
            distanceKm = 42,
            contractType = "Full-time",
            category = JobCategory.MAGAZZINO,
            description = "Turno notturno (22:00 – 06:00). Rifornimento scaffali, gestione carico/scarico. Indennità turno notturno inclusa.",
            requiredExperience = "1+ anni",
            salary = "1.300 – 1.500 €/mese"
        ),
        JobPosition(
            id = 7,
            title = "Supervisore Logistica",
            company = "Amazon Logistica",
            city = "Manoppello",
            distanceKm = 77,
            contractType = "Full-time",
            category = JobCategory.SUPERVISIONE,
            description = "Supervisione turni, coordinamento team >10 persone, KPI di performance, reportistica quotidiana. Ottima opportunità di crescita.",
            requiredExperience = "5+ anni",
            salary = "2.000 – 2.500 €/mese"
        ),
        JobPosition(
            id = 8,
            title = "Addetto Vendite Ortofrutta",
            company = "Coop Adriatica",
            city = "Vasto",
            distanceKm = 4,
            contractType = "Part-time",
            category = JobCategory.VENDITE,
            description = "Gestione reparto ortofrutta: allestimento, rotazione merce, controllo qualità e relazione clienti.",
            requiredExperience = "Anche prima esperienza",
            salary = "800 – 1.000 €/mese"
        )
    )

    val myApplications: MutableList<JobApplication> = mutableListOf(
        JobApplication(
            id = 1,
            position = positions[2],
            status = ApplicationStatus.CONTATTATO,
            appliedDate = "08 Mar 2026",
            applicantName = "Paolo Cortellesi"
        ),
        JobApplication(
            id = 2,
            position = positions[4],
            status = ApplicationStatus.IN_REVISIONE,
            appliedDate = "11 Mar 2026",
            applicantName = "Paolo Cortellesi"
        )
    )
}

