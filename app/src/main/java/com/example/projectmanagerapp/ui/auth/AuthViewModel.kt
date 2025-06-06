package com.example.projectmanagerapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _currentUserName = MutableStateFlow(auth.currentUser?.displayName ?: "")
    val currentUserName: StateFlow<String> = _currentUserName

    private val _currentUserEmail = MutableStateFlow(auth.currentUser?.email ?: "")
    val currentUserEmail: StateFlow<String> = _currentUserEmail

    init {
        _isUserLoggedIn.value = auth.currentUser != null
        updateUserInfo()
    }

    private fun updateUserInfo() {
        val user = auth.currentUser
        _currentUserName.value = user?.displayName ?: ""
        _currentUserEmail.value = user?.email ?: ""
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email và mật khẩu không được để trống")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _isUserLoggedIn.value = true
                updateUserInfo()
                _authState.value = AuthState.Success("Đăng nhập thành công")
            } catch (e: FirebaseAuthInvalidUserException) {
                _authState.value = AuthState.Error("Tài khoản không tồn tại")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Error("Email hoặc mật khẩu không chính xác")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Đăng nhập thất bại: ${e.message}")
            }
        }
    }

    fun register(fullName: String, email: String, password: String, confirmPassword: String) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _authState.value = AuthState.Error("Vui lòng điền đầy đủ thông tin")
            return
        }

        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Mật khẩu xác nhận không khớp")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()

                // Cập nhật tên người dùng
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build()

                result.user?.updateProfile(profileUpdates)?.await()

                // Cập nhật thông tin người dùng
                updateUserInfo()

                // Đăng ký thành công nhưng không tự động đăng nhập
                auth.signOut()
                _isUserLoggedIn.value = false
                _authState.value = AuthState.Success("Đăng ký thành công")
            } catch (e: FirebaseAuthWeakPasswordException) {
                _authState.value = AuthState.Error("Mật khẩu quá yếu")
            } catch (e: FirebaseAuthUserCollisionException) {
                _authState.value = AuthState.Error("Email đã được sử dụng")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Đăng ký thất bại: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Vui lòng nhập email")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.Success("Đã gửi email khôi phục mật khẩu")
            } catch (e: FirebaseAuthInvalidUserException) {
                _authState.value = AuthState.Error("Email không tồn tại")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Gửi email khôi phục thất bại: ${e.message}")
            }
        }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                _isUserLoggedIn.value = true
                _authState.value = AuthState.Success("Đăng nhập Google thành công")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Đăng nhập Google thất bại: ${e.message}")
            }
        }
    }

    fun signInWithFacebook(credential: AuthCredential) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential).await()
                _isUserLoggedIn.value = true
                _authState.value = AuthState.Success("Đăng nhập Facebook thành công")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Đăng nhập Facebook thất bại: ${e.message}")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _isUserLoggedIn.value = false
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}