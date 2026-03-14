package com.prafullkumar.orbit.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.prafullkumar.hiddenapps.HiddenAppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val habitsEnabled by viewModel.habitsEnabled.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF090909),
                    titleContentColor = Color(0xFFEEEEEE),
                    navigationIconContentColor = Color(0xFFEEEEEE)
                )
            )
        },
        containerColor = Color(0xFF090909)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection("APPEARANCE") {
                SettingsPreferenceRow(title = "Theme", subtitle = "System default")
                SettingsPreferenceRow(title = "App icon shape", subtitle = "Rounded square")
                SettingsPreferenceRow(title = "Font size", subtitle = "Medium")
            }

            SettingsSection("LAUNCHER") {
                SettingsPreferenceRow(title = "Default page", subtitle = "Home")
                SettingsPreferenceRow(title = "Swipe up", subtitle = "Open drawer")
                SettingsPreferenceRow(title = "Swipe down", subtitle = "Notification shade")
                SettingsPreferenceRow(title = "Double-tap home", subtitle = "Lock screen")
                SettingsToggleRow(
                    title = "Enable habits screen",
                    checked = habitsEnabled,
                    onCheckedChange = { viewModel.setHabitsEnabled(it) }
                )
            }

            SettingsSection("PRIVACY") {
                SettingsPreferenceRow(
                    title = "Hidden apps",
                    onClick = { navController.navigate(HiddenAppRoutes.HiddenAppsMain) }
                )
                SettingsPreferenceRow(title = "Change hidden apps PIN")
            }

            SettingsSection("SCREEN TIME") {
                SettingsPreferenceRow(title = "Daily screen time goal", subtitle = "5 hours")
            }

            SettingsSection("ABOUT") {
                SettingsPreferenceRow(title = "Version", subtitle = "1.0.0", showArrow = false)
                SettingsPreferenceRow(
                    title = "Reset to defaults",
                    titleColor = Color.Red,
                    showArrow = false
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = title,
            color = Color(0xFF888888),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
        ) {
            content()
        }
    }
}

@Composable
fun SettingsPreferenceRow(
    title: String,
    subtitle: String? = null,
    titleColor: Color = Color(0xFFEEEEEE),
    showArrow: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Normal)
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = Color(0xFF888888), fontSize = 14.sp)
            }
        }
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF888888)
            )
        }
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color(0xFFEEEEEE),
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF7C6FE0)
            )
        )
    }
}
