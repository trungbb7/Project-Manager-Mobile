package com.example.projectmanagerapp.ui.main.screens

import android.util.Log
import androidx.compose.foundation.lazy.items


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.core.graphics.toColorInt
import coil.request.ImageRequest
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.ui.main.viewmodels.EditBoardViewModel
import com.example.projectmanagerapp.utils.BackgroundType
import com.example.projectmanagerapp.utils.Constants


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBoardScreen(
    viewModel: EditBoardViewModel,
    onNavigateBack: () -> Unit,
    onBoardUpdatedSuccessfully: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Launcher để chọn ảnh ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->

            viewModel.onImageSelected(uri)
        }
    )

    val backgroundImageUri = uiState.selectedImageUri

    val boardName = uiState.boardName
    val prevBoardName = uiState.prevBoardName


    var backgroundType = uiState.backGroundType


    // Tự động điều hướng khi tạo bảng thành công
    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onBoardUpdatedSuccessfully()
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
                title = { Text("Cập nhật bảng") },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(bottom = 16.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (backgroundType == BackgroundType.IMAGE) {
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
                        } else if (uiState.prevBackgroundImage != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uiState.prevBackgroundImage)
                                    .crossfade(true)
                                    // .placeholder(R.drawable.placeholder_board_background) // Ảnh chờ
                                    // .error(R.drawable.error_board_background) // Ảnh lỗi
                                    .build(),
                                contentDescription = "Ảnh nền xem trước",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f))
                            )
                        }
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
                    onClick = { viewModel.onBackgroundTypeChange(BackgroundType.COLOR) },
                    text = { Text("Màu nền") }
                )
                Tab(
                    selected = backgroundType == BackgroundType.IMAGE,
                    onClick = { viewModel.onBackgroundTypeChange(BackgroundType.IMAGE) },
                    text = { Text("Ảnh nền") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            when (backgroundType) {
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
                                    viewModel.onBackgroundTypeChange(BackgroundType.COLOR)
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
                        Icon(
                            painterResource(id = R.drawable.outline_image_24),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chọn ảnh nền từ thư viện")
                    }
                }
            }




            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.updateBoard() },
                enabled = uiState.boardName.isNotBlank()
                        && !uiState.isLoading
                        && (uiState.selectedImageUri != null || uiState.selectedBackgroundColor.isNotBlank() || boardName != prevBoardName),
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
                    Text("Cập nhật")
                }
            }
        }
    }
}
