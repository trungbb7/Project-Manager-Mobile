package com.example.projectmanagerapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.MainFeaturesRepository
import com.example.projectmanagerapp.ui.main.Card
import com.example.projectmanagerapp.ui.main.Checklist
import com.example.projectmanagerapp.ui.main.ChecklistItem
import com.example.projectmanagerapp.ui.main.Comment
import com.example.projectmanagerapp.ui.main.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class NavigationEvent {
    object NavigationBack: NavigationEvent()
}

data class CardDetailUIState(
    val boardName: String = "",
    val listName: String = "",
    val card: Card? = null,
    val comments: List<Comment> = emptyList(),
    val checklists: List<Checklist> = emptyList(),
    val boardMembers: List<User> = emptyList(),
    val assignedMembers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CardDetailViewModel(
    private val repository: MainFeaturesRepository,
    private val boardId: String,
    private val listId: String,
    private val cardId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(CardDetailUIState())
    val uiState: StateFlow<CardDetailUIState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val boardNameFlow = repository.getBoardName(boardId)
                val listNameFlow = repository.getListName(listId)
                val cardFlow = repository.getCard(cardId)
                val commentsFlow = repository.getComments(cardId)
                val checklistsFlow = repository.getCheckLists(cardId)

                val board = repository.getBoardOnce(boardId)
                val boardMembers = repository.getMemberProfiles(board?.memberIds ?: emptyList())


                combine(boardNameFlow, listNameFlow, cardFlow, commentsFlow, checklistsFlow) { boardName, listName, card, comments, checklists ->
                    val assignedMember: List<User> = boardMembers.filter { member ->
                        card.assignedMemberIds.contains(member.uid)
                    }

                    _uiState.value.copy(
                        boardName = boardName,
                        listName = listName,
                        card = card,
                        comments = comments,
                        checklists = checklists,
                        boardMembers = boardMembers,
                        assignedMembers = assignedMember,
                        isLoading = false
                    )
                }.catch { e ->
                    Log.e("CardDetailViewModel", "Error: ${e.message}")
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }.collect { newState ->
                    _uiState.value = newState
                }

            } catch (e: Exception) {
                Log.e("CardDetailViewModel", "Error fetching data", e)
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun assignMember(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.assignMemberToCard(boardId, listId, cardId, userId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun unAssignMember(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.unassignMemberFromCard(boardId, listId, cardId, userId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun updateCardTitle(newTitle: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try{
                val card = _uiState.value.card?.copy(title = newTitle)
                if (card != null) {
                    repository.updateCard(card)
                }else {
                    _uiState.value = _uiState.value.copy(error = "Card is null", isLoading = false)
                }
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)

            }
        }
    }

    fun updateCardDescription(newDescription: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val card = _uiState.value.card?.copy(description = newDescription)
                if (card != null) {
                    repository.updateCard(card)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Card is null", isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun setDueDate(timestamp: Long?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val card = _uiState.value.card?.copy(dueDate = timestamp)
                if (card != null) {
                    repository.updateCard(card)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Card is null", isLoading = false)
                }
                } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun addCheckList(title: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val checklist = Checklist(title = title, cardId = cardId)
                repository.addCheckList(checklist)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun updateCheckListTitle(newTitle: String, checklistId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try{
                repository.updateCheckListTitle(checklistId, newTitle)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteCheckList(checklistId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.deleteCheckList(checklistId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun addCheckListItem(checklistId: String, itemText: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try{
                val checklist = _uiState.value.checklists.find { it.id == checklistId }
                if (checklist == null) {
                    _uiState.value = _uiState.value.copy(error = "Checklist not found", isLoading = false)
                    return@launch
                }
                val item = ChecklistItem(text = itemText)
                checklist.items = checklist.items + item
                Log.d("CardDetailViewModel", "checklist: $checklist")
                repository.updateCheckList(checklist)
                _uiState.value = _uiState.value.copy(isLoading = false)

            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun updateCheckListItem(checklistId: String, itemId: String, newText: String, isChecked: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val checkList = _uiState.value.checklists.find { it.id == checklistId }
                if (checkList == null) {
                    _uiState.value = _uiState.value.copy(error = "Checklist not found", isLoading = false)
                    return@launch
                }
                checkList.items.find { it.id == itemId }?.let {
                    it.text = newText
                    it.isChecked = isChecked
                }
                repository.updateCheckList(checkList)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteCheckListItem(checkListId: String, itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val checkList = _uiState.value.checklists.find { it.id == checkListId }
                val updatedCheckListItems = checkList?.items?.filter { it.id != itemId }
                if (checkList != null && updatedCheckListItems != null) {
                    checkList.items = updatedCheckListItems
                    repository.updateCheckList(checkList)
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun addComment(commentText: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val user = repository.getCurrentUser()
                val comment = Comment(text = commentText, cardId = cardId, authorId = user.uid, authorName = user.displayName)
                repository.addComment(comment)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }

    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.deleteComment(commentId)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun deleteCard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                repository.deleteCard(cardId)
                _navigationEvent.emit(NavigationEvent.NavigationBack)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}