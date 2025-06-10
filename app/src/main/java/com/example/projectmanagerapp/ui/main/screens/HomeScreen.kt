package com.example.projectmanagerapp.ui.main.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.User
import com.example.projectmanagerapp.ui.theme.Typography
import com.example.projectmanagerapp.viewmodels.BoardViewModel
import com.example.projectmanagerapp.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    boardViewModel: BoardViewModel,
    onBoardClick: (Board) -> Unit,
    onAddBoardClick: () -> Unit,
    onEditBoard: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val uiState = homeViewModel.uiState.collectAsState()
    val user = uiState.value.user

    val navigationIcon = @Composable {
        IconButton(onClick = {
            scope.launch {
                if (drawerState.isClosed) {
                    drawerState.open()
                } else {
                    drawerState.close()
                }
            }
        }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(user = user, onProfileClick = onProfileClick)
        },
        drawerState = drawerState
    ) {
        BoardsScreen(viewModel = boardViewModel,
            onBoardClick = onBoardClick,
            onAddBoardClick = onAddBoardClick,
            onEditBoard = onEditBoard,
            navigationIcon = navigationIcon)
    }
}


@Composable
fun DrawerContent(user: User?, onProfileClick: () -> Unit) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(40.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user?.photoUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.user_image_placeholder) // Ảnh chờ
                    .error(R.drawable.user_image_placeholder) // Ảnh lỗi
                    .build(),
                contentDescription = "Ảnh đại diện",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, androidx.compose.material3.MaterialTheme.colorScheme.surface), CircleShape)
            )
//            Image(painter = painterResource(id = R.drawable.homer_avatar), contentDescription = null,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.size(60.dp).clip(CircleShape))

            Spacer(Modifier.height(20.dp))
            Text(user?.displayName.toString(), modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp), style = Typography.titleLarge)
            Text(user?.email.toString(), modifier = Modifier.padding(start = 16.dp, bottom = 16.dp), style = MaterialTheme.typography.overline)
            HorizontalDivider()

            NavigationDrawerItem(
                label = { Text("Thông tin") },
                icon = {Icon(Icons.Default.AccountCircle, contentDescription = null)},
                selected = false,
                onClick = { onProfileClick() }
            )
            NavigationDrawerItem(
                label = { Text("Đăng xuất") },
                icon = {Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)},

                selected = false,
                onClick = { /* Handle click */ }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ModalDrawerSamplePreview() {
    DrawerContent(user = User(), onProfileClick = {})
}