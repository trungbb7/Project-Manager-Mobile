package com.example.projectmanagerapp.ui.main



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

// Dữ liệu mẫu cho màu nền
val predefinedBackgroundColors = listOf(
    Color(0xFF0079BF), // Trello Blue
    Color(0xFFD29034), // Orange
    Color(0xFF519839), // Green
    Color(0xFFB04632), // Red
    Color(0xFF89609E), // Purple
    Color(0xFFCD5A91), // Pink
    Color(0xFF4BBF6B), // Light Green
    Color(0xFF00AECC), // Teal
    Color(0xFF838C91)  // Gray
)

data class BackgroundImageOption(val id: String, val smallUrl: String, val fullUrl: String, val photographer: String)
val predefinedBackgroundImages = listOf(
    BackgroundImageOption("1", "https://images.unsplash.com/photo-1604147706283-d7119b5b822c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzMzA5fDB8MXxjb2xsZWN0aW9ufDF8fHx8fHx8fDE2MjA0ODQ0MjZ8&ixlib=rb-4.0.3&q=80&w=400", "https://images.unsplash.com/photo-1604147706283-d7119b5b822c?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=2000", "Scott Webb"),
    BackgroundImageOption("2", "https://images.unsplash.com/photo-1553095066-5014bc7b7f2d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzMzA5fDB8MXxjb2xsZWN0aW9ufDJ8fHx8fHx8fDE2MjA0ODQ0MjZ8&ixlib=rb-4.0.3&q=80&w=400", "https://images.unsplash.com/photo-1553095066-5014bc7b7f2d?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=2000", "Gradienta"),
    BackgroundImageOption("3", "https://images.unsplash.com/photo-1500964757637-c85e8a162699?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzMzA5fDB8MXxjb2xsZWN0aW9ufDR8fHx8fHx8fDE2MjA0ODQ0MjZ8&ixlib=rb-4.0.3&q=80&w=400", "https://images.unsplash.com/photo-1500964757637-c85e8a162699?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=2000", "eberhard grossgasteiger"),
    BackgroundImageOption("4", "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzMzA5fDB8MXxjb2xsZWN0aW9ufDE0fHx8fHx8fDE2MjA0ODQ0MjZ8&ixlib=rb-4.0.3&q=80&w=400", "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?ixlib=rb-4.0.3&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=2000", "Gradienta")
)

enum class BackgroundType {
    COLOR, IMAGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBoardScreen(
    onNavigateBack: () -> Unit,
    onCreateBoard: (boardName: String, backgroundColor: Color?, backgroundImageUri: String?) -> Unit
) {
    var boardName by remember { mutableStateOf("") }
    var selectedBackgroundColor by remember { mutableStateOf(predefinedBackgroundColors.first()) }
    var selectedBackgroundImage by remember { mutableStateOf<BackgroundImageOption?>(null) }
    var backgroundType by remember { mutableStateOf(BackgroundType.COLOR) }

    val isCreateButtonEnabled = boardName.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo bảng mới") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Preview Bảng ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f) // Tỷ lệ phổ biến cho preview
                    .padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (backgroundType == BackgroundType.IMAGE && selectedBackgroundImage != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedBackgroundImage!!.smallUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Ảnh nền xem trước",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Lớp phủ mờ để dễ đọc chữ trên ảnh
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                    } else {
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(selectedBackgroundColor)
                        )
                    }
                    Text(
                        text = if (boardName.isNotBlank()) boardName else "Tên bảng",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // --- Tên Bảng ---
            OutlinedTextField(
                value = boardName,
                onValueChange = { boardName = it },
                label = { Text("Tên bảng") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Chọn loại nền ---
            TabRow(selectedTabIndex = if (backgroundType == BackgroundType.COLOR) 0 else 1) {
                Tab(
                    selected = backgroundType == BackgroundType.COLOR,
                    onClick = { backgroundType = BackgroundType.COLOR },
                    text = { Text("Màu nền") }
                )
                Tab(
                    selected = backgroundType == BackgroundType.IMAGE,
                    onClick = { backgroundType = BackgroundType.IMAGE },
                    text = { Text("Ảnh nền") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Chọn Màu Nền hoặc Ảnh Nền ---
            when (backgroundType) {
                BackgroundType.COLOR -> {
                    Text("Chọn màu nền:", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(predefinedBackgroundColors) { color ->
                            ColorPickerItem(
                                color = color,
                                isSelected = selectedBackgroundColor == color && backgroundType == BackgroundType.COLOR,
                                onClick = {
                                    selectedBackgroundColor = color
                                    backgroundType = BackgroundType.COLOR
                                }
                            )
                        }
                    }
                }
                BackgroundType.IMAGE -> {
                    Text("Chọn ảnh nền:", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(predefinedBackgroundImages) { imageOption ->
                            ImagePickerItem(
                                imageOption = imageOption,
                                isSelected = selectedBackgroundImage?.id == imageOption.id && backgroundType == BackgroundType.IMAGE,
                                onClick = {
                                    selectedBackgroundImage = imageOption
                                    backgroundType = BackgroundType.IMAGE
                                }
                            )
                        }
//                        TODO: Upload Image Button
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Đẩy nút Tạo xuống dưới

            // --- Nút Tạo Bảng ---
            Button(
                onClick = {
                    if (isCreateButtonEnabled) {
                        val bgColor = if (backgroundType == BackgroundType.COLOR) selectedBackgroundColor else null
                        val bgImage = if (backgroundType == BackgroundType.IMAGE) selectedBackgroundImage?.fullUrl else null
                        onCreateBoard(boardName, bgColor, bgImage)
                    }
                },
                enabled = isCreateButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Tạo bảng")
            }
        }
    }
}

@Composable
fun ColorPickerItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier
            )
    )
}

@Composable
fun ImagePickerItem(
    imageOption: BackgroundImageOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 80.dp, height = 60.dp)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)) else Modifier
            ),
        shape = RoundedCornerShape(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageOption.smallUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Tùy chọn ảnh nền ${imageOption.photographer}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Preview(showBackground = true, name = "Create Board Screen")
@Composable
fun CreateBoardScreenPreview() {
    MaterialTheme { // Cần MaterialTheme để preview đúng
        CreateBoardScreen(
            onNavigateBack = {},
            onCreateBoard = { name, color, imageUri ->
                println("Board Created: Name=$name, Color=$color, ImageUri=$imageUri")
            }
        )
    }
}

@Preview(showBackground = true, name = "Create Board Screen - Dark Theme")
@Composable
fun CreateBoardScreenDarkPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) { // Sử dụng darkColorScheme
        CreateBoardScreen(
            onNavigateBack = {},
            onCreateBoard = { name, color, imageUri ->
                println("Board Created: Name=$name, Color=$color, ImageUri=$imageUri")
            }
        )
    }
}

@Preview(showBackground = true, name = "Color Picker Item Selected")
@Composable
fun ColorPickerItemSelectedPreview() {
    MaterialTheme {
        ColorPickerItem(color = Color.Blue, isSelected = true, onClick = {})
    }
}

@Preview(showBackground = true, name = "Color Picker Item Not Selected")
@Composable
fun ColorPickerItemNotSelectedPreview() {
    MaterialTheme {
        ColorPickerItem(color = Color.Green, isSelected = false, onClick = {})
    }
}

@Preview(showBackground = true, name = "Image Picker Item Selected")
@Composable
fun ImagePickerItemSelectedPreview() {
    MaterialTheme {
        ImagePickerItem(imageOption = predefinedBackgroundImages.first(), isSelected = true, onClick = {})
    }
}