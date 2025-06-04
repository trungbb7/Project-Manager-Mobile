package com.example.projectmanagerapp.ui.main



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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Giả sử bạn có ảnh placeholder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
// import com.example.trello_clone.R // Import R của bạn để lấy drawable

// --- Data Class (Mẫu) ---
data class Board(
    val id: String,
    val name: String,
    val backgroundImage: String? = null, // URL hoặc URI tới ảnh nền
    val backgroundColor: Color = Color.Gray // Màu nền mặc định nếu không có ảnh
)

// --- Composable chính cho màn hình quản lý bảng ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardsScreen(
    boards: List<Board>,
    onBoardClick: (Board) -> Unit,
    onAddBoardClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRenameBoardRequest: (Board) -> Unit,
    onChangeBackgroundRequest: (Board) -> Unit,
    onDeleteBoardRequest: (Board) -> Unit
    // Thêm các callback khác nếu cần, ví dụ: onBoardOptionsClick: (Board) -> Unit
) {
//    var showMenu by remember { mutableStateOf(false) }
//    var selectedBoardForMenu by remember { mutableStateOf<Board?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bảng của tôi") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Tìm kiếm bảng")
                    }
                    // Bạn có thể thêm các action khác ở đây
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
        if (boards.isEmpty()) {
            EmptyBoardsView(modifier = Modifier.padding(paddingValues))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Hiển thị 2 bảng trên một hàng
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(boards) { board ->
                    BoardItem(
                        board = board,
                        onClick = { onBoardClick(board) },
                        onRenameBoardRequest = onRenameBoardRequest,
                        onChangeBackgroundRequest = onChangeBackgroundRequest,
                        onDeleteBoardRequest = onDeleteBoardRequest
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
    onClick: () -> Unit,
    onRenameBoardRequest: (Board) -> Unit,
    onChangeBackgroundRequest: (Board) -> Unit,
    onDeleteBoardRequest: (Board) -> Unit
) {

    var showItemMenu by remember { mutableStateOf(false)  }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f) // Tỷ lệ khung hình cho mỗi bảng
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
                        .background(board.backgroundColor)
                )
            }

            // Lớp phủ mờ để dễ đọc chữ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)) // Điều chỉnh alpha tùy ý
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
                        color = Color.White // Đảm bảo chữ dễ đọc trên nền
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
                            text = { Text("Đổi tên bảng") },
                            onClick = {
                                // Xử lý đổi tên
                                showItemMenu = false
                                onRenameBoardRequest(board)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Thay đổi hình nền") },
                            onClick = {
                                // Xử lý thay đổi hình nền
                                showItemMenu = false
                                onChangeBackgroundRequest(board)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Xóa bảng", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                // Xử lý xóa bảng (cần có xác nhận)
                                showItemMenu = false
                                onDeleteBoardRequest(board)
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
            // Icon(
            //     painter = painterResource(id = R.drawable.ic_empty_board), // Thay bằng icon của bạn
            //     contentDescription = "Không có bảng nào",
            //     modifier = Modifier.size(120.dp),
            //     tint = MaterialTheme.colorScheme.onSurfaceVariant
            // )
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


// --- Preview ---
@Preview(showBackground = true, name = "Boards Screen - With Data")
@Composable
fun BoardsScreenPreview() {
    val sampleBoards = listOf(
        Board("1", "Dự án cá nhân", backgroundImage = "https://picsum.photos/seed/projectA/600/400", backgroundColor = Color(0xFF4CAF50)),
        Board("2", "Kế hoạch du lịch hè 2025", backgroundColor = Color(0xFF2196F3)),
        Board("3", "Học Jetpack Compose", backgroundImage = "https://picsum.photos/seed/compose/600/400", backgroundColor = Color(0xFFFFC107)),
        Board("4", "Công việc công ty", backgroundColor = Color(0xFF9C27B0))
    )
    MaterialTheme { // Cần có MaterialTheme để preview hoạt động đúng
        BoardsScreen(
            boards = sampleBoards,
            onBoardClick = {},
            onAddBoardClick = {},
            onSearchClick = {},
            onRenameBoardRequest ={},
            onChangeBackgroundRequest ={},
            onDeleteBoardRequest ={},
        )
    }
}

@Preview(showBackground = true, name = "Boards Screen - Empty")
@Composable
fun EmptyBoardsScreenPreview() {
    MaterialTheme {
        BoardsScreen(
            boards = emptyList(),
            onBoardClick = {},
            onAddBoardClick = {},
            onSearchClick = {},
            onRenameBoardRequest ={},
            onChangeBackgroundRequest ={},
            onDeleteBoardRequest ={},
        )
    }
}

@Preview(showBackground = true, name = "Board Item Preview")
@Composable
fun BoardItemPreview() {
    MaterialTheme {
        BoardItem(
            board = Board("1", "Dự án phát triển App Siêu Tốc Độ Cao", backgroundImage = "https://picsum.photos/seed/preview/600/400"),
            onClick = {},
            onRenameBoardRequest ={},
            onChangeBackgroundRequest ={},
            onDeleteBoardRequest ={},
        )
    }
}