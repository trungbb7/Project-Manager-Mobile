import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.projectmanagerapp.ui.main.User
import com.example.projectmanagerapp.viewmodels.AddMemberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    viewModel: AddMemberViewModel,
    onNavigateBack: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsState()
    val query = uiState.value.searchQuery
    val results = uiState.value.searchResults
    val isLoading = uiState.value.isLoading
    var lastAddedUserId = remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Thêm thành viên") }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, null
                )
            }
        })
    }) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {
            Text("Mời thành viên mới vào bảng bằng email của họ.")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = { Text("Email của người dùng") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(results) { user ->
                        UserSearchResultItem(
                            user = user,
                            isAlreadyAdded = lastAddedUserId.value == user.uid, // Để hiển thị trạng thái "Đã thêm"
                            onAddClick = {
                                viewModel.addMemberToBoard(user.uid)
                                lastAddedUserId.value = user.uid // Lưu lại ID người vừa thêm
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserSearchResultItem(user: User, isAlreadyAdded: Boolean, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.photoUrl,
            contentDescription = "Ảnh nền xem trước",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.displayName, fontWeight = FontWeight.Bold)
            Text(user.email, style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = onAddClick, enabled = !isAlreadyAdded) {
            if (isAlreadyAdded) Text("Đã thêm") else Text("Thêm")
        }
    }
}