package com.example.projectmanagerapp.ui.main

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID


data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null
)

data class Card(
    @DocumentId val id: String = "",
    val listId: String = "",
    val title: String = "",
    val description: String? = null,
    val dueDate: Long? = null,
    val location: CardLocation? = null,
    val assignedMemberIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

@Parcelize
data class CardLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val placeName: String? = null
): Parcelable

data class PMList(
    @DocumentId val id: String = "",
    val boardId: String = "",
    val name: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Board(
    @DocumentId val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val memberIds: List<String> = emptyList(),

    val backgroundImage: String? = null,
    val backgroundColor: String = "#0079BF",
    val createDate: Long = System.currentTimeMillis()
)

// Các model phụ
data class ChecklistItem(
    val id : String = UUID.randomUUID().toString(),
    var text: String = "",
    var isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Checklist(
    @DocumentId val id: String = "",
    val cardId: String = "",
    val title: String = "",
    var items: List<ChecklistItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Comment(
    @DocumentId val id: String = "",
    val cardId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val text: String = "",
    @ServerTimestamp val timestamp: Date? = null
)

