package com.example.projectmanagerapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectmanagerapp.repositories.Repository
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.Card
import com.example.projectmanagerapp.ui.main.PMList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class BoardDetailUIState(
    val board: Board? = null,
    val lists: List<PMList> = emptyList(),
    val cardsByList: Map<String, List<Card>> = emptyMap(),
    val cardSelectedForMoveInfo: Pair<String, String>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class BoardDetailViewModel(val repository: Repository, val boardId: String): ViewModel() {

    private val _uiState = MutableStateFlow(BoardDetailUIState())
    val uiState: StateFlow<BoardDetailUIState> = _uiState.asStateFlow()

    init {
        Log.d("BoardDetailViewModel", "BoardId: $boardId")
        viewModelScope.launch {
            var pmLists = emptyList<PMList>()

            val boardFlow = repository.getBoard(boardId)

            val cardsByListFlow = repository.getLists(boardId).flatMapLatest { lists ->
                if (lists.isEmpty()) {
                    MutableStateFlow(emptyMap<String, List<Card>>())
                } else {
                    pmLists = lists
                    val cardsFlow: List<Flow<Pair<String, List<Card>>>> = lists.map { list ->
                        repository.getCards(list.id).map { cards ->
                            Pair(list.id, cards)
                        }
                    }
                    combine(cardsFlow) { cards ->
                        cards.toMap()
                    }
                }

            }

            combine(boardFlow, cardsByListFlow) { board, cardsByList ->
                _uiState.value.copy(
                    board = board,
                    lists = pmLists,
                    cardsByList = cardsByList,
                    isLoading = false
                )
            }.catch { e ->
                Log.e("BoardDetailViewModel", "Error: ${e.message}")
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun addCard(card: Card) {
        viewModelScope.launch {
            try{
                repository.addCard(card)
            }catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch {
            try {
                repository.updateCard(card)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteCard(cardId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCard(cardId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun createList(list: PMList) {
        viewModelScope.launch {
            try {
                repository.createList(list)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateList(list: PMList) {
        viewModelScope.launch {
            try {
                repository.updateList(list)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            try {
                repository.deleteList(listId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun moveCard(cardId: String, targetListId: String){
        viewModelScope.launch {
            try {
                repository.moveCard(cardId, targetListId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}