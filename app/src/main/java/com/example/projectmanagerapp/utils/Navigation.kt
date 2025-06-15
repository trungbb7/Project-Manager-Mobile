package com.example.projectmanagerapp.utils

import AddMemberScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmanagerapp.MainActivity
import com.example.projectmanagerapp.ui.auth.AuthViewModel
import com.example.projectmanagerapp.ui.auth.ForgotPasswordScreen
import androidx.navigation.navArgument
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.viewmodels.BoardViewModel
import com.example.projectmanagerapp.viewmodels.BoardViewModelFactory
import com.example.projectmanagerapp.ui.main.screens.CreateBoardScreen
import com.example.projectmanagerapp.ui.main.screens.HomeScreen
import com.example.projectmanagerapp.repositories.MainFeaturesRepositoryImplement
import com.example.projectmanagerapp.ui.auth.Profile
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
import com.example.projectmanagerapp.viewmodels.HomeViewModel
import com.example.projectmanagerapp.viewmodels.HomeViewModelFactory


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = AppDestinations.LOGIN_ROUTE,
        modifier = modifier
    ) {


        composable(AppDestinations.HOME_ROUTE) {
            val repository = MainFeaturesRepositoryImplement()
            val boardViewModel: BoardViewModel = viewModel(factory = BoardViewModelFactory(repository))
            val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))

            HomeScreen(
                homeViewModel = homeViewModel,
                boardViewModel = boardViewModel,
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
                },
                onProfileClick = {
                    navController.navigate(AppDestinations.PROFILE_ROUTE)
                },
                onSignOutClick = {
                    authViewModel.signOut()
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.HOME_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppDestinations.HOME_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(AppDestinations.REGISTER_ROUTE) },
                onForgotPasswordClick = { navController.navigate(AppDestinations.FORGOT_PASSWORD_ROUTE) },
                onGoogleClick = {
                    Log.d("Navigation", "Google button clicked")
                    // Set callback and start Google sign in
                    MainActivity.googleSignInHelper.setCallback { credential ->
                        Log.d("Navigation", "Google callback received: ${credential != null}")
                        credential?.let {
                            Log.d("Navigation", "Calling authViewModel.signInWithGoogle")
                            authViewModel.signInWithGoogle(it)
                        }
                    }
                    MainActivity.googleSignInHelper.signIn(forceAccountSelection = true)
                },
                onFacebookClick = {
                    Log.d("Navigation", "Facebook button clicked")
                    // Check if Facebook is configured
                    val facebookAppId = context.getString(com.example.projectmanagerapp.R.string.facebook_app_id)
                    if (facebookAppId == "YOUR_FACEBOOK_APP_ID") {
                        Log.d("Navigation", "Facebook not configured yet")
                        authViewModel.resetState()
                        // Show message that Facebook needs configuration
                        return@LoginScreen
                    }

                    // Set callback and start Facebook sign in
                    MainActivity.facebookSignInHelper.setCallback { credential ->
                        Log.d("Navigation", "Facebook callback received: ${credential != null}")
                        credential?.let {
                            Log.d("Navigation", "Calling authViewModel.signInWithFacebook")
                            authViewModel.signInWithFacebook(it)
                        }
                    }
                    MainActivity.facebookSignInHelper.signIn(context as MainActivity)
                },
                authViewModel = authViewModel
            )
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.REGISTER_ROUTE) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }
        composable(AppDestinations.FORGOT_PASSWORD_ROUTE) {
            ForgotPasswordScreen(
                onResetSuccess = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }
        composable(AppDestinations.PROFILE_ROUTE) {
            Profile()
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
