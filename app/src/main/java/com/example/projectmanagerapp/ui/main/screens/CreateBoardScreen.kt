package com.example.projectmanagerapp.ui.main.screens


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.ui.main.UnsplashPhoto
import com.example.projectmanagerapp.utils.BackgroundType
import com.example.projectmanagerapp.utils.Constants
import com.example.projectmanagerapp.viewmodels.CreateBoardViewModel


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

    val backgroundType = uiState.backgroundType

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
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(bottom = 16.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val selectedUri = uiState.selectedImageUri
                    if (backgroundType == BackgroundType.IMAGE) {
                        AsyncImage(
                            model = selectedUri ?: uiState.selectedImageUrl,
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
                    onClick = { viewModel.onChangeBackgroundType(BackgroundType.COLOR) },
                    text = { Text("Màu nền") }
                )
                Tab(
                    selected = backgroundType == BackgroundType.IMAGE,
                    onClick = { viewModel.onChangeBackgroundType(BackgroundType.IMAGE) },
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
                                isSelected = uiState.selectedBackgroundColor == color,
                                onClick = {
                                    viewModel.onBackgroundColorChange(color.toString())
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        items(uiState.backgroundImageUrls) { photo ->
                            ImagePickerItem(
                                photo = photo,
//                                isSelected = selectedBackgroundImage?.id == imageOption.id && backgroundType == BackgroundType.IMAGE,
                                isSelected = uiState.selectedImageUrl == photo.urls.regular,
                                onClick = {
                                    viewModel.onBackgroundUrlSelected(photo.urls.regular)
//                                    selectedBackgroundImage = imageOption
//                                    backgroundType = BackgroundType.IMAGE
                                }
                            )
                        }
                    }

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
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                ) else Modifier
            )
    )
}


@Composable
fun ImagePickerItem(
    photo: UnsplashPhoto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 80.dp, height = 60.dp)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(
                    3.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(8.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.urls.regular)
                .crossfade(true)
                .build(),
            contentDescription = "Tùy chọn ảnh nền",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


