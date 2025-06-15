package com.example.projectmanagerapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.data.model.User
import com.example.projectmanagerapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val result = userRepository.getCurrentUser()
                if (result.isSuccess) {
                    _user.value = result.getOrNull()
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

    fun refreshUser() {
        loadCurrentUser()
    }

    fun updateProfile(updates: Map<String, Any>) {
        val currentUser = _user.value
        if (currentUser != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val result = userRepository.updateUserProfile(currentUser.uid, updates)
                    if (result.isSuccess) {
                        // Reload user data
                        loadCurrentUser()
                    } else {
                        _error.value = "Không thể cập nhật hồ sơ"
                    }
                } catch (e: Exception) {
                    _error.value = "Lỗi cập nhật: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}
