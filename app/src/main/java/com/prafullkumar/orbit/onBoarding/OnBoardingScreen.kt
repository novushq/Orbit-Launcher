package com.prafullkumar.orbit.onBoarding

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.prafullkumar.orbit.core.navigation.Routes

/**
 * Onboarding screen that guides users through granting required permissions
 * Automatically navigates to home screen when all permissions are granted
 */
@Composable
fun OnBoardingScreen(
    viewModel: OnBoardingViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    Log.d("OnBoardingScreen", "OnBoardingScreen: called")
    // Refresh permissions when screen becomes visible (handles external permission changes)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshPermissionStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Activity result launcher for default launcher permission
    val roleRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // Refresh permissions after user returns from settings
        viewModel.refreshPermissionStatus()
    }

    // Activity result launcher for usage access permission
    val usageAccessLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // Refresh permissions after user returns from settings
        viewModel.refreshPermissionStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Welcome header
        Text(
            text = "Welcome to Orbit Launcher",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Grant the following permissions to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Permission cards
        PermissionCard(
            title = "Set as Default Launcher",
            description = "Required to replace your home screen",
            granted = state.isDefaultLauncher,
            onGrantClick = {
                if (!state.isDefaultLauncher) {
                    // Try modern approach first (Android Q+)
                    val intent = viewModel.createRoleRequestIntentOrNull()
                    if (intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        roleRequestLauncher.launch(intent)
                    } else {
                        // Fallback for older Android versions
                        roleRequestLauncher.launch(viewModel.createLegacyLauncherIntent())
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PermissionCard(
            title = "Usage Access",
            description = "Required to show app usage statistics",
            granted = state.hasUsageAccess,
            onGrantClick = {
                if (!state.hasUsageAccess) {
                    usageAccessLauncher.launch(viewModel.createUsageAccessIntent())
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Continue button (only enabled when all permissions granted)
        Button(
            onClick = {
                viewModel.completeOnboarding()
                navController.navigate(Routes.HomeScreen) {
                    popUpTo(Routes.OnboardingScreen) { inclusive = true }
                    launchSingleTop = true
                }
            },
            enabled = state.allPermissionsGranted,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (state.allPermissionsGranted) "Continue" else "Grant all permissions to continue",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Card component for displaying individual permission status and grant button
 */
@Composable
private fun PermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (granted)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Show granted status or grant button
                if (granted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Granted",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Granted",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    OutlinedButton(onClick = onGrantClick) {
                        Text("Grant")
                    }
                }
            }
        }
    }
}