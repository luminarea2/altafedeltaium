package com.example.altafedeltium.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.altafedeltium.data.mock.JobMockData
import com.example.altafedeltium.data.model.JobCategory
import com.example.altafedeltium.data.model.JobPosition
import com.example.altafedeltium.data.model.JobSortDirection
import com.example.altafedeltium.data.model.JobSortField

data class JobSearchUiState(
    val allPositions: List<JobPosition> = JobMockData.positions,
    val selectedCategories: Set<JobCategory> = setOf(JobCategory.TUTTI),
    val maxDistanceKm: Int = 100,
    val searchQuery: String = "",
    val selectedCity: String = "Tutte le città",
    val selectedContractType: String = "Tutti",
    val selectedSortField: JobSortField = JobSortField.DISTANCE,
    val selectedSortDirection: JobSortDirection = JobSortDirection.ASC
    , val favoriteJobIds: Set<Int> = emptySet()
)

class JobSearchViewModel : ViewModel() {

    private val _uiState = mutableStateOf(JobSearchUiState())
    val uiState: State<JobSearchUiState> = _uiState

    val categories: List<JobCategory> = JobCategory.entries
    val distanceOptions: List<Int> = listOf(10, 25, 50, 100)

    val cities: List<String>
        get() = listOf("Tutte le città") + JobMockData.positions
            .map { it.city }
            .distinct()
            .sorted()

    fun onCategorySelected(category: JobCategory) {
        val current = _uiState.value.selectedCategories.toMutableSet()
        
        if (category == JobCategory.TUTTI) {
            // Se seleziona "TUTTI", deseleziona tutte le altre
            _uiState.value = _uiState.value.copy(selectedCategories = setOf(JobCategory.TUTTI))
        } else {
            // Se era selezionato "TUTTI", rimuovilo
            if (current.contains(JobCategory.TUTTI)) {
                current.remove(JobCategory.TUTTI)
            }
            
            // Toggle della categoria selezionata
            if (current.contains(category)) {
                current.remove(category)
                // Se rimane vuoto, seleziona "TUTTI"
                if (current.isEmpty()) {
                    current.add(JobCategory.TUTTI)
                }
            } else {
                current.add(category)
            }
            _uiState.value = _uiState.value.copy(selectedCategories = current)
        }
    }

    fun onMaxDistanceChanged(km: Int) {
        _uiState.value = _uiState.value.copy(maxDistanceKm = km)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onCitySelected(city: String) {
        _uiState.value = _uiState.value.copy(selectedCity = city)
    }

    fun onContractTypeSelected(contractType: String) {
        _uiState.value = _uiState.value.copy(selectedContractType = contractType)
    }

    fun onSortFieldChanged(field: JobSortField) {
        _uiState.value = _uiState.value.copy(selectedSortField = field)
    }

    fun onSortDirectionChanged(direction: JobSortDirection) {
        _uiState.value = _uiState.value.copy(selectedSortDirection = direction)
    }

    fun isFavorite(jobId: Int): Boolean = _uiState.value.favoriteJobIds.contains(jobId)

    fun toggleFavorite(jobId: Int) {
        val favorites = _uiState.value.favoriteJobIds.toMutableSet()
        if (!favorites.add(jobId)) favorites.remove(jobId)
        _uiState.value = _uiState.value.copy(favoriteJobIds = favorites)
    }

    fun favoriteJobs(): List<JobPosition> {
        val ids = _uiState.value.favoriteJobIds
        return _uiState.value.allPositions.filter { ids.contains(it.id) }
    }

    // ...existing code...

    fun hasActiveFilters(): Boolean {
        val state = _uiState.value
        return state.selectedCategories != setOf(JobCategory.TUTTI) ||
            state.maxDistanceKm != distanceOptions.maxOrNull() ||
            state.searchQuery.isNotBlank() ||
            state.selectedCity != "Tutte le città" ||
            state.selectedSortField != JobSortField.DISTANCE ||
            state.selectedSortDirection != JobSortDirection.ASC
    }

    fun activeFiltersSummary(): List<String> {
        val state = _uiState.value
        val tags = mutableListOf<String>()
        val categorieMostrate = state.selectedCategories.filter { it != JobCategory.TUTTI }
        if (categorieMostrate.isNotEmpty()) tags += categorieMostrate.joinToString(", ") { it.label }
        if (state.selectedCity != "Tutte le città") tags += state.selectedCity
        if (state.maxDistanceKm != distanceOptions.maxOrNull()) tags += "<= ${state.maxDistanceKm} km"
        if (state.searchQuery.isNotBlank()) tags += "Ricerca: ${state.searchQuery}"
        if (state.selectedSortField != JobSortField.DISTANCE) tags += "Ordina: ${state.selectedSortField.label}"
        if (state.selectedSortDirection != JobSortDirection.ASC) tags += state.selectedSortDirection.label
        return tags
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCategories = setOf(JobCategory.TUTTI),
            maxDistanceKm = distanceOptions.maxOrNull() ?: 100,
            searchQuery = "",
            selectedCity = "Tutte le città",
            selectedSortField = JobSortField.DISTANCE,
            selectedSortDirection = JobSortDirection.ASC,
            selectedContractType = "Tutti"
        )
    }

    fun filteredPositions(): List<JobPosition> {
        val state = _uiState.value
        val filtered = state.allPositions.filter { pos ->
            val matchesCategory =
                state.selectedCategories.contains(JobCategory.TUTTI) || state.selectedCategories.contains(pos.category)
            val matchesDistance = pos.distanceKm <= state.maxDistanceKm
            val matchesCity =
                state.selectedCity == "Tutte le città" || pos.city.equals(state.selectedCity, ignoreCase = true)
            val matchesContract = state.selectedContractType == "Tutti" || pos.contractType.equals(state.selectedContractType, ignoreCase = true)
            val matchesQuery = state.searchQuery.isBlank() ||
                    pos.title.contains(state.searchQuery, ignoreCase = true) ||
                    pos.company.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesDistance && matchesCity && matchesContract && matchesQuery
        }

        // Applica ordinamento in base al campo e direzione selezionati
        return filtered.sortedWith { a, b ->
            val comparison = when (state.selectedSortField) {
                JobSortField.DISTANCE -> a.distanceKm.compareTo(b.distanceKm)
                JobSortField.TITLE -> a.title.compareTo(b.title, ignoreCase = true)
                JobSortField.SALARY -> {
                    val aSalary = a.salary?.filter { it.isDigit() }?.toIntOrNull() ?: 0
                    val bSalary = b.salary?.filter { it.isDigit() }?.toIntOrNull() ?: 0
                    aSalary.compareTo(bSalary)
                }
            }
            if (state.selectedSortDirection == JobSortDirection.DESC) -comparison else comparison
        }
    }
}

