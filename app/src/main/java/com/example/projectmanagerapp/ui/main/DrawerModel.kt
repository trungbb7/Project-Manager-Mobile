package com.example.projectmanagerapp.ui.main

import android.content.res.Resources
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projectmanagerapp.R
import com.example.projectmanagerapp.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer(
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent()
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Project Manager") },
                    navigationIcon = {
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
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}


@Composable
fun DrawerContent() {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(40.dp))
            Image(painter = painterResource(id = R.drawable.homer_avatar), contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(CircleShape))

            Spacer(Modifier.height(20.dp))
            Text("Nguyễn Minh Trung", modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp), style = Typography.titleLarge)
            Text("trungbb8@gmail.com", modifier = Modifier.padding(start = 16.dp, bottom = 16.dp), style = MaterialTheme.typography.overline)
            HorizontalDivider()

            NavigationDrawerItem(
                label = { Text("Thông tin") },
                icon = {Icon(Icons.Default.AccountCircle, contentDescription = null)},
                selected = false,
                onClick = { /* Handle click */ }
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
    DrawerContent()
}