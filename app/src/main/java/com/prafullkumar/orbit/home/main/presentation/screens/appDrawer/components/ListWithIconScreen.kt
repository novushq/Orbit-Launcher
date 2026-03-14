package com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.components

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.prafullkumar.hiddenapps.HiddenAppRoutes
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.launchApp
import com.prafullkumar.orbit.core.utils.openAppInfo
import com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.AppDrawerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppListWithIcons(
    paddingValues: PaddingValues,
    groupedApps: Map<Char, List<AppInfo>>,
    viewModel: AppDrawerViewModel,
    context: Context,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    scope: CoroutineScope,
    searchQuery: String,
    navController: NavController
) {
    LazyColumn(
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
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        groupedApps.filter {
            it.value.any { appInfo ->
                appInfo.label.contains(searchQuery, ignoreCase = true)
            }
        }.forEach { (letter, apps) ->
            item(key = "header_$letter") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            items(apps.filter {
                it.label.contains(searchQuery, ignoreCase = true)
            }, key = { it.packageName }) { app ->
                AppItemWithIcon(
                    app = app,
                    onClick = { launchApp(context, app) },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(300),
                        placementSpec = tween(300)
                    ),
                    viewModel = viewModel,
                    navController
                )
            }
        }
    }
}

@Composable
private fun AppItemWithIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppDrawerViewModel,
    navController: NavController
) {
    var showDropDown by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .combinedClickable(onClick = {
                    onClick()
                }, onLongClick = {
                    showDropDown = true
                })
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                app.icon.let { drawable ->
                    Image(
                        bitmap = drawable.toBitmap().asImageBitmap(),
                        contentDescription = "${app.label} icon",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                }

            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
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