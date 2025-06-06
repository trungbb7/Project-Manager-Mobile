package com.example.projectmanagerapp.ui.main

import androidx.compose.ui.graphics.Color
import java.util.UUID


data class Board(
    val id: String,
    val name: String,
    val backgroundImage: String? = null,
    val backgroundColor: Color = Color.Gray
)

data class TrelloList(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val cards: MutableList<TrelloCard> = mutableListOf()
)

data class BoardDetail(
    val id: String,
    val name: String,
    val backgroundColor: Color = Color(0xFF0079BF), // Màu nền mặc định của Trello
    val lists: MutableList<TrelloList> = mutableListOf()
)
// --- Data classes mới và cập nhật cho Card ---
data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    var isChecked: Boolean = false
)

data class Checklist(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    val items: MutableList<ChecklistItem> = mutableListOf()
)

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val authorId: String, // ID người dùng, tạm thời có thể là một string tên
    val authorName: String, // Tên người dùng
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TrelloCard(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var listId: String, // ID của List chứa card này
    var description: String? = null,
    var dueDate: Long? = null, // Lưu dưới dạng timestamp (milliseconds)
    var checklists: MutableList<Checklist> = mutableListOf(),
    var comments: MutableList<Comment> = mutableListOf()
    // Thêm các thuộc tính khác sau: labels, members, attachments...
)

