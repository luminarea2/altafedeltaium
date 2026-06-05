package com.example.altafedeltium.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.altafedeltium.data.mock.JobMockData
import com.example.altafedeltium.data.model.JobApplication

data class MyApplicationsUiState(
    val applications: List<JobApplication> = emptyList()
)

class MyApplicationsViewModel : ViewModel() {

    private val _uiState = mutableStateOf(MyApplicationsUiState())
    val uiState: State<MyApplicationsUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = MyApplicationsUiState(
            applications = JobMockData.myApplications.toList()
        )
    }
}

