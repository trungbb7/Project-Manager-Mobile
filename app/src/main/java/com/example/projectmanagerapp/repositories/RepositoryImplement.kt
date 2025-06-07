package com.example.projectmanagerapp.repositories

import android.net.Uri
import androidx.compose.ui.unit.Constraints
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.User
import com.example.projectmanagerapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import java.util.UUID

class RepositoryImplement: Repository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun getCurrentUser(): User {
        return User(
            id = "oJ9T4dMvoTatcBbGvqP16AX0JpI2",
            name = "Minh Trung",
            email = "trungbb8@gmail.com"
        )
    }

    override fun getBoards(): Flow<List<Board>> = callbackFlow {
        val user = getCurrentUser()
        val userId = user.id
        val listener = firestore.collection("boards").whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val users = snapshot.documents.mapNotNull { it.toObject(Board::class.java) }
                    trySend(users)
                }
            }
        awaitClose { listener.remove() }

    }

    override suspend fun editBoardName(
        board: Board,
        newName: String
    ) {
        firestore.collection("boards").document(board.id).update("name", newName)
    }

    override suspend fun uploadBoardBackgroundImage(imageUri: Uri): String {
        val storageRef = storage.reference
        val imageRef = storageRef.child("${Constants.BOARD_BACKGROUND_REF}/${UUID.randomUUID()}_${imageUri.lastPathSegment}")
        imageRef.putFile(imageUri).await()
        return imageRef.downloadUrl.await().toString()
    }

    override suspend fun createBoard(board: Board) {
        firestore.collection(Constants.BOARD_COLLECTION).add(board).await()
    }


}