package com.example.projectmanagerapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.ui.main.Board
import com.example.projectmanagerapp.ui.main.BoardsScreen
import com.example.projectmanagerapp.ui.main.CreateBoardScreen
import com.example.projectmanagerapp.ui.main.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = AppDestinations.DEMO, modifier = modifier) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen()
        }
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen()
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen()
        }
        composable(AppDestinations.DEMO) {
            val sampleBoards = listOf(
                Board("1", "Dự án cá nhân", backgroundImage = "https://picsum.photos/seed/projectA/600/400", backgroundColor = Color(0xFF4CAF50)),
                Board("2", "Kế hoạch du lịch hè 2025", backgroundColor = Color(0xFF2196F3)),
                Board("3", "Học Jetpack Compose", backgroundImage = "https://picsum.photos/seed/compose/600/400", backgroundColor = Color(0xFFFFC107)),
                Board("4", "Công việc công ty", backgroundColor = Color(0xFF9C27B0))
            )
            BoardsScreen(
                sampleBoards,
                onBoardClick = {},
                onAddBoardClick = {
                    navController.navigate(AppDestinations.CREATE_BOARD_ROUTE)
                },
                onSearchClick = {},
                onRenameBoardRequest = {},
                onChangeBackgroundRequest = {}
            ) { }
        }
        composable(AppDestinations.CREATE_BOARD_ROUTE) {
            CreateBoardScreen(onNavigateBack = {
                navController.popBackStack()
            },
                onCreateBoard = { boardName, backgroundColor, backgroundImageUri ->
                })
        }


    }
}