package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prafullkumar.orbit.core.model.AppInfo
import com.prafullkumar.orbit.core.utils.launchApp
import com.prafullkumar.orbit.core.utils.openAppInfo
import com.prafullkumar.orbit.home.main.presentation.screens.home.HomeViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavouritesSection(
    favApps: List<AppInfo>,
    viewModel: HomeViewModel,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (favApps.isEmpty()) {
            Text(
                text = "Long press any app to pin it here",
                fontSize = 13.sp,
                color = Color(0xFF444444),
                fontWeight = FontWeight.Normal
            )
        } else {
            favApps.forEach { app ->
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Text(
                        text = app.label,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFDDDDDD),
                        modifier = Modifier
                            .combinedClickable(
                                onClick = { launchApp(context, app) },
                                onLongClick = { expanded = true }
                            )
                            .padding(vertical = 10.dp, horizontal = 16.dp)
                    )
                    HomeScreenDropDownMenu(
                        expanded = expanded,
                        onDismiss = { expanded = false },
                        onUninstall = { expanded = false; viewModel.uninstallApp(context, app.packageName) },
                        onRemoveFromFavorites = { expanded = false; viewModel.removeFromFavorites(app.packageName) }
                    ) { openAppInfo(context, app.packageName) }
                }
            }
        }
    }
}