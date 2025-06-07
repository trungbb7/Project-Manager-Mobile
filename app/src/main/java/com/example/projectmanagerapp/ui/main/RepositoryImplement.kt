package com.example.projectmanagerapp.ui.main

import com.example.projectmanagerapp.repositories.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow

class RepositoryImplement: Repository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun getCurrentUser(): User {
        return User(id = "oJ9T4dMvoTatcBbGvqP16AX0JpI2", name = "Minh Trung", email = "trungbb8@gmail.com")
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
                if(snapshot != null) {
                    val users = snapshot.documents.mapNotNull { it.toObject(Board::class.java) }
                    trySend(users)
                }
            }
        awaitClose {listener.remove()}

    }


}