package com.prafullkumar.orbit.home.main.presentation.screens.appDrawer.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AppDropDownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onUninstall: () -> Unit,
    onAddToFavorites: () -> Unit,
    onHideApp: () -> Unit,
    onAppInfo: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        DropdownMenuItem(
            text = { Text("Uninstall") },
            onClick = {
                onUninstall()
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text("Add to Favorites") },
            onClick = {
                onAddToFavorites()
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = {
                Text("App Info")
            },
            onClick = {
                onAppInfo()
            }
        )
        DropdownMenuItem(
            text = { Text("Hide App") },
            onClick = {
                onHideApp()
                onDismiss()
            }
        )
    }
}