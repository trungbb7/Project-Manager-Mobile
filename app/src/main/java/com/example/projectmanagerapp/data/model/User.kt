package com.example.projectmanagerapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class User(
    @DocumentId
    val id: String = "",
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
    val bio: String = "",
    val department: String = "",
    val position: String = "",
    val location: String = "",
    val skills: List<String> = emptyList(),
    val linkedProviders: List<String> = emptyList(), // ["google", "facebook", "password"]
    val isEmailVerified: Boolean = false,
    val isActive: Boolean = true,
    val role: String = "user", // "user", "admin", "manager"
    val preferences: UserPreferences = UserPreferences(),
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null,
    val lastLoginAt: Timestamp? = null
)

data class UserPreferences(
    val theme: String = "system", // "light", "dark", "system"
    val language: String = "vi", // "vi", "en"
    val notifications: NotificationSettings = NotificationSettings(),
    val privacy: PrivacySettings = PrivacySettings()
)

data class NotificationSettings(
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val projectUpdates: Boolean = true,
    val taskAssignments: Boolean = true,
    val deadlineReminders: Boolean = true,
    val teamInvitations: Boolean = true
)

data class PrivacySettings(
    val profileVisibility: String = "team", // "public", "team", "private"
    val showEmail: Boolean = false,
    val showPhoneNumber: Boolean = false,
    val showLocation: Boolean = true,
    val allowDirectMessages: Boolean = true
)
