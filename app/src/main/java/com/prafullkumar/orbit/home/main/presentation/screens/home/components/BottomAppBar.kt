package com.prafullkumar.orbit.home.main.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomAppBar(
    modifier: Modifier = Modifier,
    onPhoneClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onDrawerClick: () -> Unit,
    onCameraClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left group: Phone, Messages, Camera
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            DockButton(Icons.Default.Phone, "Phone", onPhoneClick)
            DockButton(Icons.Default.Message, "Messages", onMessagesClick)
            DockButton(Icons.Default.CameraAlt, "Camera", onCameraClick)
        }
        // Right: Drawer
        DockButton(Icons.AutoMirrored.Rounded.List, "App Drawer", onDrawerClick)
    }
}

@Composable
private fun DockButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1A1A))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color(0xFFAAAAAA),
            modifier = Modifier.size(22.dp)
        )
    }
}