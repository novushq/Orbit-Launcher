package com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.prafullkumar.hiddenapps.HiddenAppRoutes
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.launchApp
import com.prafullkumar.orbit.core.utils.openAppInfo
import com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.AppDrawerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppGridWithLabels(
    paddingValues: PaddingValues,
    groupedApps: Map<Char, List<AppInfo>>,
    viewModel: AppDrawerViewModel,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    scope: CoroutineScope,
    searchQuery: String,
    navController: NavController
) {
    val allApps = groupedApps.filter {
        it.value.any { appInfo ->
            appInfo.label.contains(searchQuery, ignoreCase = true)
        }
    }.values.flatten()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    scope.launch {
                        change.consume()
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
            },
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(allApps.filter {
            it.label.contains(searchQuery, ignoreCase = true)
        }, key = { it.packageName }) { app ->
            AppGridItemWithLabel(
                app = app,
                viewModel = viewModel,
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(500),
                    placementSpec = tween(500)
                ),
                navController
            )
        }
    }
}

@Composable
private fun AppGridItemWithLabel(
    app: AppInfo,
    viewModel: AppDrawerViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var showDropDown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box {
    Column(
        modifier = modifier
            .width(90.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.2f),
                RoundedCornerShape(18.dp)
            )
            .combinedClickable(
                onLongClick = {
                    showDropDown = true
                },
                onClick = {
                    launchApp(context, app)
                }
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppIcon(app = app)
        Spacer(modifier = Modifier.height(8.dp))
        AppLabel(app = app)
    }
        AppDropDownMenu(
            expanded = showDropDown,
            onDismiss = { showDropDown = false },
            onUninstall = { viewModel.uninstallApp(context, app) },
            onAddToFavorites = { viewModel.addToFavorites(app) },
            onHideApp = {
                viewModel.hideApp(context, app) {
                    navController.navigate(
                        HiddenAppRoutes.SetPasswordScreen(
                            true, app.packageName, app.label
                        )
                    )
                }
            },
            onAppInfo = {
                openAppInfo(context, app.packageName)
            }
        )
    }
}

@Composable
private fun AppIcon(app: AppInfo) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                CircleShape
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        app.icon?.let { drawable ->
            AsyncImage(
                model = drawable,
                contentDescription = "${app.label} icon",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun AppLabel(app: AppInfo) {
    Text(
        text = app.label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(80.dp)
    )
}