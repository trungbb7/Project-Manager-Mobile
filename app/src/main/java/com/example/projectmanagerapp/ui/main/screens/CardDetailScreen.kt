package com.example.projectmanagerapp.ui.main.screens


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.projectmanagerapp.ui.main.Checklist
import com.example.projectmanagerapp.ui.main.ChecklistItem
import com.example.projectmanagerapp.ui.main.Comment
import com.example.projectmanagerapp.utils.Utils
import com.example.projectmanagerapp.viewmodels.CardDetailViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    viewModel: CardDetailViewModel,
    listName: String,
    boardName: String,
    onNavigateBack: () -> Unit,
    onUpdateCardTitle: (newTitle: String) -> Unit,
    onUpdateCardDescription: (newDescription: String?) -> Unit,
    onSetDueDate: (timestamp: Long?) -> Unit,
    onAddChecklist: (checklistTitle: String) -> Unit,
    onUpdateChecklistTitle: (checklistId: String, newTitle: String) -> Unit,
    onDeleteChecklist: (checklistId: String) -> Unit,
    onAddChecklistItem: (checklistId: String, itemText: String) -> Unit,
    onUpdateChecklistItem: (checklistId: String, itemId: String, newText: String, isChecked: Boolean) -> Unit,
    onDeleteChecklistItem: (checklistId: String, itemId: String) -> Unit,
    onAddComment: (text: String) -> Unit,
    onDeleteCard: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val boardName = uiState.boardName
    val listName = uiState.listName
    val card = uiState.card
    if(card == null){
        Log.d("CardDetailScreen", "Card is null")
        return
    }else {
        Log.d("CardDetailScreen", "Card: $card")
    }
    val comments = uiState.comments
    val checklists = uiState.checklists
    val isLoading = uiState.isLoading
    val error = uiState.error



    var editingTitle by remember { mutableStateOf(false) }
    var currentTitle by remember(card.title) { mutableStateOf(card.title) }

    var editingDescription by remember { mutableStateOf(false) }
    var currentDescription by remember(card.description) { mutableStateOf(card.description ?: "") }

    var showAddChecklistDialog by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val titleFocusRequester = remember { FocusRequester() }

    // --- State cho DatePickerDialog ---
    var showDatePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(message = error)
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }


    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = card.dueDate,
            initialDisplayedMonthMillis = card.dueDate ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setDueDate(datePickerState.selectedDateMillis)
                        showDatePickerDialog = false
                    }
                ) { Text("Chọn") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (editingTitle) {
                        OutlinedTextField(
                            value = currentTitle,
                            onValueChange = { currentTitle = it },
                            label = { Text("Tiêu đề thẻ") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(titleFocusRequester),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (currentTitle.isNotBlank() && currentTitle != card.title) {
                                    viewModel.updateCardTitle(currentTitle)
                                }
                                editingTitle = false
                                keyboardController?.hide()
                            }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = LocalContentColor.current,
                                unfocusedTextColor = LocalContentColor.current
                            )
                        )
                        LaunchedEffect(Unit) {
                            titleFocusRequester.requestFocus()
                        }
                    } else {
                        Text(
                            text = card.title,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.clickable {
                                currentTitle = card.title
                                editingTitle = true
                            }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (editingTitle) {
                            editingTitle = false
                            currentTitle = card.title
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    if (editingTitle) {
                        IconButton(onClick = {
                            currentTitle = card.title
                            editingTitle = false
                            keyboardController?.hide()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Hủy sửa tiêu đề")
                        }
                        IconButton(onClick = {
                            if (currentTitle.isNotBlank() && currentTitle != card.title) {
                                viewModel.updateCardTitle(currentTitle)
                            }
                            editingTitle = false
                            keyboardController?.hide()
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = "Lưu tiêu đề")
                        }
                    } else {
                        IconButton(onClick = {
                            currentTitle = card.title
                            editingTitle = true
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Sửa tiêu đề")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CardDetailSection(icon = Icons.Filled.List, title = "Trong danh sách") {
                    Text(listName, style = MaterialTheme.typography.bodyLarge)
                }
            }
            item {
                CardDetailSection(icon = Icons.Filled.Menu, title = "Trên bảng") {
                    Text(boardName, style = MaterialTheme.typography.bodyLarge)
                }
            }

            item {
                CardDetailEditableSection(
                    icon = Icons.Filled.Info,
                    title = "Mô tả",
                    isEditing = editingDescription,
                    onEditToggle = {
                        currentDescription = card.description ?: ""
                        editingDescription = !editingDescription
                    }
                ) {
                    if (editingDescription) {
                        OutlinedTextField(
                            value = currentDescription,
                            onValueChange = { currentDescription = it },
                            label = { Text("Thêm mô tả chi tiết...") },
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                if (currentDescription != (card.description ?: "")) {
                                    viewModel.updateCardDescription(currentDescription.ifBlank { null })

                                }
                                editingDescription = false
                                keyboardController?.hide()
                            })
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = {
                                currentDescription = card.description ?: ""
                                editingDescription = false
                            }) { Text("Hủy") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                if (currentDescription != (card.description ?: "")) {
                                    viewModel.updateCardDescription(currentDescription.ifBlank { null })
                                }
                                editingDescription = false
                            }) { Text("Lưu") }
                        }

                    } else {
                        Text(
                            text = card.description ?: "Chưa có mô tả.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontStyle = if (card.description == null) FontStyle.Italic else FontStyle.Normal
                            ),
                            modifier = Modifier.clickable {
                                currentDescription = card.description ?: ""
                                editingDescription = true
                            }
                        )
                    }
                }
            }

            item {
                CardDetailSection(icon = Icons.Filled.DateRange, title = "Ngày hết hạn") {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            Utils.formatDueDate(card.dueDate),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (card.dueDate != null && (card.dueDate ?: 0) < System.currentTimeMillis()) MaterialTheme.colorScheme.error else LocalContentColor.current,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { showDatePickerDialog = true }) {
                            Text(if (card.dueDate == null) "THÊM" else "THAY ĐỔI")
                        }
                        if (card.dueDate != null) {
                            IconButton(onClick = { viewModel.setDueDate(null) }) {
                                Icon(Icons.Filled.Close, "Xóa ngày hết hạn", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            item {
                CardDetailSection(icon = Icons.Filled.CheckCircle, title = "Checklist") {
                    Button(onClick = { showAddChecklistDialog = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Add, contentDescription = "Thêm checklist")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Thêm Checklist")
                    }
                }
            }
            checklists.forEach { checklist ->
                item(key = "checklist_${checklist.id}") {
                    ChecklistSectionView(
                        checklist = checklist,
                        onUpdateTitle = { newTitle ->
                            viewModel.updateCheckListTitle(newTitle, checklist.id) },
                        onDelete = { viewModel.deleteCheckList(checklist.id) },
                        onAddItem = { itemText ->
                            viewModel.addCheckListItem(checklist.id, itemText) },
                        onUpdateItem = { itemId, newText, isChecked ->
                            viewModel.updateCheckListItem(checklist.id, itemId, newText, isChecked) },
                        onDeleteItem = { itemId ->
                            viewModel.deleteCheckListItem(checklist.id, itemId) }
                    )
                }
            }

            item {
                CardDetailSection(icon = Icons.Filled.MailOutline, title = "Bình luận") {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        label = { Text("Viết bình luận...") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (newCommentText.isNotBlank()) {
                                        viewModel.addComment(newCommentText)
                                        newCommentText = ""
                                        keyboardController?.hide()
                                    }
                                },
                                enabled = newCommentText.isNotBlank()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, "Gửi bình luận")
                            }
                        }
                    )
                }
            }
            items(comments.sortedByDescending { it.timestamp }, key = { "comment_${it.id}" }) { comment ->
                CommentItemView(comment)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Bạn có chắc muốn xóa thẻ này?",
                                actionLabel = "Xóa",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                onDeleteCard()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xóa thẻ này")
                }
            }
        }
    }

    if (showAddChecklistDialog) {
        AddChecklistDialog(
            onDismiss = { showAddChecklistDialog = false },
            onConfirm = { title ->
                viewModel.addCheckList(title)
                showAddChecklistDialog = false
            }
        )
    }
}

@Composable
fun CardDetailSection(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(start = 28.dp), content = content)
    }
}

@Composable
fun CardDetailEditableSection(
    icon: ImageVector,
    title: String,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            if (!isEditing) { // Chỉ hiển thị nút sửa khi không ở chế độ sửa
                TextButton(onClick = onEditToggle) {
                    Text("SỬA")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.padding(start = if(isEditing) 0.dp else 28.dp), content = content)
    }
}

@Composable
fun AddChecklistDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo Checklist mới") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tiêu đề Checklist") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title) },
                enabled = title.isNotBlank()
            ) { Text("Tạo") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@Composable
fun ChecklistSectionView(
    checklist: Checklist,
    onUpdateTitle: (newTitle: String) -> Unit,
    onDelete: () -> Unit,
    onAddItem: (itemText: String) -> Unit,
    onUpdateItem: (itemId: String, newText: String, isChecked: Boolean) -> Unit,
    onDeleteItem: (itemId: String) -> Unit
) {
    var editingChecklistTitle by remember { mutableStateOf(false) }
    var currentChecklistTitle by remember { mutableStateOf(checklist.title) }
    var newItemText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (editingChecklistTitle) {
                    OutlinedTextField(
                        value = currentChecklistTitle,
                        onValueChange = { currentChecklistTitle = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardActions = KeyboardActions(onDone = {
                            if(currentChecklistTitle.isNotBlank()) onUpdateTitle(currentChecklistTitle)
                            editingChecklistTitle = false
                        })
                    )
                    IconButton(onClick = {
                        if(currentChecklistTitle.isNotBlank()) onUpdateTitle(currentChecklistTitle)
                        editingChecklistTitle = false
                    }) { Icon(Icons.Filled.Edit, "Lưu tiêu đề checklist") }
                } else {
                    Text(checklist.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).clickable { editingChecklistTitle = true })
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.Delete, "Xóa checklist", tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            val checkedItems = checklist.items.count { it.isChecked }
            val totalItems = checklist.items.size
            if (totalItems > 0) {
                LinearProgressIndicator(
                    progress = { checkedItems.toFloat() / totalItems.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("$checkedItems/$totalItems hoàn thành", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
            }


            checklist.items.forEach { item ->
                ChecklistItemView(
                    item = item,
                    onUpdate = { newText, isChecked -> onUpdateItem(item.id, newText, isChecked) },
                    onDelete = { onDeleteItem(item.id) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Thêm item mới
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newItemText,
                    onValueChange = { newItemText = it },
                    label = { Text("Thêm mục...") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (newItemText.isNotBlank()) {
                            onAddItem(newItemText)
                            newItemText = ""
                            keyboardController?.hide()
                        }
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    if (newItemText.isNotBlank()) {
                        onAddItem(newItemText)
                        newItemText = ""
                    }
                }, enabled = newItemText.isNotBlank()) {
                    Icon(Icons.Filled.Add, "Thêm mục vào checklist")
                }
            }
        }
    }
}

@Composable
fun ChecklistItemView(
    item: ChecklistItem,
    onUpdate: (newText: String, isChecked: Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var editingItemText by remember { mutableStateOf(false) }
    var currentItemText by remember { mutableStateOf(item.text) }
    val focusRequester = remember { FocusRequester() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!editingItemText) onUpdate(item.text, !item.isChecked) }
            .padding(vertical = 2.dp)
    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { isChecked -> onUpdate(item.text, isChecked) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (editingItemText) {
            OutlinedTextField(
                value = currentItemText,
                onValueChange = { currentItemText = it },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardActions = KeyboardActions(onDone = {
                    if (currentItemText.isNotBlank()) onUpdate(currentItemText, item.isChecked)
                    editingItemText = false
                })
            )
            LaunchedEffect(Unit) { focusRequester.requestFocus() }
        } else {
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else LocalContentColor.current,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (editingItemText) {
            IconButton(onClick = {
                if (currentItemText.isNotBlank()) onUpdate(currentItemText, item.isChecked)
                editingItemText = !editingItemText },
                modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Check, "Lưu mục", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }else {

            IconButton(
                onClick = {
                    editingItemText = !editingItemText
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Filled.Edit,
                    "Sửa mục",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Close, "Xóa mục", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun CommentItemView(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.authorName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.timestamp.toString(), style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
