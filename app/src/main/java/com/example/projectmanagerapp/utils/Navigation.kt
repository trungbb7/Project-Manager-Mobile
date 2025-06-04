package com.example.projectmanagerapp.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.BoardDetail
import com.example.projectmanagerapp.ui.main.BoardsScreen
import com.example.projectmanagerapp.ui.main.CreateBoardScreen
import com.example.projectmanagerapp.ui.main.HomeScreen
import com.example.projectmanagerapp.ui.main.TrelloCard
import com.example.projectmanagerapp.ui.main.TrelloList
import com.example.trello_clone.ui.screens.boarddetail.BoardDetailScreen
import kotlin.collections.plus
import kotlin.collections.toMutableList


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = AppDestinations.BOARD_DETAIL_ROUTE, modifier = modifier) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen()
        }
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen()
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen()
        }
        composable(AppDestinations.DEMO) {
            val sampleBoards = listOf(
                Board("1", "Dự án cá nhân", backgroundImage = "https://picsum.photos/seed/projectA/600/400", backgroundColor = Color(0xFF4CAF50)),
                Board("2", "Kế hoạch du lịch hè 2025", backgroundColor = Color(0xFF2196F3)),
                Board("3", "Học Jetpack Compose", backgroundImage = "https://picsum.photos/seed/compose/600/400", backgroundColor = Color(0xFFFFC107)),
                Board("4", "Công việc công ty", backgroundColor = Color(0xFF9C27B0))
            )
            BoardsScreen(
                sampleBoards,
                onBoardClick = {
                    navController.navigate(AppDestinations.BOARD_DETAIL_ROUTE)
                },
                onAddBoardClick = {
                    navController.navigate(AppDestinations.CREATE_BOARD_ROUTE)
                },
                onSearchClick = {},
                onRenameBoardRequest = {},
                onChangeBackgroundRequest = {}
            ) { }
        }
        composable(AppDestinations.CREATE_BOARD_ROUTE) {
            CreateBoardScreen(onNavigateBack = {
                navController.popBackStack()
            },
                onCreateBoard = { boardName, backgroundColor, backgroundImageUri ->
                })
        }

        composable(AppDestinations.BOARD_DETAIL_ROUTE) {
            val initialBoard = BoardDetail(
                id = "board1",
                name = "Dự án Di Chuyển Thẻ",
                backgroundColor = Color(0xFF0079BF),
                lists = mutableListOf(
                    TrelloList(
                        id = "list1", name = "Cần làm", cards = mutableListOf(
                            TrelloCard(title = "Task A", listId = "list1"),
                            TrelloCard(title = "Task B", listId = "list1")
                        )
                    ),
                    TrelloList(
                        id = "list2", name = "Đang làm", cards = mutableListOf(
                            TrelloCard(title = "Task C", listId = "list2")
                        )
                    ),
                    TrelloList(id = "list3", name = "Hoàn thành", cards = mutableListOf())
                )
            )
            var boardState by remember { mutableStateOf(initialBoard) }

            MaterialTheme {
                BoardDetailScreen(
                    board = boardState,
                    onNavigateBack = {},
                    onCardClick = { card, list -> println("Clicked card ${card.title} in list ${list.name}") },
                    onAddListToBoard = { title ->
                        val newList = TrelloList(name = title)
                        boardState = boardState.copy(lists = (boardState.lists + newList).toMutableList())
                    },
                    onRenameList = { listId, newName ->
                        boardState = boardState.copy(
                            lists = boardState.lists.map { if (it.id == listId) it.copy(name = newName) else it }.toMutableList()
                        )
                    },
                    onDeleteList = { listId ->
                        boardState = boardState.copy(
                            lists = boardState.lists.filterNot { it.id == listId }.toMutableList()
                        )
                    },
                    onAddCardToList = { listId, cardTitle ->
                        val newCard = TrelloCard(title = cardTitle, listId = listId)
                        boardState = boardState.copy(
                            lists = boardState.lists.map { list ->
                                if (list.id == listId) list.copy(cards = (list.cards + newCard).toMutableList()) else list
                            }.toMutableList()
                        )
                    },
                    onUpdateCard = { updatedCard ->
                        boardState = boardState.copy(
                            lists = boardState.lists.map { list ->
                                list.copy(cards = list.cards.map { card ->
                                    if (card.id == updatedCard.id) updatedCard else card
                                }.toMutableList())
                            }.toMutableList()
                        )
                    },
                    onDeleteCard = { cardId, listId ->
                        boardState = boardState.copy(
                            lists = boardState.lists.map { list ->
                                if (list.id == listId) list.copy(cards = list.cards.filterNot { it.id == cardId }.toMutableList()) else list
                            }.toMutableList()
                        )
                    },
                    onConfirmMoveCard = { cardId, sourceListId, targetListId ->
                        var cardToMove: TrelloCard? = null
                        // Tìm và xóa card khỏi sourceList
                        val listsAfterRemoving = boardState.lists.map { list ->
                            if (list.id == sourceListId) {
                                val foundCard = list.cards.find { it.id == cardId }
                                if (foundCard != null) {
                                    cardToMove = foundCard.copy(listId = targetListId) // Cập nhật listId mới cho card
                                    list.copy(cards = list.cards.filterNot { it.id == cardId }.toMutableList())
                                } else {
                                    list
                                }
                            } else {
                                list
                            }
                        }
                        // Thêm card vào targetList
                        if (cardToMove != null) {
                            val listsAfterAdding = listsAfterRemoving.map { list ->
                                if (list.id == targetListId) {
                                    list.copy(cards = (list.cards + cardToMove!!).toMutableList())
                                } else {
                                    list
                                }
                            }
                            boardState = boardState.copy(lists = listsAfterAdding.toMutableList())
                        } else { // Nếu không tìm thấy card (trường hợp hiếm)
                            boardState = boardState.copy(lists = listsAfterRemoving.toMutableList())
                        }
                        println("Moved card $cardId from $sourceListId to $targetListId")
                    }
                )
            }

        }


    }
}