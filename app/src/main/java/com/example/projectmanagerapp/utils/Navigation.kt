package com.example.projectmanagerapp.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.viewmodels.BoardViewModel
import com.example.projectmanagerapp.viewmodels.BoardViewModelFactory
import com.example.projectmanagerapp.ui.main.screens.BoardsScreen
import com.example.projectmanagerapp.ui.main.screens.CreateBoardScreen
import com.example.projectmanagerapp.ui.main.HomeScreen
import com.example.projectmanagerapp.repositories.RepositoryImplement
import com.example.projectmanagerapp.ui.main.screens.EditBoardScreen
import com.example.projectmanagerapp.viewmodels.CreateBoardViewModel
import com.example.projectmanagerapp.viewmodels.CreateBoardViewModelFactory
import com.example.projectmanagerapp.viewmodels.EditBoardViewModel
import com.example.projectmanagerapp.viewmodels.EditBoardViewModelFactory


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = AppDestinations.BOARD_ROUTE, modifier = modifier) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen()
        }
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen()
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen()
        }
        composable(AppDestinations.BOARD_ROUTE) {
            val repository = RepositoryImplement()
            val viewModel: BoardViewModel = viewModel(factory = BoardViewModelFactory(repository))
            BoardsScreen(
                viewModel = viewModel,
                onBoardClick = {
                    navController.navigate(AppDestinations.BOARD_DETAIL_ROUTE)
                },
                onAddBoardClick = {
                    navController.navigate(AppDestinations.CREATE_BOARD_ROUTE)
                },
                onSearchClick = {},
                onRenameBoardRequest = {},
                onChangeBackgroundRequest = {},
                onDeleteBoardRequest = {},
                onEditBoard = {boardId ->
                    navController.navigate(AppDestinations.EDIT_BOARD_ROUTE.replace("{boardId}", boardId))
                }

            )
        }
        composable(AppDestinations.CREATE_BOARD_ROUTE) {
            val repository = RepositoryImplement()
            val viewModel: CreateBoardViewModel = viewModel(factory = CreateBoardViewModelFactory(repository))
            CreateBoardScreen(onNavigateBack = {
                navController.popBackStack()
            },
                onBoardCreatedSuccessfully = {
                    navController.popBackStack()
                },
                viewModel = viewModel)
        }
        composable(route = AppDestinations.EDIT_BOARD_ROUTE,
            arguments = listOf(navArgument("boardId") {type = NavType.StringType})) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getString("boardId")
            val repository = RepositoryImplement()
            val viewModel: EditBoardViewModel = viewModel(factory = EditBoardViewModelFactory(repository, boardId!!))
            Log.d("Edit board route", "boardId: $boardId")
            EditBoardScreen(viewModel = viewModel,
                onNavigateBack = {navController.popBackStack()},
                onBoardUpdatedSuccessfully = {navController.popBackStack()})
        }

    }
}