package com.example.projectmanagerapp.ui.profile

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.projectmanagerapp.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ người dùng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Edit profile */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            user?.let { userData ->
                UserProfileContent(
                    user = userData,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không thể tải thông tin người dùng")
                }
            }
        }
    }
}

@Composable
private fun UserProfileContent(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        ProfileHeader(user = user)
        
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
        
        // Work Info
        if (user.department.isNotBlank() || user.position.isNotBlank()) {
            ProfileSection(
                title = "Thông tin công việc",
                icon = Icons.Default.Work
            ) {
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
        
        // Skills
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
        
        // Bio
        if (user.bio.isNotBlank()) {
            ProfileSection(
                title = "Giới thiệu",
                icon = Icons.Default.Description
            ) {
                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            AsyncImage(
                model = user.photoUrl.ifBlank { "https://via.placeholder.com/120" },
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Name
            Text(
                text = user.displayName.ifBlank { "Người dùng" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
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
