package com.example.projectmanagerapp.repositories

import android.net.Uri
import android.util.Log
import androidx.compose.animation.core.snap
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.Card
import com.example.projectmanagerapp.ui.main.Checklist
import com.example.projectmanagerapp.ui.main.Comment
import com.example.projectmanagerapp.ui.main.PMList
import com.example.projectmanagerapp.ui.main.User
import com.example.projectmanagerapp.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MainFeaturesRepositoryImplement: MainFeaturesRepository {
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
                    Log.d("Firestoreddd", "Snapshot size: ${snapshot.documents.size}")
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

    override suspend fun getBoard(boardId: String): Flow<Board> = callbackFlow {
        val listener = firestore.collection(Constants.BOARD_COLLECTION).document(boardId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val board = snapshot.toObject(Board::class.java)
                trySend(board!!)
            }
        }
        awaitClose {
            listener.remove()
        }
    }

    override suspend fun updateBoard(boardId: String, data: HashMap<String, Any?>) {
        firestore.collection(Constants.BOARD_COLLECTION).document(boardId).update(data).await()
    }

    override suspend fun deleteBoard(boardId: String) {
        firestore.collection(Constants.BOARD_COLLECTION).document(boardId).delete().await()
    }

    override suspend fun getLists(boardId: String): Flow<List<PMList>> = callbackFlow {
        val listener = firestore.collection(Constants.LIST_COLLECTION).whereEqualTo("boardId", boardId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lists = snapshot.documents.mapNotNull { it.toObject(PMList::class.java) }
                    trySend(lists)
                }
            }
        awaitClose { listener.remove() }

    }

    override suspend fun getCard(cardId: String): Flow<Card> = callbackFlow {
        val listener = firestore.collection(Constants.CARD_COLLECTION).document(cardId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val card = snapshot.toObject(Card::class.java)
                trySend(card!!)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getCards(listId: String): Flow<List<Card>> = callbackFlow {
        val listener = firestore.collection(Constants.CARD_COLLECTION).whereEqualTo("listId", listId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val cards = snapshot.documents.mapNotNull { it.toObject(Card::class.java) }
                trySend(cards)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addCard(
        card: Card
    ) {
        firestore.collection(Constants.CARD_COLLECTION).add(card).await()
    }

    override suspend fun updateCard(card: Card) {
        firestore.collection(Constants.CARD_COLLECTION).document(card.id).set(card).await()
    }

    override suspend fun deleteCard(cardId: String) {
        firestore.collection(Constants.CARD_COLLECTION).document(cardId).delete().await()
    }

    override suspend fun createList(list: PMList) {
        firestore.collection(Constants.LIST_COLLECTION).add(list).await()
    }

    override suspend fun updateList(list: PMList) {
        firestore.collection(Constants.LIST_COLLECTION).document(list.id).set(list).await()
    }

    override suspend fun deleteList(listId: String) {
        firestore.collection(Constants.LIST_COLLECTION).document(listId).delete().await()

    }

    override suspend fun moveCard(cardId: String, targetListId: String) {
        firestore.collection(Constants.CARD_COLLECTION).document(cardId).update("listId", targetListId).await()
    }

    override suspend fun getBoardName(boardId: String): Flow<String> = callbackFlow {
        val listener = firestore.collection(Constants.BOARD_COLLECTION).document(boardId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val board = snapshot.toObject(Board::class.java)
                trySend(board!!.name)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getListName(listId: String): Flow<String> = callbackFlow {
        val listener = firestore.collection(Constants.LIST_COLLECTION).document(listId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.toObject(PMList::class.java)
                trySend(list!!.name)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getComments(cardId: String): Flow<List<Comment>> = callbackFlow {
        val listener = firestore.collection(Constants.COMMENT_COLLECTION).whereEqualTo("cardId", cardId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val comments = snapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                trySend(comments)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getCheckLists(cardId: String): Flow<List<Checklist>> = callbackFlow {
        val listener = firestore.collection(Constants.CHECKLIST_COLLECTION).whereEqualTo("cardId", cardId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val checklists = snapshot.documents.mapNotNull { it.toObject(Checklist::class.java) }
                trySend(checklists)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addCheckList(checklist: Checklist) {
        firestore.collection(Constants.CHECKLIST_COLLECTION).add(checklist).await()
    }

    override suspend fun updateCheckList(checklist: Checklist) {
        firestore.collection(Constants.CHECKLIST_COLLECTION).document(checklist.id).set(checklist).await()
    }

    override suspend fun updateCheckListTitle(checklistId: String, newTitle: String) {
        firestore.collection(Constants.CHECKLIST_COLLECTION).document(checklistId).update("title", newTitle).await()
    }

    override suspend fun deleteCheckList(checklistId: String) {
        firestore.collection(Constants.CHECKLIST_COLLECTION).document(checklistId).delete().await()
    }

    override suspend fun addComment(comment: Comment) {
        firestore.collection(Constants.COMMENT_COLLECTION).add(comment).await()
    }

    override suspend fun deleteComment(commentId: String) {
        firestore.collection(Constants.COMMENT_COLLECTION).document(commentId).delete().await()
    }
}