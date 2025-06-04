package com.example.projectmanagerapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.projectmanagerapp.ui.auth.LoginScreen
import com.example.projectmanagerapp.ui.auth.RegisterScreen
import com.example.projectmanagerapp.ui.main.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = AppDestinations.HOME_ROUTE, modifier = modifier) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen()
        }
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen()
        }
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen()
        }
    }
}