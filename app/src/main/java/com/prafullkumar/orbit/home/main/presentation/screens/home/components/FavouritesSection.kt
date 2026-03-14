package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import android.content.Context
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.launchApp
import com.prafullkumar.orbit.core.utils.openAppInfo
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel

@Composable
fun FavouritesSection(
    favApps: List<AppInfo>,
    viewModel: HomeViewModel,
    context: Context
) {
    if (favApps.isNotEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            favApps.forEachIndexed { index, app ->
                var appDropDownExpanded by remember { mutableStateOf(false) }
                Box {
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    launchApp(context, app)
                                },
                                onLongClick = {
                                    appDropDownExpanded = true
                                })
                            .padding(vertical = 8.dp)
                    )
                    HomeScreenDropDownMenu(
                        expanded = appDropDownExpanded,
                        onDismiss = {
                            appDropDownExpanded = false
                        },
                        onUninstall = {
                            appDropDownExpanded = false
                            viewModel.uninstallApp(context, app.packageName)
                        },
                        onRemoveFromFavorites = {
                            appDropDownExpanded = false
                            viewModel.removeFromFavorites(app.packageName)
                        }
                    ) {
                        openAppInfo(context, app.packageName)
                    }
                }
            }
        }
    } else {
        Text(
            text = "No favorite apps added. Long press on any app to add to favorites.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}