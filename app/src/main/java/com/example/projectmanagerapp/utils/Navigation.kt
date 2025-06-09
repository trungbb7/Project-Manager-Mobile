package com.example.projectmanagerapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmanagerapp.MainActivity
import com.example.projectmanagerapp.ui.auth.AuthViewModel
import com.example.projectmanagerapp.ui.auth.ForgotPasswordScreen
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.ui.main.HomeScreen

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
    }
}