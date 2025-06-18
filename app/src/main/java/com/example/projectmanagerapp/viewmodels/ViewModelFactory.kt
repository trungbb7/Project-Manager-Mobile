package com.example.projectmanagerapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.example.projectmanagerapp.repositories.MainFeaturesRepository

class BoardViewModelFactory(
    private val mainFeaturesRepository: MainFeaturesRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BoardViewModel(mainFeaturesRepository) as T
    }
}

class CreateBoardViewModelFactory(
    private val mainFeaturesRepository: MainFeaturesRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateBoardViewModel(mainFeaturesRepository) as T
    }
}

class EditBoardViewModelFactory(
    private val mainFeaturesRepository: MainFeaturesRepository,
    private val boardId: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditBoardViewModel(mainFeaturesRepository, boardId) as T
    }
}

class BoardDetailViewModelFactory(
    private val mainFeaturesRepository: MainFeaturesRepository,
    private val boardId: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BoardDetailViewModel(mainFeaturesRepository, boardId) as T
    }
}

class AddMemberViewModelFactory(
    private val mainFeaturesRepository: MainFeaturesRepository,
    private val boardId: String
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddMemberViewModel(mainFeaturesRepository, boardId) as T
    }
}



class CardDetailViewModelFactory(
    private val mainFeaturesRepository: MainFeaturesRepository,
    private val boardId: String,
    private val listId: String,
    private val cardId: String,
    private val workManager: WorkManager
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardDetailViewModel(mainFeaturesRepository, boardId, listId, cardId, workManager) as T
    }
}

class HomeViewModelFactory(
    private val mainFeaturesRepository: MainFeaturesRepository,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(mainFeaturesRepository) as T
    }
}
