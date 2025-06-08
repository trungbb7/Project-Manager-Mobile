package com.example.projectmanagerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.Repository
import com.example.projectmanagerapp.ui.main.Board
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
        _boardUIState.value = _boardUIState.value.copy(idLoading = true)
        viewModelScope.launch {
            try {
                repository.getBoards().collect { boards ->
                    _boardUIState.value = BoardUIState(
                        boards = boards,
                        idLoading = false
                    )
                }
            }catch (e: Exception) {
                _boardUIState.value = BoardUIState(
                    error = e.message
                )
            }
        }
    }

    fun editBoardName(board: Board, newName: String) {
        _boardUIState.value = _boardUIState.value.copy(idLoading = true)
        viewModelScope.launch {
            try {
                repository.editBoardName(board, newName)
            } catch (e: Exception) {
                _boardUIState.value = BoardUIState(
                    error = e.message
                )
            }
        }
        _boardUIState.value = _boardUIState.value.copy(idLoading = false)
    }

    fun deleteBoard(boardId: String) {
        _boardUIState.value = _boardUIState.value.copy(idLoading = true)

        viewModelScope.launch {
            try {
                repository.deleteBoard(boardId)
                _boardUIState.value = _boardUIState.value.copy(idLoading = false)
            } catch (e: Exception) {
                _boardUIState.value = _boardUIState.value.copy(error = e.message, idLoading = false)
            }
        }
    }

    fun clearError() {
        _boardUIState.value  = _boardUIState.value.copy(error = "")
    }
}