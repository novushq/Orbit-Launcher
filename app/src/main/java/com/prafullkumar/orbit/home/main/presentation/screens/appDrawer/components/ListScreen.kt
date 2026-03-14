package com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.components

import android.content.Context
import androidx.compose.animation.core.tween
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.prafullkumar.hiddenapps.HiddenAppRoutes
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.launchApp
import com.prafullkumar.orbit.core.utils.openAppInfo
import com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.AppDrawerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun AppList(
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
            }, contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        groupedApps.filter {
            it.value.any { appInfo ->
                appInfo.label.contains(searchQuery, ignoreCase = true)
            }
        }.forEach { (letter, apps) ->
            item(key = "header_$letter") {
                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp, start = 24.dp)
                )
            }
            items(apps.filter {
                it.label.contains(searchQuery, ignoreCase = true)
            }, key = { it.packageName }) { app ->
                AppItemRow(
                    app = app,
                    onClick = { launchApp(context, app) },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(300), placementSpec = tween(3000)
                    ),
                    viewModel = viewModel,
                    navController
                )
            }
        }
    }
}

@Composable
private fun AppItemRow(
    app: AppInfo, onClick: () -> Unit, modifier: Modifier = Modifier, viewModel: AppDrawerViewModel, navController: NavController
) {
    var showDropDown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box {
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier
                .fillMaxWidth()
                .combinedClickable(onClick = { onClick() }, onLongClick = { showDropDown = true })
                .padding(
                    horizontal = 24.dp, vertical = 16.dp
                ) // Generous padding for easy tapping and clean look
        )
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