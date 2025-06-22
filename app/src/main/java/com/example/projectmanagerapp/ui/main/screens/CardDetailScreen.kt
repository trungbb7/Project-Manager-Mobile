package com.example.projectmanagerapp.ui.main.screens


import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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


@Composable
fun AiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Gợi ý"
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
            .height(40.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .background(aiGradient)
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    text = text,
                    color = Color.White
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    viewModel: CardDetailViewModel,
    navController: NavController,
    onNavigateBack: () -> Unit,

    ) {
    val lazyListState = rememberLazyListState()

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
    LocalContext.current
    val userRecoverableAuthIntent = uiState.userRecoverableAuthIntent

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.addEventToCalendar()
        viewModel.onUserRecoverableAuthIntentHandled()
    }

    LaunchedEffect(userRecoverableAuthIntent) {
        if (userRecoverableAuthIntent != null) {
            authLauncher.launch(userRecoverableAuthIntent)
        }
    }


    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<CardLocation>("picked_location")?.observeForever { location ->
            viewModel.updateCardLocation(location)
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

    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(LocalTime.now()) }
    var selectedEpochMilli by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    )

    LaunchedEffect(error) {
        if (error != null) {
            scope.launch {
                snackbarHostState.showSnackbar(message = error)
            }
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
        val timePickerState = rememberTimePickerState(
            initialHour = LocalTime.now().hour,
            initialMinute = LocalTime.now().minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Chọn giờ") },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        val ldt = LocalDateTime.of(selectedDate, selectedTime)
                        selectedEpochMilli =
                            ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        viewModel.setDueDate(selectedEpochMilli)
                        showTimePicker = false
                    }
                ) { Text("Chọn") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Thẻ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = lazyListState
            ) {

                // Breadcrumbs
                item {
                    Text(
                        "Trong danh sách: ${listName}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("Trên bảng: ${boardName}", style = MaterialTheme.typography.titleSmall)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Editable Title
                    if (editingTitle) {
                        OutlinedTextField(
                            value = currentTitle,
                            onValueChange = { currentTitle = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(titleFocusRequester),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                viewModel.updateCardTitle(currentTitle)
                                editingTitle = false
                                keyboardController?.hide()
                            }),
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.updateCardTitle(currentTitle)
                                    editingTitle = false
                                    keyboardController?.hide()
                                }) {
                                    Icon(Icons.Default.Check, "Save")
                                }
                            }
                        )
                        LaunchedEffect(Unit) {
                            titleFocusRequester.requestFocus()
                        }
                    } else {
                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.clickable { editingTitle = true }
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                item {
                    CardDetailSection(icon = Icons.Default.Info, title = "Mô tả") {
                        if (editingDescription) {
                            OutlinedTextField(
                                value = currentDescription,
                                onValueChange = { currentDescription = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 100.dp),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    capitalization = KeyboardCapitalization.Sentences
                                ),
                                placeholder = { Text("Thêm mô tả chi tiết hơn...") }
                            )
                            Spacer(Modifier.height(8.dp))
                            Row {
                                Button(onClick = {
                                    viewModel.updateCardDescription(currentDescription)
                                    editingDescription = false
                                }) {
                                    Text("Lưu")
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = { editingDescription = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Hủy")
                                }
                            }
                        } else {
                            Text(
                                text = card.description ?: "Thêm mô tả chi tiết hơn...",
                                style = if (card.description == null) MaterialTheme.typography.bodyLarge.copy(
                                    fontStyle = FontStyle.Italic
                                ) else MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editingDescription = true }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                item {
                    CardDetailSection(icon = Icons.Default.Person, title = "Thành viên") {
                        // Assigned members
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Giao cho",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showAssignMemberDialog = true }) {
                                Icon(Icons.Default.AddCircle, "Giao việc")
                            }
                        }

                        if (uiState.assignedMembers.isNotEmpty()) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                uiState.assignedMembers.forEach { member ->
                                    AsyncImage(
                                        model = member.photoUrl,
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .border(
                                                BorderStroke(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.primary
                                                ),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        } else {
                            Text("Chưa giao cho thành viên nào.")
                        }

                        if (showAssignMemberDialog) {
                            AssignMemberDialog(
                                boardMembers = uiState.boardMembers,
                                assignedMemberIds = card.assignedMemberIds,
                                onDismiss = { showAssignMemberDialog = false },
                                onMemberSelected = { member, isAssigned ->
                                    if (isAssigned) {
                                        viewModel.assignMember(member.uid)
                                    } else {
                                        viewModel.unAssignMember(member.uid)
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    CardDetailSection(icon = Icons.Default.DateRange, title = "Ngày hết hạn") {
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
                        // Add to Calendar button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .clickable { viewModel.addEventToCalendar() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = "Thêm vào Lịch Google",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Thêm vào Lịch Google")
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
                                    navController.navigate(
                                        AppDestinations.MAP_PICKER_ROUTE.replace(
                                            "{latitude}",
                                            if (cardLocation?.latitude != null) cardLocation.latitude.toString() else ""
                                        ).replace(
                                            "{longitude}",
                                            if (cardLocation?.longitude != null) cardLocation.longitude.toString() else ""
                                        )
                                    )
                                }
                        ) {
                            if (cardLocation != null) {
                                // Hiển thị vị trí đã chọn
                                Text(
                                    cardLocation.placeName ?: "Vị trí đã chọn",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    cardLocation.address,
                                    style = MaterialTheme.typography.bodyMedium
                                )
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CardDetailSectionTitle(icon = Icons.Default.List, title = "Checklist")
                        Row {
                            IconButton(
                                onClick = {
                                    viewModel.generateCheckList()
                                }
                            ) {
                                Icon(Icons.Outlined.AutoAwesome, "Tạo checklist bằng AI")
                            }
                            IconButton(onClick = { showAddChecklistDialog = true }) {
                                Icon(Icons.Default.Add, "Thêm checklist")
                            }
                        }

                    }
                }


                if (checklists.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Chưa có checklist nào")
                            Spacer(modifier = Modifier.height(8.dp))
                            AiButton(
                                onClick = {
                                    viewModel.generateCheckList()
                                },
                                text = "Tạo bằng AI"
                            )
                        }
                    }
                } else {
                    items(checklists) { checklist ->
                        ChecklistSection(
                            checklist = checklist,
                            onUpdateTitle = { title ->
                                viewModel.updateCheckListTitle(title, checklist.id)
                            },
                            onItemCheckedChange = { item, isChecked ->
                                viewModel.updateCheckListItem(
                                    checklist.id,
                                    item.id,
                                    item.text,
                                    isChecked
                                )
                            },
                            onAddItem = { text ->
                                viewModel.addCheckListItem(checklist.id, text)
                            },
                            onDeleteItem = { itemId ->
                                viewModel.deleteCheckListItem(checklist.id, itemId)
                            },
                            onDeleteChecklist = {
                                viewModel.deleteCheckList(checklist.id)
                            }
                        )
                    }
                }

                item {
                    CardDetailSectionTitle(icon = Icons.Default.MailOutline, title = "Bình luận")
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Thêm bình luận...") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        )
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
                }

                if (comments.isEmpty()) {
                    item {
                        Text(
                            "Chưa có bình luận nào.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }
                } else {
                    items(comments) { comment ->
                        val author = uiState.boardMembers.find { it.uid == comment.authorId }
                        CommentItem(
                            comment = comment,
                            author = author,
                            onDelete = { viewModel.deleteComment(comment.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddChecklistDialog) {
        var newChecklistTitle by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddChecklistDialog = false },
            title = { Text("Thêm Checklist Mới") },
            text = {
                OutlinedTextField(
                    value = newChecklistTitle,
                    onValueChange = { newChecklistTitle = it },
                    label = { Text("Tiêu đề checklist") }
                )
            },
            confirmButton = {
                Row {
                    AiButton(
                        onClick = {
                            viewModel.generateCheckList()
                            showAddChecklistDialog = false
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newChecklistTitle.isNotBlank()) {
                                viewModel.addCheckList(newChecklistTitle)
                                showAddChecklistDialog = false
                            }
                        }
                    ) { Text("Thêm") }
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAddChecklistDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) { Text("Hủy") }
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
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun CardDetailSectionTitle(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    ) {
        Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}


@Composable
fun ChecklistSection(
    checklist: Checklist,
    onUpdateTitle: (String) -> Unit,
    onItemCheckedChange: (ChecklistItem, Boolean) -> Unit,
    onAddItem: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onDeleteChecklist: () -> Unit
) {
    var editingTitle by remember { mutableStateOf(false) }
    var title by remember(checklist.title) { mutableStateOf(checklist.title) }
    var newItemText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (editingTitle) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    keyboardActions = KeyboardActions(onDone = {
                        onUpdateTitle(title)
                        editingTitle = false
                        keyboardController?.hide()
                    }),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    singleLine = true
                )
                IconButton(onClick = {
                    onUpdateTitle(title)
                    editingTitle = false
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Check, "Lưu tiêu đề")
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

            } else {
                Text(
                    text = checklist.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { editingTitle = true }
                )
            }

            IconButton(onClick = onDeleteChecklist) {
                Icon(Icons.Default.Delete, "Xóa checklist", tint = MaterialTheme.colorScheme.error)
            }
        }


        val progress =
            if (checklist.items.isNotEmpty()) checklist.items.count { it.isChecked }
                .toFloat() / checklist.items.size else 0f
        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())

        checklist.items.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { isChecked -> onItemCheckedChange(item, isChecked) }
                )
                Text(
                    text = item.text,
                    modifier = Modifier.weight(1f),
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    color = if (item.isChecked) Color.Gray else LocalContentColor.current
                )
                IconButton(onClick = { onDeleteItem(item.id) }) {
                    Icon(Icons.Default.Close, "Xóa mục", modifier = Modifier.size(16.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Thêm mục") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (newItemText.isNotBlank()) {
                        onAddItem(newItemText)
                        newItemText = ""
                        keyboardController?.hide()
                    }
                })
            )
            IconButton(
                onClick = {
                    if (newItemText.isNotBlank()) {
                        onAddItem(newItemText)
                        newItemText = ""
                        keyboardController?.hide()
                    }
                },
                enabled = newItemText.isNotBlank()
            ) {
                Icon(Icons.Default.Add, "Thêm mục")
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, author: User?, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(author?.photoUrl)
                .placeholder(R.drawable.user_image_placeholder)
                .error(R.drawable.user_image_placeholder)
                .build(),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(comment.authorName, fontWeight = FontWeight.Bold)
            Text(comment.text, style = MaterialTheme.typography.bodyMedium)
            Text(
                Utils.formatCommentTimestamp(comment.timestamp?.time ?: 0),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        // For simplicity, we assume the current user can delete their own comments.
        // A more robust implementation would check against the logged-in user's ID.
//        if (comment.canDelete) {
//            IconButton(onClick = onDelete) {
//                Icon(Icons.Default.Delete, "Xóa bình luận")
//            }
//        }
    }
}

@Composable
fun AssignMemberDialog(
    boardMembers: List<User>,
    assignedMemberIds: List<String>,
    onDismiss: () -> Unit,
    onMemberSelected: (User, Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Giao việc cho thành viên") },
        text = {
            LazyColumn {
                items(boardMembers) { member ->
                    var isAssigned by remember { mutableStateOf(assignedMemberIds.contains(member.uid)) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isAssigned = !isAssigned
                                onMemberSelected(member, isAssigned)
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isAssigned, onCheckedChange = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        AsyncImage(
                            model = member.photoUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(member.displayName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("XONG") }
        }
    )
}
