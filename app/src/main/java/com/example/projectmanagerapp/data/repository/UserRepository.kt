package com.example.projectmanagerapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.projectmanagerapp.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) {
    companion object {
        private const val USERS_COLLECTION = "users"
        private const val PROFILE_IMAGES_FOLDER = "profile_images"
        private const val TAG = "UserRepository"
    }

    suspend fun createOrUpdateUser(firebaseUser: FirebaseUser): Result<User> {
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION).document(firebaseUser.uid)
            val existingUser = userDoc.get().await()

            val user = if (existingUser.exists()) {
                val currentUser = existingUser.toObject(User::class.java)!!
                val updatedProviders =
                    updateLinkedProviders(currentUser.linkedProviders, firebaseUser)

                val updates = mapOf(
                    "linkedProviders" to updatedProviders,
                    "isEmailVerified" to firebaseUser.isEmailVerified,
                    "updatedAt" to Timestamp.now(),
                    "lastLoginAt" to Timestamp.now()
                )

                userDoc.update(updates).await()
                currentUser.copy(
                    linkedProviders = updatedProviders,
                    isEmailVerified = firebaseUser.isEmailVerified,
                    updatedAt = updates["updatedAt"] as Timestamp,
                    lastLoginAt = updates["lastLoginAt"] as Timestamp
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

    fun getUserFlow(uid: String): Flow<User?> = callbackFlow {
        val documentReference = firestore.collection(USERS_COLLECTION).document(uid)

        val listener = documentReference.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error listening to user changes", error)
                close(error) // Close the flow with an error
                return@addSnapshotListener
            }
            val user = snapshot?.toObject(User::class.java)
            trySend(user).isSuccess // Offer the user to the flow
        }

        // When the flow is cancelled, remove the listener
        awaitClose {
            Log.d(TAG, "Closing user flow listener")
            listener.remove()
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

    private fun updateLinkedProviders(
        currentProviders: List<String>,
        firebaseUser: FirebaseUser
    ): List<String> {
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

    suspend fun uploadProfileImage(uid: String, imageUri: Uri): Result<String> {
        return try {
            val storageRef = storage.reference.child("$PROFILE_IMAGES_FOLDER/$uid.jpg")
            val downloadUrl = storageRef.putFile(imageUri).await()
                .storage.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading profile image", e)
            Result.failure(e)
        }
    }
}
