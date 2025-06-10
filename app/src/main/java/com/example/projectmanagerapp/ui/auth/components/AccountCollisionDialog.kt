package com.example.projectmanagerapp.ui.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AccountCollisionDialog(
    email: String,
    existingProvider: String,
    newProvider: String,
    onLinkAccount: () -> Unit,
    onCreateNew: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Warning Icon
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.warning
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = "Email đã được sử dụng",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                Text(
                    text = "Email $email đã được đăng ký với $existingProvider.\n\nBạn muốn:",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Option 1: Link Account
                AccountOptionButton(
                    icon = Icons.Default.Link,
                    title = "Liên kết tài khoản",
                    description = "Liên kết $newProvider với tài khoản $existingProvider hiện tại",
                    onClick = onLinkAccount
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Option 2: Create New
                AccountOptionButton(
                    icon = Icons.Default.PersonAdd,
                    title = "Tạo tài khoản mới",
                    description = "Tạo tài khoản mới với $newProvider (sử dụng email khác)",
                    onClick = onCreateNew
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hủy")
                }
            }
        }
    }
}

@Composable
private fun AccountOptionButton(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Extension property for warning color
val ColorScheme.warning: androidx.compose.ui.graphics.Color
    get() = androidx.compose.ui.graphics.Color(0xFFFF9800)
