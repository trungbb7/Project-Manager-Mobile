package com.example.projectmanagerapp.ui.main.screens


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.ui.main.CardLocation
import com.example.projectmanagerapp.ui.main.Checklist
import com.example.projectmanagerapp.ui.main.ChecklistItem
import com.example.projectmanagerapp.ui.main.Comment
import com.example.projectmanagerapp.ui.main.User
import com.example.projectmanagerapp.utils.AppDestinations
import com.example.projectmanagerapp.utils.Utils
import com.example.projectmanagerapp.viewmodels.CardDetailViewModel
import com.example.projectmanagerapp.viewmodels.NavigationEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    viewModel: CardDetailViewModel,
    navController: NavController,
    onNavigateBack: () -> Unit,

    ) {

    val uiState by viewModel.uiState.collectAsState()
    val boardName = uiState.boardName
    val listName = uiState.listName
    val card = uiState.card
    if (card == null) {
        Log.d("CardDetailScreen", "Card is null")
        return
    } else {
        Log.d("CardDetailScreen", "Card: $card")
    }
    val comments = uiState.comments
    val checklists = uiState.checklists
    val isLoading = uiState.isLoading
    val error = uiState.error




    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<CardLocation>("picked_location")?.observeForever { location ->
            // Khi có kết quả, gọi ViewModel để lưu
            viewModel.updateCardLocation(location)
            // Xóa kết quả khỏi state handle để không bị gọi lại
            savedStateHandle.remove<CardLocation>("picked_location")
        }
    }


    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                NavigationEvent.NavigationBack -> {
                    onNavigateBack()
                }
            }
        }
    }


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

    var showAssignMemberDialog by remember { mutableStateOf(false) }

    // --- State cho DatePickerDialog ---
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(LocalTime.now()) }
    var selectedEpochMilli by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    )

//    var showDatePickerDialog by remember { mutableStateOf(false) }

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


    if (showDatePicker) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = card.dueDate,
            initialDisplayedMonthMillis = card.dueDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
//                        viewModel.setDueDate(datePickerState.selectedDateMillis)
//                        showDatePicker = false
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            selectedDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                        showTimePicker = true
                    }
                ) { Text("Chọn") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            modifier = Modifier.fillMaxWidth(),
            title = { Text("Chọn giờ") },
            text = { Box(contentAlignment = Alignment.Center) { TimePicker(state = timePickerState) } },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false

                        if (selectedDate != null && selectedTime != null) {
                            val localDateTime = LocalDateTime.of(selectedDate!!, selectedTime!!)

                            val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())

                            val epochMilli = zonedDateTime.toInstant().toEpochMilli()

                            selectedEpochMilli = epochMilli
                            viewModel.setDueDate(selectedEpochMilli)
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } }
        )
    }

    if (showAssignMemberDialog) {
        AssignMemberDialog(
            boardMembers = uiState.boardMembers,
            assignedMemberIds = uiState.card!!.assignedMemberIds,
            onAssign = { userId ->
                viewModel.assignMember(userId)
            },
            onUnassign = { userId ->
                viewModel.unAssignMember(userId)
            },
            onDismiss = {
                showAssignMemberDialog = false
            }
        )
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
                CardDetailSection(icon = Icons.Filled.Person, title = "Thành viên") {
                    val assignedMembers = uiState.assignedMembers

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        assignedMembers.forEach { member ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(member.photoUrl)
                                    .crossfade(true)
                                    .placeholder(R.drawable.user_image_placeholder) // Ảnh chờ
                                    .error(R.drawable.user_image_placeholder) // Ảnh lỗi
                                    .build(),
                                contentDescription = "Ảnh đại diện",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(
                                        BorderStroke(2.dp, MaterialTheme.colorScheme.surface),
                                        CircleShape
                                    )
                            )
                        }

                        IconButton(onClick = { showAssignMemberDialog = true }) {
                            Icon(Icons.Filled.AddCircle, "Gán thành viên")
                        }
                    }
                }
            }

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
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 100.dp),
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
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            Utils.formatDueDate(card.dueDate),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (card.dueDate != null && (card.dueDate
                                    ?: 0) < System.currentTimeMillis()
                            ) MaterialTheme.colorScheme.error else LocalContentColor.current,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(if (card.dueDate == null) "THÊM" else "THAY ĐỔI")
                        }
                        if (card.dueDate != null) {
                            IconButton(onClick = { viewModel.setDueDate(null) }) {
                                Icon(
                                    Icons.Filled.Close,
                                    "Xóa ngày hết hạn",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            item {
                CardDetailSection(icon = Icons.Default.LocationOn, title = "Vị trí") {
                    val cardLocation = uiState.card?.location
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Điều hướng đến màn hình chọn vị trí
//                                navController.navigate("map_picker/${card.id}")
                                navController.navigate(AppDestinations.MAP_PICKER_ROUTE)
                            }
                    ) {
                        if (cardLocation != null) {
                            // Hiển thị vị trí đã chọn
                            Text(
                                cardLocation.placeName ?: "Vị trí đã chọn",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(cardLocation.address, style = MaterialTheme.typography.bodyMedium)
                            // Nút xóa vị trí
                            TextButton(onClick = { viewModel.updateCardLocation(null) }) {
                                Text("Xóa vị trí", color = MaterialTheme.colorScheme.error)
                            }
                        } else {
                            // Hiển thị nút thêm vị trí
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = "Thêm vị trí")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thêm vị trí")
                            }
                        }
                    }
                }
            }

            item {
                CardDetailSection(icon = Icons.Filled.CheckCircle, title = "Checklist") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { showAddChecklistDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Thêm checklist")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thêm Checklist")
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        AiButton(onClick = {
                            viewModel.generateCheckList()
                        })
                    }
                }
            }
            checklists.forEach { checklist ->
                item(key = "checklist_${checklist.id}") {
                    ChecklistSectionView(
                        checklist = checklist,
                        onUpdateTitle = { newTitle ->
                            viewModel.updateCheckListTitle(newTitle, checklist.id)
                        },
                        onDelete = { viewModel.deleteCheckList(checklist.id) },
                        onAddItem = { itemText ->
                            viewModel.addCheckListItem(checklist.id, itemText)
                        },
                        onUpdateItem = { itemId, newText, isChecked ->
                            viewModel.updateCheckListItem(checklist.id, itemId, newText, isChecked)
                        },
                        onDeleteItem = { itemId ->
                            viewModel.deleteCheckListItem(checklist.id, itemId)
                        }
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
            items(
                comments.sortedByDescending { it.timestamp },
                key = { "comment_${it.id}" }) { comment ->
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
                                viewModel.deleteCard()
//                                onNavigateBack()
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
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
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
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (!isEditing) { // Chỉ hiển thị nút sửa khi không ở chế độ sửa
                TextButton(onClick = onEditToggle) {
                    Text("SỬA")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.padding(start = if (isEditing) 0.dp else 28.dp),
            content = content
        )
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
                            if (currentChecklistTitle.isNotBlank()) onUpdateTitle(
                                currentChecklistTitle
                            )
                            editingChecklistTitle = false
                        })
                    )
                    IconButton(onClick = {
                        if (currentChecklistTitle.isNotBlank()) onUpdateTitle(currentChecklistTitle)
                        editingChecklistTitle = false
                    }) { Icon(Icons.Filled.Edit, "Lưu tiêu đề checklist") }
                } else {
                    Text(
                        checklist.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { editingChecklistTitle = true })
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Filled.Delete,
                        "Xóa checklist",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            val checkedItems = checklist.items.count { it.isChecked }
            val totalItems = checklist.items.size
            if (totalItems > 0) {
                LinearProgressIndicator(
                    progress = { checkedItems.toFloat() / totalItems.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$checkedItems/$totalItems hoàn thành",
                    style = MaterialTheme.typography.labelSmall
                )
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
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
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
            IconButton(
                onClick = {
                    if (currentItemText.isNotBlank()) onUpdate(currentItemText, item.isChecked)
                    editingItemText = !editingItemText
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Filled.Check,
                    "Lưu mục",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {

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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    comment.authorName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.timestamp.toString(), style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
fun AssignMemberDialog(
    boardMembers: List<User>, // Tất cả thành viên của board
    assignedMemberIds: List<String>, // ID các thành viên đã được gán cho card này
    onAssign: (userId: String) -> Unit,
    onUnassign: (userId: String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gán thành viên") },
        text = {
            LazyColumn {
                items(boardMembers) { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (assignedMemberIds.contains(member.uid)) {
                                    onUnassign(member.uid)
                                } else {
                                    onAssign(member.uid)
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = assignedMemberIds.contains(member.uid),
                            onCheckedChange = null // Xử lý trong modifier của Row
                        )
                        Text(member.displayName)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Xong") } }
    )
}


@Composable
fun AiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aiGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF8E2DE2),
            Color(0xFF4A00E0)
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .size(height = 40.dp, width = 100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(aiGradient),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = "AI Icon",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gợi ý",
                    color = Color.White
                )
            }
        }
    }
}
