package com.example.projectmanagerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.MainFeaturesRepository
import com.example.projectmanagerapp.ui.main.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class HomeViewModelUIState(
    val user: User? = null
)

class HomeViewModel(private val repository: MainFeaturesRepository): ViewModel() {
    private val _uiState = MutableStateFlow(HomeViewModelUIState())
    val uiState: StateFlow<HomeViewModelUIState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            _uiState.value = _uiState.value.copy(user = user)
        }
    }
}