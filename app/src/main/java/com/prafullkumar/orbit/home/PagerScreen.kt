package com.prafullkumar.orbit.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeScreen
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel
import com.prafullkumar.orbit.home.utility.UtilityScreen
import com.prafullkumar.orbit.home.utility.UtilityViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PagerScreen(navController: NavHostController) {
    val viewModels = mutableMapOf<PagerScreens, ViewModel>()

    val pagerState = rememberPagerState(initialPage = 0) { 2 }

    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize(), key = { it }) { page ->
        when (page) {
            0 -> {
                val homeViewModel: HomeViewModel = koinViewModel()
                HomeScreen(navController = navController, viewModel = homeViewModel)
            }
            1 -> {
                val utilityViewModel: UtilityViewModel = koinViewModel()
                UtilityScreen(navController = navController, viewModel = utilityViewModel)
            }
        }
    }
}