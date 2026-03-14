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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.prafullkumar.orbit.settings.data.SettingsPreferenceStore
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PagerScreen(navController: NavHostController) {
    val settingsStore: SettingsPreferenceStore = koinInject()
    val habitsEnabled by settingsStore.habitsEnabled.collectAsState(initial = false)
    val pageCount = if (habitsEnabled) 2 else 1

    val pagerState = rememberPagerState(initialPage = 0) { pageCount }

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