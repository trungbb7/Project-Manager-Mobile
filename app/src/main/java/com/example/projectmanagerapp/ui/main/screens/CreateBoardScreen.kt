package com.example.projectmanagerapp.ui.main.screens



import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.core.graphics.toColorInt
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.ui.main.viewmodels.CreateBoardViewModel
import com.example.projectmanagerapp.utils.BackgroundType
import com.example.projectmanagerapp.utils.Constants


data class BackgroundImageOption(val id: String, val smallUrl: String, val fullUrl: String, val photographer: String)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBoardScreen(
    viewModel: CreateBoardViewModel,
    onNavigateBack: () -> Unit,
    onBoardCreatedSuccessfully: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Launcher để chọn ảnh ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->

            viewModel.onImageSelected(uri)
        }
    )

    var backgroundType: BackgroundType by remember { mutableStateOf(BackgroundType.COLOR) }

    // Tự động điều hướng khi tạo bảng thành công
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onBoardCreatedSuccessfully()
        }
    }

    // Hiển thị lỗi nếu có
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            snackbarHostState.showSnackbar(uiState.error!!)
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tạo bảng mới") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
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
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).padding(bottom = 16.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val selectedUri = uiState.selectedImageUri
                    if (selectedUri != null) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = "Ảnh nền xem trước",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                    } else {
                        // Hiển thị màu nền đã chọn
                        Spacer(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(uiState.selectedBackgroundColor.toColorInt()))
                        )
                    }
                    Text(
                        text = if (uiState.boardName.isNotBlank()) uiState.boardName else "Tên bảng",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // --- Tên Bảng ---
            OutlinedTextField(
                value = uiState.boardName,
                onValueChange = { viewModel.onBoardNameChange(it) },
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


            when(backgroundType) {
                BackgroundType.COLOR -> {
                    Text("Chọn màu nền:", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(Constants.predefinedBackgroundColors) { color ->
                            ColorPickerItem(
                                color = color,
                                isSelected = uiState.selectedBackgroundColor == color && backgroundType == BackgroundType.COLOR,
                                onClick = {
                                    viewModel.onBackgroundColorChange(color.toString())
                                    backgroundType = BackgroundType.COLOR
                                }
                            )
                        }
                    }
                }

                BackgroundType.IMAGE -> {
                    // --- Nút chọn ảnh ---
                    Button(
                        onClick = {
                            // Mở Photo Picker của Android
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(painterResource(id = R.drawable.outline_image_24), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chọn ảnh nền từ thư viện")
                    }
                }
            }




            Spacer(modifier = Modifier.weight(1f))

            // --- Nút Tạo Bảng ---
            Button(
                onClick = { viewModel.createBoard() },
                enabled = uiState.boardName.isNotBlank() && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Tạo bảng")
                }
            }
        }
    }
}

@Composable
fun ColorPickerItem(
    color: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(color.toColorInt()))
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier
            )
    )
}
