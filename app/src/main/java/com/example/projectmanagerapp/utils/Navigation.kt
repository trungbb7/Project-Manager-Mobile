package com.example.projectmanagerapp.utils

import AddMemberScreen
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
import com.example.projectmanagerapp.repositories.MainFeaturesRepositoryImplement
import com.example.projectmanagerapp.ui.main.screens.BoardDetailScreen
import com.example.projectmanagerapp.ui.main.screens.CardDetailScreen
import com.example.projectmanagerapp.ui.main.screens.EditBoardScreen
import com.example.projectmanagerapp.viewmodels.AddMemberViewModel
import com.example.projectmanagerapp.viewmodels.AddMemberViewModelFactory
import com.example.projectmanagerapp.viewmodels.BoardDetailViewModel
import com.example.projectmanagerapp.viewmodels.BoardDetailViewModelFactory
import com.example.projectmanagerapp.viewmodels.CardDetailViewModel
import com.example.projectmanagerapp.viewmodels.CardDetailViewModelFactory
import com.example.projectmanagerapp.viewmodels.CreateBoardViewModel
import com.example.projectmanagerapp.viewmodels.CreateBoardViewModelFactory
import com.example.projectmanagerapp.viewmodels.EditBoardViewModel
import com.example.projectmanagerapp.viewmodels.EditBoardViewModelFactory


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.BOARD_ROUTE,
        modifier = modifier
    ) {
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
            val repository = MainFeaturesRepositoryImplement()
            val viewModel: BoardViewModel = viewModel(factory = BoardViewModelFactory(repository))
            BoardsScreen(
                viewModel = viewModel,
                onBoardClick = {
                    navController.navigate(
                        AppDestinations.BOARD_DETAIL_ROUTE.replace(
                            "{boardId}",
                            it.id
                        )
                    )
                },
                onAddBoardClick = {
                    navController.navigate(AppDestinations.CREATE_BOARD_ROUTE)
                },
                onEditBoard = { boardId ->
                    navController.navigate(
                        AppDestinations.EDIT_BOARD_ROUTE.replace(
                            "{boardId}",
                            boardId
                        )
                    )
                }

            )
        }
        composable(AppDestinations.CREATE_BOARD_ROUTE) {
            val repository = MainFeaturesRepositoryImplement()
            val viewModel: CreateBoardViewModel =
                viewModel(factory = CreateBoardViewModelFactory(repository))
            CreateBoardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBoardCreatedSuccessfully = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
        composable(
            route = AppDestinations.EDIT_BOARD_ROUTE,
            arguments = listOf(navArgument("boardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getString("boardId")
            val repository = MainFeaturesRepositoryImplement()
            val viewModel: EditBoardViewModel =
                viewModel(factory = EditBoardViewModelFactory(repository, boardId!!))
            EditBoardScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onBoardUpdatedSuccessfully = { navController.popBackStack() })
        }

        composable(
            route = AppDestinations.BOARD_DETAIL_ROUTE,
            arguments = listOf(navArgument("boardId") { type = NavType.StringType })
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getString("boardId")
            val repository = MainFeaturesRepositoryImplement()
            val viewModel: BoardDetailViewModel =
                viewModel(factory = BoardDetailViewModelFactory(repository, boardId!!))
            BoardDetailScreen(
                viewModel,
                onNavigateBack = { navController.popBackStack() },
                onCardItemClicked = { listId, cardId ->
                    navController.navigate(
                        AppDestinations.CARD_DETAIL_ROUTE
                            .replace("{boardId}", boardId)
                            .replace("{listId}", listId)
                            .replace("{cardId}", cardId)
                    )
                },
                onInviteClick = {
                    navController.navigate(
                        AppDestinations.ADD_MEMBER_ROUTE.replace("{boardId}", boardId)
                    )
                }
            )
        }

        composable(
            route = AppDestinations.CARD_DETAIL_ROUTE,
            arguments = listOf(
                navArgument("boardId") { type = NavType.StringType },
                navArgument("listId") { type = NavType.StringType },
                navArgument("cardId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getString("boardId")
            val listId = backStackEntry.arguments?.getString("listId")
            val cardId = backStackEntry.arguments?.getString("cardId")
            val repository = MainFeaturesRepositoryImplement()
            val viewModel: CardDetailViewModel =
                viewModel(factory = CardDetailViewModelFactory(repository, boardId!!, listId!!, cardId!!))
            CardDetailScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()}
            )
        }

        composable(
            route = AppDestinations.ADD_MEMBER_ROUTE,
            arguments = listOf(
                navArgument("boardId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val boardId = backStackEntry.arguments?.getString("boardId")
            val repository = MainFeaturesRepositoryImplement()
            val viewModel: AddMemberViewModel =
                viewModel(factory = AddMemberViewModelFactory(repository, boardId!!))
            AddMemberScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()}
            )
        }
    }
}