//package com.example.trello_clone.ui.screens.boarddetail
//
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Build
//import androidx.compose.material.icons.filled.CheckCircle // Icon cho thẻ đang chọn
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.example.projectmanagerapp.ui.main.BoardDetail
//import com.example.projectmanagerapp.ui.main.Card
//import com.example.projectmanagerapp.ui.main.PMList
//import java.util.UUID
//
//
//// --- Composable Chính ---
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BoardDetailScreen(
//    board: BoardDetail,
//    onNavigateBack: () -> Unit,
//    onCardClick: (Card, PMList) -> Unit,
//    // List Actions
//    onAddListToBoard: (String) -> Unit,
//    onRenameList: (listId: String, newName: String) -> Unit,
//    onDeleteList: (listId: String) -> Unit,
//    // Card Actions
//    onAddCardToList: (listId: String, cardTitle: String) -> Unit,
//    onUpdateCard: (updatedCard: Card) -> Unit,
//    onDeleteCard: (cardId: String, listId: String) -> Unit,
//    // Move Card Action
//    onConfirmMoveCard: (cardId: String, sourceListId: String, targetListId: String) -> Unit
//) {
//    var showAddListInput by remember { mutableStateOf(false) }
//    var newListTitle by remember { mutableStateOf("") }
//
//    // State để lưu thông tin thẻ đang được chọn để di chuyển: Pair(cardId, sourceListId)
//    var cardSelectedForMoveInfo by remember { mutableStateOf<Pair<String, String>?>(null) }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(board.name, fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
//                    }
//                },
//                actions = {
//                    // Nếu đang trong chế độ di chuyển thẻ, có thể hiển thị nút Hủy
//                    if (cardSelectedForMoveInfo != null) {
//                        TextButton(onClick = { cardSelectedForMoveInfo = null }) {
//                            Text("HỦY DI CHUYỂN", color = Color.White)
//                        }
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = board.backgroundColor.copy(alpha = 0.8f),
//                    titleContentColor = Color.White,
//                    navigationIconContentColor = Color.White,
//                    actionIconContentColor = Color.White
//                )
//            )
//        },
//        containerColor = board.backgroundColor
//    ) { paddingValues ->
//        LazyRow(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            itemsIndexed(board.lists, key = { _, list -> list.id }) { _, trelloList ->
//                ListItemColumn(
//                    trelloList = trelloList,
//                    cardSelectedForMoveInfo = cardSelectedForMoveInfo,
//                    onCardClick = { card ->
//                        // Nếu đang trong chế độ di chuyển và click vào thẻ khác thẻ đang chọn, không làm gì cả
//                        // Hoặc có thể cho phép click để mở chi tiết nếu muốn
//                        if (cardSelectedForMoveInfo == null || cardSelectedForMoveInfo?.first == card.id) {
//                            onCardClick(card, trelloList)
//                        }
//                    },
//                    // List Menu Actions
//                    onRenameList = { newName -> onRenameList(trelloList.id, newName) },
//                    onDeleteList = { onDeleteList(trelloList.id) },
//                    onAddCardFromMenu = { cardTitle -> onAddCardToList(trelloList.id, cardTitle) },
//                    // Card Actions
//                    onUpdateCard = onUpdateCard,
//                    onDeleteCard = { cardId -> onDeleteCard(cardId, trelloList.id) },
//                    // Move Card Actions
//                    onToggleSelectCardForMove = { cardId, sourceListId ->
//                        cardSelectedForMoveInfo = if (cardSelectedForMoveInfo?.first == cardId) {
//                            null // Bỏ chọn nếu click lại thẻ đang chọn
//                        } else {
//                            Pair(cardId, sourceListId) // Chọn thẻ mới
//                        }
//                    },
//                    onConfirmMoveCardToList = { targetListId ->
//                        cardSelectedForMoveInfo?.let { (cardId, sourceListId) ->
//                            onConfirmMoveCard(cardId, sourceListId, targetListId)
//                            cardSelectedForMoveInfo = null // Reset sau khi di chuyển
//                        }
//                    }
//                )
//            }
//            item {
//                AddListColumn(
//                    showInput = showAddListInput,
//                    onShowInputToggle = { showAddListInput = !showAddListInput },
//                    listTitle = newListTitle,
//                    onListTitleChange = { newListTitle = it },
//                    onAddList = {
//                        if (newListTitle.isNotBlank()) {
//                            onAddListToBoard(newListTitle)
//                            newListTitle = ""
//                            showAddListInput = false
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
//
//// --- Composable cho một cột Danh sách (List) ---
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ListItemColumn(
//    trelloList: TrelloList,
//    cardSelectedForMoveInfo: Pair<String, String>?, // (cardId, sourceListId)
//    onCardClick: (TrelloCard) -> Unit,
//    onRenameList: (String) -> Unit,
//    onDeleteList: () -> Unit,
//    onAddCardFromMenu: (String) -> Unit,
//    onUpdateCard: (TrelloCard) -> Unit,
//    onDeleteCard: (cardId: String) -> Unit,
//    onToggleSelectCardForMove: (cardId: String, sourceListId: String) -> Unit,
//    onConfirmMoveCardToList: (targetListId: String) -> Unit // Khi list này là đích
//) {
//    var showAddCardInput by remember { mutableStateOf(false) }
//    var newCardTitle by remember { mutableStateOf("") }
//    var isEditingListTitle by remember { mutableStateOf(false) }
//    var editingListTitle by remember { mutableStateOf(TextFieldValue(trelloList.name)) }
//    var showListMenu by remember { mutableStateOf(false) }
//    val listState = rememberLazyListState()
//
//    Card(
//        modifier = Modifier
//            .width(300.dp)
//            .fillMaxHeight()
//            .padding(end = 8.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBECF0)),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Column(modifier = Modifier.padding(8.dp)) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 8.dp)
//            ) {
//                if (isEditingListTitle) { /* ... Phần sửa tên list giữ nguyên ... */
//                    OutlinedTextField(
//                        value = editingListTitle,
//                        onValueChange = { editingListTitle = it },
//                        modifier = Modifier.weight(1f),
//                        singleLine = true,
//                        colors = TextFieldDefaults.colors(
//                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
//                        ),
//                        trailingIcon = {
//                            IconButton(onClick = {
//                                if (editingListTitle.text.isNotBlank() && editingListTitle.text != trelloList.name) {
//                                    onRenameList(editingListTitle.text)
//                                }
//                                isEditingListTitle = false
//                            }) { Icon(Icons.Filled.Add, "Lưu") }
//                        }
//                    )
//                } else {
//                    Text(
//                        text = trelloList.name,
//                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//                        modifier = Modifier
//                            .weight(1f)
//                            .clickable { isEditingListTitle = true }
//                            .padding(vertical = 8.dp)
//                    )
//                }
//                Box {
//                    IconButton(onClick = { showListMenu = true }) {
//                        Icon(Icons.Filled.MoreVert, contentDescription = "Tùy chọn danh sách")
//                    }
//                    ListOptionsMenu(
//                        expanded = showListMenu,
//                        onDismiss = { showListMenu = false },
//                        onRenameClick = {
//                            isEditingListTitle = true
//                            editingListTitle = TextFieldValue(trelloList.name)
//                            showListMenu = false
//                        },
//                        onAddCardClick = {
//                            showAddCardInput = true
//                            newCardTitle = ""
//                            showListMenu = false
//                        },
//                        onDeleteClick = {
//                            onDeleteList()
//                            showListMenu = false
//                        },
//                        // Thêm tùy chọn "Move selected card here"
//                        showMoveCardToThisListOption = cardSelectedForMoveInfo != null && cardSelectedForMoveInfo.second != trelloList.id,
//                        onMoveCardToThisListClick = {
//                            onConfirmMoveCardToList(trelloList.id)
//                            showListMenu = false
//                        }
//                    )
//                }
//            }
//
//            LazyColumn(
//                state = listState,
//                modifier = Modifier.weight(1f).fillMaxWidth(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                itemsIndexed(trelloList.cards, key = { _, card -> card.id }) { _, card ->
//                    CardItem(
//                        card = card,
//                        isCurrentlySelectedForMove = cardSelectedForMoveInfo?.first == card.id,
//                        onClick = { onCardClick(card) },
//                        onUpdateCard = onUpdateCard,
//                        onDeleteCard = onDeleteCard,
//                        onSelectForMove = { onToggleSelectCardForMove(card.id, trelloList.id) }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//            if (showAddCardInput) { /* ... Phần thêm card mới giữ nguyên ... */
//                OutlinedTextField(
//                    value = newCardTitle,
//                    onValueChange = { newCardTitle = it },
//                    label = { Text("Nhập tiêu đề thẻ...") },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true,
//                    trailingIcon = {
//                        IconButton(onClick = { showAddCardInput = false }) {
//                            Icon(Icons.Filled.Close, "Hủy")
//                        }
//                    }
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Button(
//                    onClick = {
//                        if (newCardTitle.isNotBlank()) {
//                            onAddCardFromMenu(newCardTitle) // Sử dụng callback mới
//                            newCardTitle = ""
//                            showAddCardInput = false
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) { Text("Thêm thẻ") }
//            } else {
//                TextButton(
//                    onClick = { showAddCardInput = true },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
//                    Spacer(Modifier.width(4.dp))
//                    Text("Thêm thẻ khác")
//                }
//            }
//        }
//    }
//}
//
//// --- Composable cho một mục Card ---
//@Composable
//fun CardItem(
//    card: TrelloCard,
//    isCurrentlySelectedForMove: Boolean,
//    onClick: () -> Unit,
//    onUpdateCard: (TrelloCard) -> Unit,
//    onDeleteCard: (cardId: String) -> Unit,
//    onSelectForMove: () -> Unit // Callback khi chọn "Di chuyển thẻ" từ menu của card này
//) {
//    var showCardMenu by remember { mutableStateOf(false) }
//    var isEditingCardTitle by remember { mutableStateOf(false) }
//    var editingCardTitle by remember { mutableStateOf(TextFieldValue(card.title)) }
//
//    val cardBorder = if (isCurrentlySelectedForMove) {
//        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
//    } else {
//        null
//    }
//    val cardBackgroundColor = if (isCurrentlySelectedForMove) {
//        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
//    } else {
//        Color.White
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = { if (!isEditingCardTitle && !isCurrentlySelectedForMove) onClick() }),
//        shape = RoundedCornerShape(6.dp),
//        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
//        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentlySelectedForMove) 4.dp else 2.dp),
//        border = cardBorder
//    ) {
//        Row(
//            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (isCurrentlySelectedForMove) {
//                Icon(
//                    Icons.Filled.CheckCircle,
//                    contentDescription = "Đã chọn để di chuyển",
//                    tint = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.size(18.dp).padding(end = 4.dp)
//                )
//            }
//            if (isEditingCardTitle) { /* ... Phần sửa tên card giữ nguyên ... */
//                OutlinedTextField(
//                    value = editingCardTitle,
//                    onValueChange = { editingCardTitle = it },
//                    modifier = Modifier.weight(1f).padding(vertical = 4.dp),
//                    singleLine = true,
//                    textStyle = MaterialTheme.typography.bodyMedium,
//                    trailingIcon = {
//                        IconButton(onClick = {
//                            if (editingCardTitle.text.isNotBlank() && editingCardTitle.text != card.title) {
//                                onUpdateCard(card.copy(title = editingCardTitle.text))
//                            }
//                            isEditingCardTitle = false
//                        }) { Icon(Icons.Filled.Add, "Lưu") }
//                    }
//                )
//            } else {
//                Text(
//                    text = card.title,
//                    modifier = Modifier.weight(1f).padding(vertical = 6.dp),
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//            Box {
//                IconButton(onClick = { showCardMenu = true }) {
//                    Icon(Icons.Filled.MoreVert, contentDescription = "Tùy chọn thẻ", Modifier.size(18.dp))
//                }
//                CardOptionsMenu(
//                    expanded = showCardMenu,
//                    onDismiss = { showCardMenu = false },
//                    onEditClick = {
//                        isEditingCardTitle = true
//                        editingCardTitle = TextFieldValue(card.title)
//                        showCardMenu = false
//                    },
//                    onDeleteClick = {
//                        onDeleteCard(card.id)
//                        showCardMenu = false
//                    },
//                    onMoveClick = { // Khi nhấn "Di chuyển thẻ" trên menu của card
//                        onSelectForMove()
//                        showCardMenu = false
//                    },
//                    isCardSelectedForMove = isCurrentlySelectedForMove
//                )
//            }
//        }
//    }
//}
//
//// --- Menu cho List ---
//@Composable
//fun ListOptionsMenu(
//    expanded: Boolean,
//    onDismiss: () -> Unit,
//    onRenameClick: () -> Unit,
//    onAddCardClick: () -> Unit,
//    onDeleteClick: () -> Unit,
//    showMoveCardToThisListOption: Boolean, // Điều kiện hiển thị tùy chọn di chuyển
//    onMoveCardToThisListClick: () -> Unit  // Hành động khi di chuyển thẻ vào list này
//) {
//    DropdownMenu(
//        expanded = expanded,
//        onDismissRequest = onDismiss
//    ) {
//        DropdownMenuItem(
//            text = { Text("Đổi tên danh sách") },
//            onClick = onRenameClick,
//            leadingIcon = { Icon(Icons.Filled.Edit, null) }
//        )
//        DropdownMenuItem(
//            text = { Text("Thêm thẻ...") },
//            onClick = onAddCardClick,
//            leadingIcon = { Icon(Icons.Default.Add, null) }
//        )
//        if (showMoveCardToThisListOption) {
//            Divider()
//            DropdownMenuItem(
//                text = { Text("Chuyển thẻ được chọn vào đây") },
//                onClick = onMoveCardToThisListClick,
//                leadingIcon = { Icon(Icons.Filled.Build, null, tint = MaterialTheme.colorScheme.primary) }
//            )
//        }
//        Divider()
//        DropdownMenuItem(
//            text = { Text("Xóa danh sách này") },
//            onClick = onDeleteClick,
//            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) },
//            // colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.error) // Material 3 không có cách này trực tiếp, dùng tint cho icon
//        )
//    }
//}
//
//// --- Menu cho Card ---
//@Composable
//fun CardOptionsMenu(
//    expanded: Boolean,
//    onDismiss: () -> Unit,
//    onEditClick: () -> Unit,
//    onDeleteClick: () -> Unit,
//    onMoveClick: () -> Unit, // Thêm callback cho hành động "Di chuyển thẻ"
//    isCardSelectedForMove: Boolean // Để thay đổi text của nút "Move"
//) {
//    DropdownMenu(
//        expanded = expanded,
//        onDismissRequest = onDismiss
//    ) {
//        DropdownMenuItem(
//            text = { Text("Sửa tiêu đề thẻ") },
//            onClick = onEditClick,
//            leadingIcon = { Icon(Icons.Filled.Edit, null) }
//        )
//        DropdownMenuItem(
//            text = { Text(if (isCardSelectedForMove) "Bỏ chọn di chuyển" else "Di chuyển thẻ...") },
//            onClick = onMoveClick,
//            leadingIcon = { Icon(Icons.Filled.Build, null) }
//        )
//        Divider()
//        DropdownMenuItem(
//            text = { Text("Xóa thẻ này") },
//            onClick = onDeleteClick,
//            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) }
//        )
//    }
//}
//
//// --- Composable cho cột "Thêm danh sách mới" (Không thay đổi) ---
//@Composable
//fun AddListColumn( /* ... Giữ nguyên ... */
//                   showInput: Boolean,
//                   onShowInputToggle: () -> Unit,
//                   listTitle: String,
//                   onListTitleChange: (String) -> Unit,
//                   onAddList: () -> Unit
//) {
//    if (showInput) {
//        Card(
//            modifier = Modifier
//                .width(300.dp)
//                .wrapContentHeight()
//                .padding(end = 8.dp),
//            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBECF0).copy(alpha = 0.8f)),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Column(modifier = Modifier.padding(8.dp)) {
//                OutlinedTextField(
//                    value = listTitle,
//                    onValueChange = onListTitleChange,
//                    label = { Text("Nhập tiêu đề danh sách...") },
//                    modifier = Modifier.fillMaxWidth(),
//                    singleLine = true
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                Row {
//                    Button(onClick = onAddList) {
//                        Text("Thêm danh sách")
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    TextButton(onClick = onShowInputToggle) {
//                        Text("Hủy")
//                    }
//                }
//            }
//        }
//    } else {
//        Button(
//            onClick = onShowInputToggle,
//            modifier = Modifier
//                .width(300.dp)
//                .heightIn(min = 48.dp)
//                .padding(end = 8.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.White.copy(alpha = 0.3f),
//                contentColor = Color.White
//            ),
//            shape = RoundedCornerShape(8.dp)
//        ) {
//            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
//            Spacer(modifier = Modifier.width(4.dp))
//            Text("Thêm danh sách khác")
//        }
//    }
//}
//
//
//// --- Preview ---
//@Preview(showBackground = true, device = "spec:width=1080px,height=2340px,dpi=440")
//@Composable
//fun BoardDetailScreenWithMoveCardPreview() {
//    val initialBoard = BoardDetail(
//        id = "board1",
//        name = "Dự án Di Chuyển Thẻ",
//        backgroundColor = Color(0xFF0079BF),
//        lists = mutableListOf(
//            TrelloList(
//                id = "list1", name = "Cần làm", cards = mutableListOf(
//                    TrelloCard(title = "Task A", listId = "list1"),
//                    TrelloCard(title = "Task B", listId = "list1")
//                )
//            ),
//            TrelloList(
//                id = "list2", name = "Đang làm", cards = mutableListOf(
//                    TrelloCard(title = "Task C", listId = "list2")
//                )
//            ),
//            TrelloList(id = "list3", name = "Hoàn thành", cards = mutableListOf())
//        )
//    )
//    var boardState by remember { mutableStateOf(initialBoard) }
//
//    MaterialTheme {
//        BoardDetailScreen(
//            board = boardState,
//            onNavigateBack = {},
//            onCardClick = { card, list -> println("Clicked card ${card.title} in list ${list.name}") },
//            onAddListToBoard = { title ->
//                val newList = TrelloList(name = title)
//                boardState = boardState.copy(lists = (boardState.lists + newList).toMutableList())
//            },
//            onRenameList = { listId, newName ->
//                boardState = boardState.copy(
//                    lists = boardState.lists.map { if (it.id == listId) it.copy(name = newName) else it }.toMutableList()
//                )
//            },
//            onDeleteList = { listId ->
//                boardState = boardState.copy(
//                    lists = boardState.lists.filterNot { it.id == listId }.toMutableList()
//                )
//            },
//            onAddCardToList = { listId, cardTitle ->
//                val newCard = TrelloCard(title = cardTitle, listId = listId)
//                boardState = boardState.copy(
//                    lists = boardState.lists.map { list ->
//                        if (list.id == listId) list.copy(cards = (list.cards + newCard).toMutableList()) else list
//                    }.toMutableList()
//                )
//            },
//            onUpdateCard = { updatedCard ->
//                boardState = boardState.copy(
//                    lists = boardState.lists.map { list ->
//                        list.copy(cards = list.cards.map { card ->
//                            if (card.id == updatedCard.id) updatedCard else card
//                        }.toMutableList())
//                    }.toMutableList()
//                )
//            },
//            onDeleteCard = { cardId, listId ->
//                boardState = boardState.copy(
//                    lists = boardState.lists.map { list ->
//                        if (list.id == listId) list.copy(cards = list.cards.filterNot { it.id == cardId }.toMutableList()) else list
//                    }.toMutableList()
//                )
//            },
//            onConfirmMoveCard = { cardId, sourceListId, targetListId ->
//                var cardToMove: TrelloCard? = null
//                // Tìm và xóa card khỏi sourceList
//                val listsAfterRemoving = boardState.lists.map { list ->
//                    if (list.id == sourceListId) {
//                        val foundCard = list.cards.find { it.id == cardId }
//                        if (foundCard != null) {
//                            cardToMove = foundCard.copy(listId = targetListId) // Cập nhật listId mới cho card
//                            list.copy(cards = list.cards.filterNot { it.id == cardId }.toMutableList())
//                        } else {
//                            list
//                        }
//                    } else {
//                        list
//                    }
//                }
//                // Thêm card vào targetList
//                if (cardToMove != null) {
//                    val listsAfterAdding = listsAfterRemoving.map { list ->
//                        if (list.id == targetListId) {
//                            list.copy(cards = (list.cards + cardToMove!!).toMutableList())
//                        } else {
//                            list
//                        }
//                    }
//                    boardState = boardState.copy(lists = listsAfterAdding.toMutableList())
//                } else { // Nếu không tìm thấy card (trường hợp hiếm)
//                    boardState = boardState.copy(lists = listsAfterRemoving.toMutableList())
//                }
//                println("Moved card $cardId from $sourceListId to $targetListId")
//            }
//        )
//    }
//}