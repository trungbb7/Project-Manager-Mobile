package com.example.projectmanagerapp.ui.main

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val imageUrl: String? = null
)

data class Card(
    @DocumentId val id: String = "",
    val listId: String = "",
    val title: String = "",
    val description: String? = null,
    val dueDate: Long? = null,
    val checkLists: List<Checklist> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val createdDate: Long = System.currentTimeMillis()
)

data class PMList(
    @DocumentId val id: String = "",
    val boardId: String = "",
    val name: String = "",
    val createdDate: Long = System.currentTimeMillis()
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
    val text: String = "",
    val isChecked: Boolean = false)

data class Checklist(
    val title: String = "",
    val items: List<ChecklistItem> = emptyList())

data class Comment(
    val authorId: String = "",
    val authorName: String = "",
    val text: String = "",
    @ServerTimestamp val timestamp: Date? = null
)

