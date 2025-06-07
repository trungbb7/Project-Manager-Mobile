package com.example.projectmanagerapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BoardUIState(
    val boards: List<Board> = emptyList(),
    var idLoading: Boolean = false,
    var error: String? = null
)

class BoardViewModel(
    val repository: Repository
): ViewModel() {


    private val _boardUIState = MutableStateFlow(BoardUIState())
    val boardUIState = _boardUIState.asStateFlow()

    init {
        getBoards()
    }

    private fun getBoards() {
        viewModelScope.launch {
            repository.getBoards().collect { boards ->
                _boardUIState.value = BoardUIState(
                    boards = boards
                )
            }
        }
    }
}