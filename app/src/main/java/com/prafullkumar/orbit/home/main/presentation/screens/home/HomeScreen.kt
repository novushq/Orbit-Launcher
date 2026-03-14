package com.prafullkumar.orbit.home.main.presentation.screens.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.AppDrawerScreen
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.BottomAppBar
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.FavouritesSection
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.UsageComposable
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.WatchComposable
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val groupedApps by viewModel.groupedApps.collectAsStateWithLifecycle()
    val favApps by viewModel.favApps.collectAsState()
    val scope = rememberCoroutineScope()

    val showBottomSheet = rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                BottomAppBar(
                        modifier = Modifier,
                        onPhoneClick = { scope.launch { openIntent(context, Intent.ACTION_DIAL) } },
                        onMessagesClick = {
                            scope.launch {
                                val intent =
                                        Intent(Intent.ACTION_MAIN).apply {
                                            addCategory(Intent.CATEGORY_APP_MESSAGING)
                                        }
                                openIntent(context, intent = intent, action = "")
                            }
                        },
                        onDrawerClick = { scope.launch { showBottomSheet.value = true } },
                        onCameraClick = { viewModel.launchCamera(context) }
                )
            }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                    Modifier.fillMaxSize()
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { _, dragAmount ->
                                    if (dragAmount < -40) {
                                        // Dragging up
                                        scope.launch { showBottomSheet.value = true }
                                    }
                                }
                            }
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                WatchComposable(viewModel)
                UsageComposable(viewModel, navController)
                FavouritesSection(favApps, viewModel, context)
                Spacer(Modifier.height(32.dp))
            }
        }

        if (showBottomSheet.value) {
            ModalBottomSheet(
                    onDismissRequest = { showBottomSheet.value = false },
                    sheetState = bottomSheetState,
                    dragHandle = null
            ) {
                AppDrawerScreen(
                        viewModel = koinViewModel(),
                        navController = navController,
                        groupedApps = groupedApps,
                        onDismiss = {
                            scope.launch {
                                bottomSheetState.hide()
                                showBottomSheet.value = false
                            }
                        }
                )
            }
        }
    }
}

fun openIntent(context: Context, action: String, intent: Intent? = null) {
    try {
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
                ?: run {
                    val intent = Intent(action)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
    } catch (e: Exception) {
        println(e.message)
    }
}
