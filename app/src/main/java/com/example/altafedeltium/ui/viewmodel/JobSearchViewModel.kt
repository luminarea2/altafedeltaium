package com.example.altafedeltium.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.altafedeltium.data.mock.JobMockData
import com.example.altafedeltium.data.model.JobCategory
import com.example.altafedeltium.data.model.JobPosition

data class JobSearchUiState(
    val allPositions: List<JobPosition> = JobMockData.positions,
    val selectedCategory: JobCategory = JobCategory.TUTTI,
    val maxDistanceKm: Int = 100,
    val searchQuery: String = "",
    val selectedCity: String = "Tutte le città"
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
        _uiState.value = _uiState.value.copy(selectedCategory = category)
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

    fun hasActiveFilters(): Boolean {
        val state = _uiState.value
        return state.selectedCategory != JobCategory.TUTTI ||
            state.maxDistanceKm != distanceOptions.maxOrNull() ||
            state.searchQuery.isNotBlank() ||
            state.selectedCity != "Tutte le città"
    }

    fun activeFiltersSummary(): List<String> {
        val state = _uiState.value
        val tags = mutableListOf<String>()
        if (state.selectedCategory != JobCategory.TUTTI) tags += state.selectedCategory.label
        if (state.selectedCity != "Tutte le città") tags += state.selectedCity
        if (state.maxDistanceKm != distanceOptions.maxOrNull()) tags += "<= ${state.maxDistanceKm} km"
        if (state.searchQuery.isNotBlank()) tags += "Ricerca: ${state.searchQuery}"
        return tags
    }

    fun resetFilters() {
        _uiState.value = _uiState.value.copy(
            selectedCategory = JobCategory.TUTTI,
            maxDistanceKm = distanceOptions.maxOrNull() ?: 100,
            searchQuery = "",
            selectedCity = "Tutte le città"
        )
    }

    fun filteredPositions(): List<JobPosition> {
        val state = _uiState.value
        return state.allPositions.filter { pos ->
            val matchesCategory =
                state.selectedCategory == JobCategory.TUTTI || pos.category == state.selectedCategory
            val matchesDistance = pos.distanceKm <= state.maxDistanceKm
            val matchesCity =
                state.selectedCity == "Tutte le città" || pos.city.equals(state.selectedCity, ignoreCase = true)
            val matchesQuery = state.searchQuery.isBlank() ||
                    pos.title.contains(state.searchQuery, ignoreCase = true) ||
                    pos.company.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesDistance && matchesCity && matchesQuery
        }.sortedWith(compareByDescending<JobPosition> { it.isUrgent }.thenBy { it.distanceKm })
    }
}

