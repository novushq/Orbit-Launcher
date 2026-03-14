package com.prafullkumar.orbit.home.main.presentation.screens.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.core.navigation.HomeRoutes
import com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.AppDrawerScreen
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.BottomAppBar
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.FavouritesSection
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.UsageComposable
import com.prafullkumar.orbit.home.main.presentation.screens.home.components.WatchComposable
import com.prafullkumar.orbit.settings.data.SettingsPreferenceStore
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val groupedApps by viewModel.groupedApps.collectAsStateWithLifecycle()
    val favApps by viewModel.favApps.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val settingsStore: SettingsPreferenceStore = koinInject()
    val habitsEnabled by settingsStore.habitsEnabled.collectAsState(initial = false)

    var showDrawer by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = {
            BottomAppBar(
                onPhoneClick = { openIntent(context, Intent.ACTION_DIAL) },
                onMessagesClick = {
                    val i = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_MESSAGING) }
                    openIntent(context, intent = i, action = "")
                },
                onDrawerClick = { showDrawer = true },
                onCameraClick = { viewModel.launchCamera(context) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -40) { scope.launch { showDrawer = true } }
                    }
                }
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            // Clock + date + greeting
            WatchComposable(viewModel)

            Spacer(Modifier.height(8.dp))

            // Screen time
            UsageComposable(viewModel, navController)

            // Habits link (only when habits are enabled in settings)
            if (habitsEnabled) {
                Spacer(Modifier.height(8.dp))
                HabitsLinkRow(onTap = { navController.navigate(HomeRoutes.HabitsScreen) })
            }

            Spacer(Modifier.height(32.dp))

            // Pinned favorites
            FavouritesSection(favApps, viewModel, context)

            Spacer(Modifier.weight(1f))
        }
    }

    if (showDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showDrawer = false },
            sheetState = sheetState,
            dragHandle = null,
            containerColor = Color(0xFF0D0D0D)
        ) {
            AppDrawerScreen(
                viewModel = koinViewModel(),
                navController = navController,
                groupedApps = groupedApps,
                onDismiss = {
                    scope.launch { sheetState.hide(); showDrawer = false }
                }
            )
        }
    }
}

@Composable
private fun HabitsLinkRow(onTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🎯 Habits",
            fontSize = 13.sp,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Normal
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open Habits screen",
            tint = Color(0xFF444444),
            modifier = Modifier
                .padding(start = 2.dp)
                .height(16.dp)
        )
    }
}

fun openIntent(context: Context, action: String, intent: Intent? = null) {
    try {
        val i = intent ?: Intent(action)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    } catch (e: Exception) {
        android.util.Log.w("HomeScreen", "Failed to launch intent: action=$action", e)
    }
}
