package com.prafullkumar.orbit.appblocker.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.prafullkumar.orbit.appblocker.model.BlockSchedule
import com.prafullkumar.orbit.appblocker.model.BlockedApp
import java.util.Calendar
import java.util.UUID

private val Background = Color(0xFF090909)
private val Accent = Color(0xFF7C6FE0)
private val SurfaceColor = Color(0xFF1A1A1A)
private val OnSurface = Color(0xFFEEEEEE)
private val SubText = Color(0xFF888888)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBlockerScreen(
    navController: NavHostController,
    viewModel: AppBlockerViewModel
) {
    val blockedApps by viewModel.blockedApps.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val isFocusActive by viewModel.isFocusActive.collectAsState()
    val focusRemainingSeconds by viewModel.focusRemainingSeconds.collectAsState()

    var selectedDuration by rememberSaveable { mutableIntStateOf(25) }
    var showAddAppSheet by rememberSaveable { mutableStateOf(false) }
    var showAddScheduleSheet by rememberSaveable { mutableStateOf(false) }
    var appToDelete by remember { mutableStateOf<BlockedApp?>(null) }
    var scheduleToDelete by remember { mutableStateOf<BlockSchedule?>(null) }

    val durationOptions = listOf(25, 45, 60, 120)

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("App Blocker", color = OnSurface, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Focus Session Card
            item {
                FocusSessionCard(
                    isFocusActive = isFocusActive,
                    focusRemainingSeconds = focusRemainingSeconds,
                    selectedDuration = selectedDuration,
                    durationOptions = durationOptions,
                    onDurationSelected = { selectedDuration = it },
                    onStartFocus = { viewModel.startFocus(selectedDuration) },
                    onStopFocus = { viewModel.stopFocus() }
                )
            }

            // Blocked Apps header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Blocked Apps", color = OnSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    TextButton(onClick = { showAddAppSheet = true }) {
                        Text("+ Add", color = Accent)
                    }
                }
            }

            if (blockedApps.isEmpty()) {
                item {
                    Text(
                        "No apps blocked yet",
                        color = SubText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(blockedApps, key = { it.packageName }) { app ->
                    BlockedAppRow(
                        app = app,
                        onToggle = { viewModel.toggleBlockedApp(app.packageName, it) },
                        onLongPress = { appToDelete = app }
                    )
                }
            }

            // Schedules header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedules", color = OnSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    TextButton(onClick = { showAddScheduleSheet = true }) {
                        Text("+ Add", color = Accent)
                    }
                }
            }

            if (schedules.isEmpty()) {
                item {
                    Text(
                        "No schedules set",
                        color = SubText,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(schedules, key = { it.id }) { schedule ->
                    ScheduleCard(
                        schedule = schedule,
                        onLongPress = { scheduleToDelete = schedule }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Delete blocked app dialog
    appToDelete?.let { app ->
        AlertDialog(
            onDismissRequest = { appToDelete = null },
            containerColor = SurfaceColor,
            title = { Text("Remove App", color = OnSurface) },
            text = { Text("Remove ${app.label} from blocked apps?", color = SubText) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeBlockedApp(app.packageName)
                    appToDelete = null
                }) { Text("Remove", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { appToDelete = null }) { Text("Cancel", color = Accent) }
            }
        )
    }

    // Delete schedule dialog
    scheduleToDelete?.let { schedule ->
        AlertDialog(
            onDismissRequest = { scheduleToDelete = null },
            containerColor = SurfaceColor,
            title = { Text("Delete Schedule", color = OnSurface) },
            text = { Text("Delete schedule \"${schedule.name}\"?", color = SubText) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSchedule(schedule.id)
                    scheduleToDelete = null
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { scheduleToDelete = null }) { Text("Cancel", color = Accent) }
            }
        )
    }

    // Add App Bottom Sheet
    if (showAddAppSheet) {
        AddAppBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAddAppSheet = false }
        )
    }

    // Add Schedule Bottom Sheet
    if (showAddScheduleSheet) {
        AddScheduleBottomSheet(
            onDismiss = { showAddScheduleSheet = false },
            onSave = { schedule ->
                viewModel.addSchedule(schedule)
                showAddScheduleSheet = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FocusSessionCard(
    isFocusActive: Boolean,
    focusRemainingSeconds: Int,
    selectedDuration: Int,
    durationOptions: List<Int>,
    onDurationSelected: (Int) -> Unit,
    onStartFocus: () -> Unit,
    onStopFocus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Focus Session", color = OnSurface, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text("Block all apps for a set time", color = SubText, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))

            if (!isFocusActive) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    durationOptions.forEach { duration ->
                        FilterChip(
                            selected = selectedDuration == duration,
                            onClick = { onDurationSelected(duration) },
                            label = {
                                Text(
                                    when (duration) {
                                        60 -> "1h"
                                        120 -> "2h"
                                        else -> "${duration}m"
                                    },
                                    color = if (selectedDuration == duration) Background else OnSurface
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                containerColor = Color(0xFF2A2A2A)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onStartFocus,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Text("Start Focus", color = Color.White)
                }
            } else {
                val hours = focusRemainingSeconds / 3600
                val minutes = (focusRemainingSeconds % 3600) / 60
                val seconds = focusRemainingSeconds % 60
                val totalDuration = selectedDuration * 60f

                Text(
                    text = "%02d:%02d:%02d".format(hours, minutes, seconds),
                    color = Accent,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { if (totalDuration > 0) focusRemainingSeconds / totalDuration else 0f },
                    modifier = Modifier.fillMaxWidth(),
                    color = Accent,
                    trackColor = Color(0xFF2A2A2A)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onStopFocus,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent)
                ) {
                    Text("Stop Focus")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BlockedAppRow(
    app: BlockedApp,
    onToggle: (Boolean) -> Unit,
    onLongPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onLongClick = onLongPress, onClick = {}),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(app.label, color = OnSurface, fontWeight = FontWeight.Medium)
                Text(app.packageName, color = SubText, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = app.isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Accent
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ScheduleCard(
    schedule: BlockSchedule,
    onLongPress: () -> Unit
) {
    val dayNames = mapOf(
        Calendar.MONDAY to "Mon",
        Calendar.TUESDAY to "Tue",
        Calendar.WEDNESDAY to "Wed",
        Calendar.THURSDAY to "Thu",
        Calendar.FRIDAY to "Fri",
        Calendar.SATURDAY to "Sat",
        Calendar.SUNDAY to "Sun"
    )
    val daysText = schedule.activeDays.mapNotNull { dayNames[it] }.joinToString(", ")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onLongClick = onLongPress, onClick = {}),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(schedule.name, color = OnSurface, fontWeight = FontWeight.Medium)
                Text(
                    "%02d:%02d – %02d:%02d".format(
                        schedule.startHour, schedule.startMinute,
                        schedule.endHour, schedule.endMinute
                    ),
                    color = Accent,
                    fontSize = 13.sp
                )
                if (daysText.isNotEmpty()) {
                    Text(daysText, color = SubText, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAppBottomSheet(
    viewModel: AppBlockerViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    val installedApps by viewModel.installedApps.collectAsState()
    val filtered = remember(searchQuery, installedApps) {
        if (searchQuery.isBlank()) installedApps
        else installedApps.filter { (pkg, label) ->
            label.contains(searchQuery, ignoreCase = true) || pkg.contains(searchQuery, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Add App to Block",
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search apps…", color = SubText) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = SubText,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.height(400.dp)) {
                items(filtered, key = { it.first }) { (pkg, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(label, color = OnSurface)
                            Text(pkg, color = SubText, fontSize = 11.sp)
                        }
                        TextButton(onClick = {
                            viewModel.addBlockedApp(pkg, label)
                            onDismiss()
                        }) {
                            Text("Add", color = Accent)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AddScheduleBottomSheet(
    onDismiss: () -> Unit,
    onSave: (BlockSchedule) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by rememberSaveable { mutableStateOf("") }
    var startHour by rememberSaveable { mutableIntStateOf(9) }
    var startMinute by rememberSaveable { mutableIntStateOf(0) }
    var endHour by rememberSaveable { mutableIntStateOf(17) }
    var endMinute by rememberSaveable { mutableIntStateOf(0) }
    var selectedDays by rememberSaveable { mutableStateOf(setOf<Int>()) }

    val days = listOf(
        Calendar.MONDAY to "Mon",
        Calendar.TUESDAY to "Tue",
        Calendar.WEDNESDAY to "Wed",
        Calendar.THURSDAY to "Thu",
        Calendar.FRIDAY to "Fri",
        Calendar.SATURDAY to "Sat",
        Calendar.SUNDAY to "Sun"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Add Schedule",
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Schedule name", color = SubText) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = SubText,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                    focusedLabelColor = Accent,
                    unfocusedLabelColor = SubText
                )
            )

            // Start time
            Text("Start Time", color = SubText, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeField(
                    value = startHour,
                    label = "HH",
                    range = 0..23,
                    modifier = Modifier.weight(1f),
                    onChange = { startHour = it }
                )
                Text(":", color = OnSurface, fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterVertically))
                TimeField(
                    value = startMinute,
                    label = "MM",
                    range = 0..59,
                    modifier = Modifier.weight(1f),
                    onChange = { startMinute = it }
                )
            }

            // End time
            Text("End Time", color = SubText, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeField(
                    value = endHour,
                    label = "HH",
                    range = 0..23,
                    modifier = Modifier.weight(1f),
                    onChange = { endHour = it }
                )
                Text(":", color = OnSurface, fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterVertically))
                TimeField(
                    value = endMinute,
                    label = "MM",
                    range = 0..59,
                    modifier = Modifier.weight(1f),
                    onChange = { endMinute = it }
                )
            }

            // Days
            Text("Active Days", color = SubText, fontSize = 13.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                days.forEach { (calDay, label) ->
                    FilterChip(
                        selected = calDay in selectedDays,
                        onClick = {
                            selectedDays = if (calDay in selectedDays)
                                selectedDays - calDay else selectedDays + calDay
                        },
                        label = {
                            Text(
                                label,
                                color = if (calDay in selectedDays) Background else OnSurface
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Accent,
                            containerColor = Color(0xFF2A2A2A)
                        )
                    )
                }
            }

            Button(
                onClick = {
                    if (name.isNotBlank() && selectedDays.isNotEmpty()) {
                        onSave(
                            BlockSchedule(
                                id = UUID.randomUUID().toString(),
                                name = name.trim(),
                                startHour = startHour,
                                startMinute = startMinute,
                                endHour = endHour,
                                endMinute = endMinute,
                                activeDays = selectedDays.toList()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled = name.isNotBlank() && selectedDays.isNotEmpty()
            ) {
                Text("Save Schedule", color = Color.White)
            }
        }
    }
}

@Composable
private fun TimeField(
    value: Int,
    label: String,
    range: IntRange,
    modifier: Modifier = Modifier,
    onChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = value.toString().padStart(2, '0'),
        onValueChange = { input ->
            input.toIntOrNull()?.let { if (it in range) onChange(it) }
        },
        label = { Text(label, color = SubText) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = SubText,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface,
            focusedLabelColor = Accent,
            unfocusedLabelColor = SubText
        )
    )
}
