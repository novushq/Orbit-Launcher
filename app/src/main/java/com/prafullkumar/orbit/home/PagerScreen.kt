package com.prafullkumar.orbit.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeScreen
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PagerScreen(navController: NavHostController) {
    val homeViewModel: HomeViewModel = koinViewModel()
    HomeScreen(navController = navController, viewModel = homeViewModel)
}