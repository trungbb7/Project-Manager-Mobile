package com.example.projectmanagerapp.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.data.model.User
import com.example.projectmanagerapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    init {
        loadCurrentUser()
    }
    
    fun setPhotoUri(uri: Uri) {
        _photoUri.value = uri
    }

    fun clearPhotoUri() {
        _photoUri.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = userRepository.getCurrentUser()
                if (result.isSuccess) {
                    val loadedUser = result.getOrNull()
                    if (loadedUser != null) {
                        _user.value = loadedUser
                    }
                } else {
                    _error.value = "Không thể tải thông tin người dùng"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun saveProfile(textUpdates: Map<String, Any>, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val allUpdates = textUpdates.toMutableMap()

                // 1. Upload image if a new one is selected
                if (imageUri != null) {
                    val uid = _user.value?.uid ?: throw IllegalStateException("User not logged in")
                    val uploadResult = userRepository.uploadProfileImage(uid, imageUri)
                    if (uploadResult.isSuccess) {
                        allUpdates["photoUrl"] = uploadResult.getOrThrow()
                    } else {
                        throw uploadResult.exceptionOrNull() ?: Exception("Lỗi tải ảnh lên")
                    }
                }

                // 2. Update profile with text changes and new photo URL
                if (allUpdates.isNotEmpty()) {
                    updateProfile(allUpdates)
                }
            } catch (e: Exception) {
                _error.value = "Lỗi lưu hồ sơ: ${e.message}"
            } finally {
                _isLoading.value = false
                clearPhotoUri()
            }
        }
    }

    private fun updateProfile(updates: Map<String, Any>) {
        val currentUser = _user.value
        if (currentUser != null) {
            viewModelScope.launch {
                // No need to set isLoading here as saveProfile handles it
                try {
                    val result = userRepository.updateUserProfile(currentUser.uid, updates)
                    if (result.isSuccess) {
                        // Instead of manually updating the user, just reload from source
                        loadCurrentUser() 
                        _error.value = "Cập nhật thành công!"
                    } else {
                        _error.value = "Không thể cập nhật hồ sơ"
                    }
                } catch (e: Exception) {
                    _error.value = "Lỗi cập nhật: ${e.message}"
                }
            }
        }
    }

    fun refreshUser() {
        loadCurrentUser()
    }
}
