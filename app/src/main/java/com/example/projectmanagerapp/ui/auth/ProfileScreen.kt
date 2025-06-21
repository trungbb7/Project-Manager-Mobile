package com.example.projectmanagerapp.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.data.model.User
import com.example.projectmanagerapp.ui.profile.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    onNavigateBack: () -> Unit = {},
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    val isScrolled = scrollState.value > 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = isScrolled,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text("Hồ sơ người dùng")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isScrolled)
                        MaterialTheme.colorScheme.surface
                    else
                        Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
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
                Box(modifier = Modifier.fillMaxSize()) {
                    // Modern gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Header with Avatar
                        ModernProfileHeader(userData)

                        // Content sections in a card
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .offset(y = (-20).dp),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Basic Info
                                ProfileSection(
                                    title = "Thông tin cơ bản",
                                    icon = Icons.Outlined.Person
                                ) {
                                    ProfileInfoItem(
                                        icon = Icons.Outlined.Email,
                                        label = "Email",
                                        value = userData.email
                                    )

                                    if (userData.phoneNumber.isNotBlank()) {
                                        ProfileInfoItem(
                                            icon = Icons.Outlined.Phone,
                                            label = "Số điện thoại",
                                            value = userData.phoneNumber
                                        )
                                    }
                                }

                                // Work Info
                                if (userData.department.isNotBlank() || userData.position.isNotBlank()) {
                                    ProfileSection(
                                        title = "Thông tin công việc",
                                        icon = Icons.Outlined.Work
                                    ) {
                                        if (userData.department.isNotBlank()) {
                                            ProfileInfoItem(
                                                icon = Icons.Outlined.Business,
                                                label = "Phòng ban",
                                                value = userData.department
                                            )
                                        }

                                        if (userData.position.isNotBlank()) {
                                            ProfileInfoItem(
                                                icon = Icons.Outlined.Badge,
                                                label = "Chức vụ",
                                                value = userData.position
                                            )
                                        }
                                    }
                                }

                                // Linked Accounts
                                if (userData.linkedProviders.isNotEmpty()) {
                                    ProfileSection(
                                        title = "Tài khoản liên kết",
                                        icon = Icons.Outlined.Link
                                    ) {
                                        LinkedAccountsChips(providers = userData.linkedProviders)
                                    }
                                }

                                // Skills section if available
                                if (userData.skills?.isNotEmpty() == true) {
                                    ProfileSection(
                                        title = "Kỹ năng",
                                        icon = Icons.Outlined.Star
                                    ) {
                                        SkillsChips(skills = userData.skills)
                                    }
                                }

                                // Bio section if available
                                if (userData.bio?.isNotBlank() == true) {
                                    ProfileSection(
                                        title = "Giới thiệu",
                                        icon = Icons.Outlined.Info
                                    ) {
                                        Text(
                                            text = userData.bio,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Outlined.PersonOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Không thể tải thông tin người dùng",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { viewModel.loadCurrentUser() }) {
                            Text("Thử lại")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernProfileHeader(userData: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture with shadow
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(4.dp, Color.White),
                    CircleShape
                )
                .padding(4.dp)
        ) {
            AsyncImage(
                model = userData.photoUrl.ifBlank { R.drawable.user_image_placeholder },
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            // Status indicator
            if (userData.isActive) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name with clear visibility
        Text(
            text = userData.displayName.ifBlank { "Người dùng" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Role if available
        if (userData.role.isNotBlank()) {
            Text(
                text = userData.role.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        content()
    }
}

@Composable
private fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun LinkedAccountsChips(providers: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        providers.forEach { provider ->
            SuggestionChip(
                onClick = { /* No action */ },
                label = { Text(provider) },
                icon = {
                    val icon = when {
                        provider.contains("google", ignoreCase = true) -> Icons.Outlined.Email
                        provider.contains("facebook", ignoreCase = true) -> Icons.Outlined.Chat
                        provider.contains("github", ignoreCase = true) -> Icons.Outlined.Code
                        provider.contains("twitter", ignoreCase = true) -> Icons.Outlined.Chat
                        else -> Icons.Outlined.Link
                    }
                    Icon(icon, contentDescription = null, Modifier.size(18.dp))
                }
            )
        }
    }
}

@Composable
private fun SkillsChips(skills: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        skills.forEach { skill ->
            AssistChip(
                onClick = { /* No action */ },
                label = { Text(skill) },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Star,
                        contentDescription = null,
                        Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}
