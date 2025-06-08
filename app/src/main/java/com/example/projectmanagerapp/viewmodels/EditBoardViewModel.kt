package com.example.projectmanagerapp.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.Repository
import com.example.projectmanagerapp.utils.BackgroundType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditBoardViewModelState(

    val boardName: String = "",
    val prevBoardName: String = "",
    val selectedBackgroundColor: String = "#0079BF",
    val selectedImageUri: Uri? = null,
    val prevBackgroundImage: String? = null,
    val backGroundType: BackgroundType = BackgroundType.COLOR,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)


class EditBoardViewModel(private val repository: Repository, private val boardId: String) :
    ViewModel() {

    private val _uiState = MutableStateFlow(EditBoardViewModelState())
    val uiState: StateFlow<EditBoardViewModelState> = _uiState.asStateFlow()

    init {
        try {
            _uiState.value = _uiState.value.copy(isLoading = true)


            viewModelScope.launch {
                val board = repository.getBoard(boardId)
                if (board != null) {
                    _uiState.value = _uiState.value.copy(
                        boardName = board.name,
                        prevBoardName = board.name,
                        selectedBackgroundColor = board.backgroundColor,
                        prevBackgroundImage = board.backgroundImage,
                        backGroundType = if(board.backgroundImage != null) BackgroundType.IMAGE else BackgroundType.COLOR,
                        isLoading = false,
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Không thể tải thông tin bảng", isLoading = false
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
        }
    }

    fun onBackgroundTypeChange(type: BackgroundType) {
        _uiState.value = _uiState.value.copy(backGroundType = type)
    }

    fun onBoardNameChange(name: String) {
        _uiState.value = _uiState.value.copy(boardName = name)
    }

    fun onBackgroundColorChange(color: String) {
        _uiState.value = _uiState.value.copy(selectedBackgroundColor = color)
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun updateBoard() {
        if (_uiState.value.boardName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Tên bảng không được để trống")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            viewModelScope.launch {
                val imageUrl: String? = _uiState.value.selectedImageUri?.let {
                    repository.uploadBoardBackgroundImage(it)
                }

                var data = hashMapOf<String, Any?>()

                if (_uiState.value.backGroundType == BackgroundType.COLOR) {
                    data = hashMapOf<String, Any?>(
                        "name" to _uiState.value.boardName,
                        "backgroundColor" to _uiState.value.selectedBackgroundColor,
                        "backgroundImage" to null
                    )
                } else {
                    if(imageUrl != null) {
                        data = hashMapOf<String, Any?>(
                            "name" to _uiState.value.boardName,
                            "backgroundColor" to _uiState.value.selectedBackgroundColor,
                            "backgroundImage" to imageUrl
                        )
                    }
                    else if(_uiState.value.selectedImageUri != null) {
                        _uiState.value = _uiState.value.copy(error = "Không thể tải ảnh nền bảng", isLoading = false)
                        return@launch
                    }
                    else if(_uiState.value.selectedImageUri == null && _uiState.value.prevBackgroundImage == null){
                        _uiState.value = _uiState.value.copy(error = "Vui lòng chọn ảnh nền bảng", isLoading = false)
                        return@launch
                    }
                    else if(_uiState.value.selectedImageUri == null && _uiState.value.prevBackgroundImage != null){
                        data = hashMapOf<String, Any?>(
                            "name" to _uiState.value.boardName,
                            "backgroundColor" to _uiState.value.selectedBackgroundColor,
                            "backgroundImage" to _uiState.value.prevBackgroundImage
                        )
                    }
                }

                repository.updateBoard(boardId, data)
                _uiState.value = _uiState.value.copy(
                    success = true, isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
        }

    }


}