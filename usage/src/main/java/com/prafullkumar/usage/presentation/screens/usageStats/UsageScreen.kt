package com.prafullkumar.usage.presentation.screens.usageStats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.prafullkumar.usage.presentation.screens.usageStats.components.UsageStatItem

/**
 * Main usage statistics screen composable
 *
 * Function Flow:
 * 1. UsageScreen() - Main screen with navigation and layout
 * 2. WeeklyGraph() - Displays weekly chart via DailyUsageComposable
 * 3. UsageStatItem() - Individual app usage item in list
 * 4. formatDuration() - Formats milliseconds to human readable time
 * 5. formatTimeAgo() - Formats last used time to relative format
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageScreen(
    navController: NavHostController,
    viewModel: UsageScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    Surface(
        Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(text = "Usage Stats")
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
            }
        ) { paddingValues ->
            if (uiState.loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        WeeklyGraph(viewModel)
                        Spacer(Modifier.height(16.dp))
                    }
                    items(uiState.appUsageList.size, key = {
                        it.hashCode()
                    }) { data ->
                        val selectedDayData = uiState.appUsageList[selectedDate]
                        if (selectedDayData != null && selectedDayData.isNotEmpty()) {
                            UsageStatItem(selectedDayData[data], navController)
                        }
                    }
                }
            }

        }
    }
}


