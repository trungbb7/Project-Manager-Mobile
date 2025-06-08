package com.example.projectmanagerapp.ui.main.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.viewmodels.BoardViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardsScreen(
    viewModel: BoardViewModel,
    onBoardClick: (Board) -> Unit,
    onAddBoardClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRenameBoardRequest: (String) -> Unit,
    onChangeBackgroundRequest: (Board) -> Unit,
    onDeleteBoardRequest: (Board) -> Unit,
    onEditBoard: (String) -> Unit
) {


    val uiState = viewModel.boardUIState.collectAsState()

    val error = uiState.value.error
    val isLoading = uiState.value.idLoading

    var deleteBoardId by remember { mutableStateOf<String?>(null) }


    var openDeleteBoardDialog by remember { mutableStateOf(false) }


    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(message = error)
            viewModel.clearError()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (openDeleteBoardDialog) {
        AlertDialog(
            onDismissRequest = {
                openDeleteBoardDialog = false
            },
            title = { Text(text = "Cảnh báo") },

            confirmButton = {
                TextButton(onClick = {
                    openDeleteBoardDialog = false

                    viewModel.deleteBoard(deleteBoardId!!)
                }
                )
                { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { openDeleteBoardDialog = false }) { Text("Hủy bỏ") }
            },
            text = {
                Text("Bạn có chắc chắn muốn xóa bảng này?")
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảng của tôi") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Tìm kiếm bảng")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBoardClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "Tạo bảng mới")
            }
        }
    ) { paddingValues ->
        if (uiState.value.boards.isEmpty()) {
            EmptyBoardsView(modifier = Modifier.padding(paddingValues))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.value.boards) { board ->
                    BoardItem(
                        board = board,
                        onEditBoardClick = {
                            onEditBoard(board.id)
                        },
                        onClick = { onBoardClick(board) },
                        onRenameBoardRequest = { newName ->
                            if (newName != board.name) {
                                viewModel.editBoardName(board, newName)
                            }
                        },
                        onChangeBackgroundRequest = onChangeBackgroundRequest,
                        onDeleteBoardRequest = onDeleteBoardRequest,
                        onDeleteBoardClick = { boardId ->
                            deleteBoardId = boardId
                            openDeleteBoardDialog = true
                        }
                    )
                }
            }
        }

    }
}

// --- Composable cho một mục bảng ---
@Composable
fun BoardItem(
    board: Board,
    onEditBoardClick: () -> Unit,
    onClick: () -> Unit,
    onRenameBoardRequest: (String) -> Unit,
    onChangeBackgroundRequest: (Board) -> Unit,
    onDeleteBoardClick: (String) -> Unit,
    onDeleteBoardRequest: (Board) -> Unit
) {

    var showItemMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Ảnh nền hoặc màu nền
            if (board.backgroundImage != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(board.backgroundImage)
                        .crossfade(true)
                        // .placeholder(R.drawable.placeholder_board_background) // Ảnh chờ
                        // .error(R.drawable.error_board_background) // Ảnh lỗi
                        .build(),
                    contentDescription = "Ảnh nền bảng",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(board.backgroundColor.toColorInt()))
                )
            }

            // Lớp phủ mờ để dễ đọc chữ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            // Tiêu đề bảng và nút tùy chọn
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = board.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = { showItemMenu = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "Tùy chọn khác",
                        tint = Color.White // Đảm bảo icon dễ thấy
                    )
                }

                // Dropdown Menu cho tùy chọn của bảng
                DropdownMenu(
                    expanded = showItemMenu,
                    onDismissRequest = { showItemMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Chỉnh sửa") },
                        onClick = {
                            // Xử lý đổi tên
                            showItemMenu = false
                            onEditBoardClick()
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Xóa bảng", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            // Xử lý xóa bảng (cần có xác nhận)
                            showItemMenu = false
                            onDeleteBoardClick(board.id)
                        }
                    )
                }
            }
        }
    }
}

// --- Composable cho trường hợp không có bảng nào ---
@Composable
fun EmptyBoardsView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chưa có bảng nào",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nhấn nút '+' để tạo bảng mới và bắt đầu quản lý công việc của bạn.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
