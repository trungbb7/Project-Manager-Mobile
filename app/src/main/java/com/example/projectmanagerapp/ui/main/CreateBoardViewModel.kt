package com.example.projectmanagerapp.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class CreateBoardState(
    val boardName: String = "",
    val memberIds: List<String> = emptyList(),
    val ownerId: String = "",
    val selectedImageUri: Uri? = null,
    val selectedBackgroundColor: String = "#0079BF",
    val error: String? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false


)

class CreateBoardViewModel(
    private val repository: Repository
): ViewModel() {

    private val _uiState = MutableStateFlow(CreateBoardState())
    val uiState: StateFlow<CreateBoardState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun onBackgroundColorChange(color: String) {
        _uiState.value = _uiState.value.copy(selectedBackgroundColor = color)
    }

    fun onBoardNameChange(name: String) {
        _uiState.value = _uiState.value.copy(boardName = name)
    }

    fun createBoard() {
        if(_uiState.value.boardName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Tên bảng không được để trống")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            try{
                val imageUrl: String? =  _uiState.value.selectedImageUri?.let {
                    repository.uploadBoardBackgroundImage(it)
                }

                val currentUserId = repository.getCurrentUser().id
                val board = Board(
                    name = _uiState.value.boardName,
                    ownerId = currentUserId,
                    memberIds = listOf(currentUserId),
                    backgroundImage = imageUrl,
                    backgroundColor = _uiState.value.selectedBackgroundColor
                )
                repository.createBoard(board)
                _uiState.value = _uiState.value.copy(success = true, isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false, success = false)

            }
        }


    }

}