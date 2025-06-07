package com.example.projectmanagerapp.ui.main

import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import java.util.UUID


//data class Board(
//    val id: String,
//    val name: String,
//    val backgroundImage: String? = null,
//    val backgroundColor: Color = Color.Gray
//)

//data class TrelloList(
//    val id: String = UUID.randomUUID().toString(),
//    var name: String,
//    val cards: MutableList<TrelloCard> = mutableListOf()
//)
//
//data class BoardDetail(
//    val id: String,
//    val name: String,
//    val backgroundColor: Color = Color(0xFF0079BF), // Màu nền mặc định
//    val lists: MutableList<TrelloList> = mutableListOf()
//)
//// --- Data classes mới và cập nhật cho Card ---
//data class ChecklistItem(
//    val id: String = UUID.randomUUID().toString(),
//    var text: String,
//    var isChecked: Boolean = false
//)
//
//data class Checklist(
//    val id: String = UUID.randomUUID().toString(),
//    var title: String,
//    val items: MutableList<ChecklistItem> = mutableListOf()
//)
//
//data class Comment(
//    val id: String = UUID.randomUUID().toString(),
//    val authorId: String, // ID người dùng, tạm thời có thể là một string tên
//    val authorName: String, // Tên người dùng
//    val text: String,
//    val timestamp: Long = System.currentTimeMillis()
//)
//
//data class TrelloCard(
//    val id: String = UUID.randomUUID().toString(),
//    var title: String,
//    var listId: String, // ID của List chứa card này
//    var description: String? = null,
//    var dueDate: Long? = null, // Lưu dưới dạng timestamp (milliseconds)
//    var checklists: MutableList<Checklist> = mutableListOf(),
//    var comments: MutableList<Comment> = mutableListOf()
//    // Thêm các thuộc tính khác sau: labels, members, attachments...
//)

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val imageUrl: String? = null
)

data class Card(
    @DocumentId val id: String = "",
    val title: String = "",
    val listId: String = "",
    val description: String? = null,
    val dueDate: Long? = null,
    val checklists: List<Checklist> = emptyList(),
    val comments: List<Comment> = emptyList()
)

data class PMList(
    @DocumentId val id: String = "",
    val name: String = "",

)

data class Board(
    @DocumentId val id: String = "",
    val name: String = "",
    val ownerId: String = "",
    val memberIds: List<String> = emptyList(),

    val backgroundImage: String? = null,
    val backgroundColor: String = "#0079BF"
)

// Các model phụ
data class ChecklistItem(
    val text: String = "",
    val isChecked: Boolean = false)

data class Checklist(
    @DocumentId val id: String = "",
    val title: String = "",
    val items: List<ChecklistItem> = emptyList())

data class Comment(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val text: String = "",
    @ServerTimestamp val timestamp: Date? = null
)