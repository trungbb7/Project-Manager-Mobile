package com.example.projectmanagerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.MainFeaturesRepository
import com.example.projectmanagerapp.ui.main.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddMemberViewModelUIState(
    val searchQuery: String = "",
    val searchResults: List<User> = emptyList(),
    val isLoading: Boolean = false
)


class AddMemberViewModel(
    private val repository: MainFeaturesRepository,
    private val boardId: String
) : ViewModel() {
    //    val searchQuery = MutableStateFlow("")
//    val searchResults = MutableStateFlow<List<User>>(emptyList())
//    val isLoading = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(AddMemberViewModelUIState())
    val uiState: StateFlow<AddMemberViewModelUIState> = _uiState.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.contains("@")) { // Chỉ tìm khi nhập đủ email
            searchUsers()
        }
    }

    private fun searchUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val results = repository.searchUsersByEmail(_uiState.value.searchQuery)
            _uiState.value = _uiState.value.copy(searchResults = results, isLoading = false)
//            isLoading.value = true
//            searchResults.value = repository.searchUsersByEmail(searchQuery.value)
//            isLoading.value = false
        }
    }

    fun addMemberToBoard(userId: String) {
        viewModelScope.launch {
            repository.addMemberToBoard(boardId, userId)
        }
    }
}