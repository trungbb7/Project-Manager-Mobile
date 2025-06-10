package com.example.projectmanagerapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import android.util.Log
import com.example.projectmanagerapp.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _currentUserName = MutableStateFlow(auth.currentUser?.displayName ?: "")
    val currentUserName: StateFlow<String> = _currentUserName

    private val _currentUserEmail = MutableStateFlow(auth.currentUser?.email ?: "")
    val currentUserEmail: StateFlow<String> = _currentUserEmail

    // Simplified account collision handling
    private val _pendingLinkCredential = MutableStateFlow<AuthCredential?>(null)
    val pendingLinkCredential: StateFlow<AuthCredential?> = _pendingLinkCredential

    private val _pendingLinkProvider = MutableStateFlow<String?>(null)
    val pendingLinkProvider: StateFlow<String?> = _pendingLinkProvider

    init {
        _isUserLoggedIn.value = auth.currentUser != null
        updateUserInfo()
    }

    private fun updateUserInfo() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            _currentUserName.value = firebaseUser.displayName ?: "User"
            _currentUserEmail.value = firebaseUser.email ?: ""

            // Create or update user in Firestore
            viewModelScope.launch {
                try {
                    val result = userRepository.createOrUpdateUser(firebaseUser)
                    if (result.isSuccess) {
                        Log.d("AuthViewModel", "User info updated in Firestore")
                    } else {
                        Log.e("AuthViewModel", "Failed to update user in Firestore: ${result.exceptionOrNull()}")
                    }
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error updating user info", e)
                }
            }
        } else {
            _currentUserName.value = ""
            _currentUserEmail.value = ""
        }
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

                // Check if there's a pending link credential
                checkAndLinkPendingCredential()

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
        Log.d("AuthViewModel", "signInWithGoogle called")
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Attempting Firebase sign in with Google credential")
                val result = auth.signInWithCredential(credential).await()
                Log.d("AuthViewModel", "Firebase sign in successful: ${result.user?.email}")
                _isUserLoggedIn.value = true
                updateUserInfo()

                // Check if there's a pending link credential
                checkAndLinkPendingCredential()

                _authState.value = AuthState.Success("Đăng nhập Google thành công")
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.d("AuthViewModel", "Account collision detected, attempting to link accounts")
                handleAccountCollisionWithEmail(credential, "Google", e.email)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Firebase sign in failed", e)
                _authState.value = AuthState.Error("Đăng nhập Google thất bại: ${e.message}")
            }
        }
    }

    fun signInWithFacebook(credential: AuthCredential) {
        Log.d("AuthViewModel", "signInWithFacebook called")
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Attempting Firebase sign in with Facebook credential")
                val result = auth.signInWithCredential(credential).await()
                Log.d("AuthViewModel", "Firebase sign in successful: ${result.user?.email}")
                _isUserLoggedIn.value = true
                updateUserInfo()

                // Check if there's a pending link credential
                checkAndLinkPendingCredential()

                _authState.value = AuthState.Success("Đăng nhập Facebook thành công")
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.d("AuthViewModel", "Account collision detected, attempting to link accounts")
                handleAccountCollisionWithEmail(credential, "Facebook", e.email)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Firebase sign in failed", e)
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

    private suspend fun handleAccountCollisionWithEmail(newCredential: AuthCredential, providerName: String, email: String?) {
        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Log.d("AuthViewModel", "User already signed in, linking $providerName account")
                // Link the new credential to the existing account
                currentUser.linkWithCredential(newCredential).await()
                updateUserInfo()
                _authState.value = AuthState.Success("Đã liên kết tài khoản $providerName thành công")
                _isUserLoggedIn.value = true
            } else if (email != null) {
                Log.d("AuthViewModel", "Account collision detected for email: $email")
                // Store pending credential for later linking
                _pendingLinkCredential.value = newCredential
                _pendingLinkProvider.value = providerName

                // Get existing provider info
                val signInMethods = auth.fetchSignInMethodsForEmail(email).await()
                val existingMethods = signInMethods.signInMethods ?: emptyList()
                Log.d("AuthViewModel", "Existing methods: $existingMethods")
                val existingProvider = when {
                    existingMethods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD) -> "Google"
                    existingMethods.contains(FacebookAuthProvider.FACEBOOK_SIGN_IN_METHOD) -> "Facebook"
                    existingMethods.contains("password") -> "Email/Password"
                    else -> "khác"
                }

                // Show simple message
                _authState.value = AuthState.Error(
                    "Email $email đã được đăng ký với $existingProvider.\n\n" +
                    "Vui lòng đăng nhập bằng $existingProvider trước, sau đó tài khoản $providerName sẽ được liên kết tự động."
                )
            } else {
                _authState.value = AuthState.Error("Không thể xác định email từ tài khoản $providerName")
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Failed to handle account collision", e)
            _authState.value = AuthState.Error("Có lỗi xảy ra khi xử lý tài khoản: ${e.message}")
        }
    }

    fun signOut() {
        auth.signOut()
        _isUserLoggedIn.value = false
    }





    private suspend fun checkAndLinkPendingCredential() {
        val pendingCredential = _pendingLinkCredential.value
        val pendingProvider = _pendingLinkProvider.value

        if (pendingCredential != null && pendingProvider != null) {
            try {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    Log.d("AuthViewModel", "Linking pending $pendingProvider credential")
                    currentUser.linkWithCredential(pendingCredential).await()

                    // Clear pending credential
                    _pendingLinkCredential.value = null
                    _pendingLinkProvider.value = null

                    _authState.value = AuthState.Success("Đã liên kết tài khoản $pendingProvider thành công")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to link pending credential", e)
                // Clear pending credential on error
                _pendingLinkCredential.value = null
                _pendingLinkProvider.value = null
            }
        }
    }

    fun clearPendingLink() {
        _pendingLinkCredential.value = null
        _pendingLinkProvider.value = null
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}