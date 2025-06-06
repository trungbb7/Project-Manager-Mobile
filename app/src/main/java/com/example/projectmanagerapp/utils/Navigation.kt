package com.example.projectmanagerapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmanagerapp.ui.auth.AuthViewModel
import com.example.projectmanagerapp.ui.auth.ForgotPasswordScreen
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.ui.main.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.LOGIN_ROUTE,
        modifier = modifier
    ) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(
                onLogout = {
                    navController.navigate(AppDestinations.LOGIN_ROUTE) {
                        popUpTo(AppDestinations.HOME_ROUTE) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
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
                onGoogleClick = { /* Implement Google login */ },
                onFacebookClick = { /* Implement Facebook login */ },
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
    }
}