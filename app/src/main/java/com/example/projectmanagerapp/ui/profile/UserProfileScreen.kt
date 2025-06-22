package com.example.projectmanagerapp.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.data.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    // State for edited fields
    var displayName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf<Uri?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.setPhotoUri(it)
        }
    }

    // This effect now only handles the photo URI from the view model
    LaunchedEffect(viewModel.photoUri.collectAsState().value) {
        photoUrl = viewModel.photoUri.value
    }
    
    // Show snackbar on error
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Chỉnh sửa hồ sơ" else "Hồ sơ người dùng") },
                navigationIcon = {
                    if (isEditing) {
                        IconButton(onClick = {
                            isEditing = false
                            viewModel.clearPhotoUri()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Hủy")
                        }
                    } else {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                        }
                    }
                },
                actions = {
                    if (isEditing) {
                        Button(onClick = {
                            val updates = mutableMapOf<String, Any>()
                            if (displayName != user?.displayName) updates["displayName"] = displayName
                            if (phoneNumber != user?.phoneNumber) updates["phoneNumber"] = phoneNumber
                            if (location != user?.location) updates["location"] = location
                            if (department != user?.department) updates["department"] = department
                            if (position != user?.position) updates["position"] = position
                            if (bio != user?.bio) updates["bio"] = bio
                            
                            viewModel.saveProfile(updates, photoUrl)
                            isEditing = false
                        }) {
                            Text("Lưu")
                        }
                    } else {
                        IconButton(onClick = { 
                            isEditing = true 
                            user?.let {
                                displayName = it.displayName
                                phoneNumber = it.phoneNumber
                                location = it.location
                                department = it.department
                                position = it.position
                                bio = it.bio
                            }
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            user == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không thể tải thông tin người dùng")
                }
            }
            else -> {
                UserProfileContent(
                    user = user!!,
                    isEditing = isEditing,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    displayName = displayName,
                    onDisplayNameChange = { displayName = it },
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it },
                    location = location,
                    onLocationChange = { location = it },
                    department = department,
                    onDepartmentChange = { department = it },
                    position = position,
                    onPositionChange = { position = it },
                    bio = bio,
                    onBioChange = { bio = it },
                    photoUrl = photoUrl,
                    onImageClick = { imagePickerLauncher.launch("image/*") }
                )
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    user: User,
    isEditing: Boolean,
    modifier: Modifier = Modifier,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    department: String,
    onDepartmentChange: (String) -> Unit,
    position: String,
    onPositionChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    photoUrl: Uri?,
    onImageClick: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        ProfileHeader(
            user = user,
            isEditing = isEditing,
            displayName = displayName,
            onDisplayNameChange = onDisplayNameChange,
            photoUrl = photoUrl,
            onImageClick = onImageClick
        )
        
        // Basic Info
        ProfileSection(
            title = "Thông tin cơ bản",
            icon = Icons.Default.Person
        ) {
            ProfileInfoItem(
                label = "Email",
                value = user.email,
                icon = Icons.Default.Email
            )

            if (isEditing) {
                OutlinedTextField(value = phoneNumber, onValueChange = onPhoneNumberChange, label = { Text("Số điện thoại") })
                OutlinedTextField(value = location, onValueChange = onLocationChange, label = { Text("Địa điểm") })
            } else {
                if (user.phoneNumber.isNotBlank()) {
                    ProfileInfoItem(
                        label = "Số điện thoại",
                        value = user.phoneNumber,
                        icon = Icons.Default.Phone
                    )
                }
                if (user.location.isNotBlank()) {
                    ProfileInfoItem(
                        label = "Địa điểm",
                        value = user.location,
                        icon = Icons.Default.LocationOn
                    )
                }
            }
        }
        
        // Work Info
        ProfileSection(
            title = "Thông tin công việc",
            icon = Icons.Default.Work
        ) {
            if(isEditing){
                OutlinedTextField(value = department, onValueChange = onDepartmentChange, label = { Text("Phòng ban") })
                OutlinedTextField(value = position, onValueChange = onPositionChange, label = { Text("Chức vụ") })
            } else {
                if (user.department.isNotBlank()) {
                    ProfileInfoItem(
                        label = "Phòng ban",
                        value = user.department,
                        icon = Icons.Default.Business
                    )
                }
                if (user.position.isNotBlank()) {
                    ProfileInfoItem(
                        label = "Chức vụ",
                        value = user.position,
                        icon = Icons.Default.Badge
                    )
                }
            }
        }
        
        // Bio
        ProfileSection(
            title = "Giới thiệu",
            icon = Icons.Default.Description
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = bio,
                    onValueChange = onBioChange,
                    label = { Text("Giới thiệu") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp)
                )
            } else {
                if (user.bio.isNotBlank()) {
                    Text(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Chưa có giới thiệu.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
        
        // Skills
        if (!isEditing) {
            if (user.skills.isNotEmpty()) {
                ProfileSection(
                    title = "Kỹ năng",
                    icon = Icons.Default.Star
                ) {
                    SkillsChips(skills = user.skills)
                }
            }

            // Linked Providers
            if (user.linkedProviders.isNotEmpty()) {
                ProfileSection(
                    title = "Tài khoản liên kết",
                    icon = Icons.Default.Link
                ) {
                    LinkedProvidersChips(providers = user.linkedProviders)
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    user: User,
    isEditing: Boolean,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    photoUrl: Uri?,
    onImageClick: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(120.dp)) {
            AsyncImage(
                model = photoUrl ?: user.photoUrl.takeIf { it.isNotBlank() } ?: R.drawable.user_image_placeholder,
                contentDescription = "Ảnh đại diện",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(enabled = isEditing, onClick = onImageClick),
                contentScale = ContentScale.Crop
            )

            if (isEditing) {
                IconButton(
                    onClick = onImageClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = "Đổi ảnh",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        if (isEditing) {
            OutlinedTextField(
                value = displayName,
                onValueChange = onDisplayNameChange,
                label = { Text("Họ và tên") }
            )
        } else {
            Text(
                text = user.displayName.ifBlank { "Người dùng" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Role
        Text(
            text = user.role.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (user.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (user.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (user.isActive) "Hoạt động" else "Không hoạt động",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SkillsChips(skills: List<String>) {
    // TODO: Implement skills chips layout
    Text("Skills: ${skills.joinToString(", ")}")
}

@Composable
private fun LinkedProvidersChips(providers: List<String>) {
    // TODO: Implement providers chips layout
    Text("Linked: ${providers.joinToString(", ")}")
}
