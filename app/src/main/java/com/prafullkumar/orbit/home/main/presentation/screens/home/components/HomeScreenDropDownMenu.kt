package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun HomeScreenDropDownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onUninstall: () -> Unit,
    onRemoveFromFavorites: () -> Unit,
    onAppInfoClick: () -> Unit
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
            text = { Text("Remove from Favorites") },
            onClick = {
                onRemoveFromFavorites()
                onDismiss()
            }
        )
        DropdownMenuItem(
            text = { Text("App Info") },
            onClick = {
                onAppInfoClick()
                onDismiss()
            }
        )
    }
}