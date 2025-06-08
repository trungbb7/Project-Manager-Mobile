package com.example.projectmanagerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projectmanagerapp.repositories.Repository

class BoardViewModelFactory(
    private val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BoardViewModel(repository) as T
    }
}

class CreateBoardViewModelFactory(
    private val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateBoardViewModel(repository) as T
    }
}

class EditBoardViewModelFactory(
    private val repository: Repository,
    private val boardId: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditBoardViewModel(repository, boardId) as T
    }
}

