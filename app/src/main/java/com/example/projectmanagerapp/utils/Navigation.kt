package com.example.projectmanagerapp.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmanagerapp.repositories.Repository
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.BoardViewModel
import com.example.projectmanagerapp.ui.main.BoardViewModelFactory
import com.example.projectmanagerapp.ui.main.BoardsScreen
import com.example.projectmanagerapp.ui.main.Checklist
import com.example.projectmanagerapp.ui.main.ChecklistItem
import com.example.projectmanagerapp.ui.main.Comment
import com.example.projectmanagerapp.ui.main.CreateBoardScreen
import com.example.projectmanagerapp.ui.main.HomeScreen
import com.example.projectmanagerapp.repositories.RepositoryImplement
import com.example.projectmanagerapp.ui.main.CreateBoardViewModel
import com.example.projectmanagerapp.ui.main.CreateBoardViewModelFactory
import com.example.projectmanagerapp.ui.theme.ProjectManagerAppTheme
import kotlin.collections.plus
import kotlin.collections.toMutableList



@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = AppDestinations.BOARD_ROUTE, modifier = modifier) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen()
        }
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen()
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen()
        }
        composable(AppDestinations.BOARD_ROUTE) {
//            val sampleBoards = listOf(
//                Board("1", "Dự án cá nhân", backgroundImage = "https://picsum.photos/seed/projectA/600/400", backgroundColor = "#FF4CAF50"),
//                Board("2", "Kế hoạch du lịch hè 2025", backgroundColor = "#FF2196F3"),
//                Board("3", "Học Jetpack Compose", backgroundImage = "https://picsum.photos/seed/compose/600/400", backgroundColor = "#FFFFC107"),
//                Board("4", "Công việc công ty", backgroundColor = "#FF9C27B0")
//            )
            val repository = RepositoryImplement()
            val viewModel: BoardViewModel = viewModel(factory = BoardViewModelFactory(repository))
            BoardsScreen(
                viewModel = viewModel,
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
            val repository = RepositoryImplement()
            val viewModel: CreateBoardViewModel = viewModel(factory = CreateBoardViewModelFactory(repository))
            CreateBoardScreen(onNavigateBack = {
                navController.popBackStack()
            },
                onBoardCreatedSuccessfully = {
                    navController.popBackStack()
                },
                viewModel = viewModel)
        }

//        composable(AppDestinations.BOARD_DETAIL_ROUTE) {
//            val initialBoard = BoardDetail(
//                id = "board1",
//                name = "Dự án Di Chuyển Thẻ",
//                backgroundColor = Color(0xFF0079BF),
//                lists = mutableListOf(
//                    TrelloList(
//                        id = "list1", name = "Cần làm", cards = mutableListOf(
//                            TrelloCard(title = "Task A", listId = "list1"),
//                            TrelloCard(title = "Task B", listId = "list1")
//                        )
//                    ),
//                    TrelloList(
//                        id = "list2", name = "Đang làm", cards = mutableListOf(
//                            TrelloCard(title = "Task C", listId = "list2")
//                        )
//                    ),
//                    TrelloList(id = "list3", name = "Hoàn thành", cards = mutableListOf())
//                )
//            )
//            var boardState by remember { mutableStateOf(initialBoard) }
//
//            MaterialTheme {
//                BoardDetailScreen(
//                    board = boardState,
//                    onNavigateBack = {},
//                    onCardClick = { card, list -> println("Clicked card ${card.title} in list ${list.name}") },
//                    onAddListToBoard = { title ->
//                        val newList = TrelloList(name = title)
//                        boardState = boardState.copy(lists = (boardState.lists + newList).toMutableList())
//                    },
//                    onRenameList = { listId, newName ->
//                        boardState = boardState.copy(
//                            lists = boardState.lists.map { if (it.id == listId) it.copy(name = newName) else it }.toMutableList()
//                        )
//                    },
//                    onDeleteList = { listId ->
//                        boardState = boardState.copy(
//                            lists = boardState.lists.filterNot { it.id == listId }.toMutableList()
//                        )
//                    },
//                    onAddCardToList = { listId, cardTitle ->
//                        val newCard = TrelloCard(title = cardTitle, listId = listId)
//                        boardState = boardState.copy(
//                            lists = boardState.lists.map { list ->
//                                if (list.id == listId) list.copy(cards = (list.cards + newCard).toMutableList()) else list
//                            }.toMutableList()
//                        )
//                    },
//                    onUpdateCard = { updatedCard ->
//                        boardState = boardState.copy(
//                            lists = boardState.lists.map { list ->
//                                list.copy(cards = list.cards.map { card ->
//                                    if (card.id == updatedCard.id) updatedCard else card
//                                }.toMutableList())
//                            }.toMutableList()
//                        )
//                    },
//                    onDeleteCard = { cardId, listId ->
//                        boardState = boardState.copy(
//                            lists = boardState.lists.map { list ->
//                                if (list.id == listId) list.copy(cards = list.cards.filterNot { it.id == cardId }.toMutableList()) else list
//                            }.toMutableList()
//                        )
//                    },
//                    onConfirmMoveCard = { cardId, sourceListId, targetListId ->
//                        var cardToMove: TrelloCard? = null
//                        // Tìm và xóa card khỏi sourceList
//                        val listsAfterRemoving = boardState.lists.map { list ->
//                            if (list.id == sourceListId) {
//                                val foundCard = list.cards.find { it.id == cardId }
//                                if (foundCard != null) {
//                                    cardToMove = foundCard.copy(listId = targetListId) // Cập nhật listId mới cho card
//                                    list.copy(cards = list.cards.filterNot { it.id == cardId }.toMutableList())
//                                } else {
//                                    list
//                                }
//                            } else {
//                                list
//                            }
//                        }
//                        // Thêm card vào targetList
//                        if (cardToMove != null) {
//                            val listsAfterAdding = listsAfterRemoving.map { list ->
//                                if (list.id == targetListId) {
//                                    list.copy(cards = (list.cards + cardToMove!!).toMutableList())
//                                } else {
//                                    list
//                                }
//                            }
//                            boardState = boardState.copy(lists = listsAfterAdding.toMutableList())
//                        } else { // Nếu không tìm thấy card (trường hợp hiếm)
//                            boardState = boardState.copy(lists = listsAfterRemoving.toMutableList())
//                        }
//                        println("Moved card $cardId from $sourceListId to $targetListId")
//                    }
//                )
//            }
//
//        }
//
//        composable(AppDestinations.CARD_DETAIL_ROUTE) {
//            val sampleCard = remember {
//                TrelloCard(
//                    id = "card1",
//                    title = "Hoàn thiện giao diện Chi tiết Card",
//                    listId = "list_inprogress",
//                    description = "Cần đảm bảo các thành phần hiển thị đúng và có thể tương tác: \n- Tiêu đề \n- Mô tả \n- Checklist \n- Bình luận.",
//                    dueDate = System.currentTimeMillis() + (3 * 24 * 60 * 60 * 1000), // 3 ngày sau
//                    checklists = mutableListOf(
//                        Checklist(
//                            id = "cl1", title = "Giao diện cơ bản", items = mutableListOf(
//                                ChecklistItem(id = "cli1_1", text = "Hiển thị tiêu đề", isChecked = true),
//                                ChecklistItem(id = "cli1_2", text = "Hiển thị mô tả", isChecked = true),
//                                ChecklistItem(id = "cli1_3", text = "Nút quay lại")
//                            )
//                        ),
//                        Checklist(
//                            id = "cl2", title = "Tương tác", items = mutableListOf(
//                                ChecklistItem(id = "cli2_1", text = "Sửa tiêu đề"),
//                                ChecklistItem(id = "cli2_2", text = "Thêm/sửa/xóa checklist item", isChecked = false)
//                            )
//                        )
//                    ),
//                    comments = mutableListOf(
//                        Comment(id = "cmt1", authorId = "user1", authorName = "Alice", text = "Checklist 'Giao diện cơ bản' còn thiếu mục nào không?"),
//                        Comment(id = "cmt2", authorId = "user2", authorName = "Bob", text = "Tôi nghĩ là đủ rồi đó.", timestamp = System.currentTimeMillis() - 60000)
//                    )
//                )
//            }
//            var currentCardState by remember { mutableStateOf(sampleCard) }
//
//            ProjectManagerAppTheme { // Sử dụng Theme của bạn
//                CardDetailScreen(
//                    card = currentCardState,
//                    listName = "Đang thực hiện",
//                    boardName = "Dự án Trello Clone",
//                    onNavigateBack = {},
//                    onUpdateCardTitle = { newTitle -> currentCardState = currentCardState.copy(title = newTitle) },
//                    onUpdateCardDescription = { newDesc -> currentCardState = currentCardState.copy(description = newDesc) },
//                    onSetDueDate = { timestamp -> currentCardState = currentCardState.copy(dueDate = timestamp) },
//                    onAddChecklist = { title ->
//                        val newChecklist = Checklist(title = title)
//                        currentCardState = currentCardState.copy(checklists = (currentCardState.checklists + newChecklist).toMutableList())
//                    },
//                    onUpdateChecklistTitle = { clId, newTitle ->
//                        currentCardState = currentCardState.copy(checklists = currentCardState.checklists.map {
//                            if (it.id == clId) it.copy(title = newTitle) else it
//                        }.toMutableList())
//                    },
//                    onDeleteChecklist = { clId ->
//                        currentCardState = currentCardState.copy(checklists = currentCardState.checklists.filterNot { it.id == clId }.toMutableList())
//                    },
//                    onAddChecklistItem = { clId, itemText ->
//                        val newClItem = ChecklistItem(text = itemText)
//                        currentCardState = currentCardState.copy(checklists = currentCardState.checklists.map {
//                            if (it.id == clId) it.copy(items = (it.items + newClItem).toMutableList()) else it
//                        }.toMutableList())
//                    },
//                    onUpdateChecklistItem = { clId, itemId, newText, isChecked ->
//                        currentCardState = currentCardState.copy(checklists = currentCardState.checklists.map { cl ->
//                            if (cl.id == clId) {
//                                cl.copy(items = cl.items.map { item ->
//                                    if (item.id == itemId) item.copy(text = newText, isChecked = isChecked) else item
//                                }.toMutableList())
//                            } else cl
//                        }.toMutableList())
//                    },
//                    onDeleteChecklistItem = { clId, itemId ->
//                        currentCardState = currentCardState.copy(checklists = currentCardState.checklists.map { cl ->
//                            if (cl.id == clId) {
//                                cl.copy(items = cl.items.filterNot { it.id == itemId }.toMutableList())
//                            } else cl
//                        }.toMutableList())
//                    },
//                    onAddComment = { text ->
//                        val newComment = Comment(authorId = "currentUser", authorName = "Current User", text = text)
//                        currentCardState = currentCardState.copy(comments = (currentCardState.comments + newComment).toMutableList())
//                    },
//                    onDeleteCard = { println("Card deleted!") }
//                )
//            }
//        }


    }
}