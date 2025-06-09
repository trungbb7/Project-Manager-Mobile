package com.example.projectmanagerapp.data.repository

import android.util.Log
import com.example.projectmanagerapp.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val TAG = "UserRepository"
    }

    suspend fun createOrUpdateUser(firebaseUser: FirebaseUser): Result<User> {
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION).document(firebaseUser.uid)
            val existingUser = userDoc.get().await()

            val user = if (existingUser.exists()) {
                val currentUser = existingUser.toObject(User::class.java)!!
                val updatedProviders = updateLinkedProviders(currentUser.linkedProviders, firebaseUser)
                
                val updates = mapOf(
                    "email" to (firebaseUser.email ?: currentUser.email),
                    "displayName" to (firebaseUser.displayName ?: currentUser.displayName),
                    "photoUrl" to (firebaseUser.photoUrl?.toString() ?: currentUser.photoUrl),
                    "phoneNumber" to (firebaseUser.phoneNumber ?: currentUser.phoneNumber),
                    "linkedProviders" to updatedProviders,
                    "isEmailVerified" to firebaseUser.isEmailVerified,
                    "updatedAt" to Timestamp.now(),
                    "lastLoginAt" to Timestamp.now()
                )
                
                userDoc.set(updates, SetOptions.merge()).await()
                currentUser.copy(
                    email = firebaseUser.email ?: currentUser.email,
                    displayName = firebaseUser.displayName ?: currentUser.displayName,
                    photoUrl = firebaseUser.photoUrl?.toString() ?: currentUser.photoUrl,
                    phoneNumber = firebaseUser.phoneNumber ?: currentUser.phoneNumber,
                    linkedProviders = updatedProviders,
                    isEmailVerified = firebaseUser.isEmailVerified,
                    lastLoginAt = Timestamp.now()
                )
            } else {
                val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    linkedProviders = getProvidersFromFirebaseUser(firebaseUser),
                    isEmailVerified = firebaseUser.isEmailVerified,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now(),
                    lastLoginAt = Timestamp.now()
                )
                
                userDoc.set(newUser).await()
                newUser
            }

            Log.d(TAG, "User created/updated successfully: ${user.email}")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating/updating user", e)
            Result.failure(e)
        }
    }

    suspend fun getUserByUid(uid: String): Result<User?> {
        return try {
            val document = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by UID", e)
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<User?> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            getUserByUid(currentUser.uid)
        } else {
            Result.success(null)
        }
    }

    suspend fun updateUserProfile(uid: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["updatedAt"] = Timestamp.now()
            
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(updatesWithTimestamp)
                .await()
            
            Log.d(TAG, "User profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            Result.failure(e)
        }
    }

    fun getUserFlow(uid: String): Flow<User?> = flow {
        try {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error listening to user changes", error)
                        return@addSnapshotListener
                    }
                    
                    val user = snapshot?.toObject(User::class.java)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up user flow", e)
        }
    }

    private fun getProvidersFromFirebaseUser(firebaseUser: FirebaseUser): List<String> {
        return firebaseUser.providerData.map { userInfo ->
            when (userInfo.providerId) {
                "google.com" -> "google"
                "facebook.com" -> "facebook"
                "password" -> "password"
                else -> userInfo.providerId
            }
        }.distinct()
    }

    private fun updateLinkedProviders(currentProviders: List<String>, firebaseUser: FirebaseUser): List<String> {
        val newProviders = getProvidersFromFirebaseUser(firebaseUser)
        return (currentProviders + newProviders).distinct()
    }

    suspend fun deleteUser(uid: String): Result<Unit> {
        return try {
            firestore.collection(USERS_COLLECTION).document(uid).delete().await()
            Log.d(TAG, "User deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user", e)
            Result.failure(e)
        }
    }
}
