package com.example.projectmanagerapp.repositories

import android.net.Uri
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.Card
import com.example.projectmanagerapp.ui.main.Checklist
import com.example.projectmanagerapp.ui.main.Comment
import com.example.projectmanagerapp.ui.main.User
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getCurrentUser(): User
    fun getBoards(): Flow<List<Board>>
    suspend fun editBoardName(board: Board, newName: String)

    suspend fun uploadBoardBackgroundImage(imageUri: Uri): String

    suspend fun createBoard(board: Board)

    suspend fun getBoard(boardId: String): Board?
    suspend fun updateBoard(boardId: String, data: HashMap<String, Any?>)
    suspend fun deleteBoard(boardId: String)

//    fun getCard(boardId: String, listId: String, cardId: String): Flow<Card>
//    fun getCheckList(boardId: String, listId: String, cardId: String): Flow<List<Checklist>>
//    fun getComments(boardId: String, listId: String, cardId: String): Flow<List<Comment>>
}