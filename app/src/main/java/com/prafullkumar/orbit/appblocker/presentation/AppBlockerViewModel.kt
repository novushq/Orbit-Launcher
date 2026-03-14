package com.prafullkumar.orbit.appblocker.presentation

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.orbit.appblocker.data.AppBlockerRepository
import com.prafullkumar.orbit.appblocker.data.local.BlockedAppEntity
import com.prafullkumar.orbit.appblocker.data.local.BlockScheduleEntity
import com.prafullkumar.orbit.appblocker.model.BlockSchedule
import com.prafullkumar.orbit.appblocker.model.BlockedApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppBlockerViewModel(
    private val repository: AppBlockerRepository,
    private val context: Context
) : ViewModel() {

    val blockedApps: StateFlow<List<BlockedApp>> = repository.getAllBlockedApps()
        .map { entities ->
            entities.map { BlockedApp(it.packageName, it.label, it.isEnabled) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<BlockSchedule>> = repository.getAllSchedules()
        .map { entities ->
            entities.map { entity ->
                BlockSchedule(
                    id = entity.id,
                    name = entity.name,
                    startHour = entity.startHour,
                    startMinute = entity.startMinute,
                    endHour = entity.endHour,
                    endMinute = entity.endMinute,
                    activeDays = entity.activeDays.split(",").mapNotNull { it.trim().toIntOrNull() },
                    isEnabled = entity.isEnabled
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isFocusActive = MutableStateFlow(false)
    val isFocusActive: StateFlow<Boolean> = _isFocusActive.asStateFlow()

    private val _focusEndTimeMs = MutableStateFlow<Long?>(null)
    val focusEndTimeMs: StateFlow<Long?> = _focusEndTimeMs.asStateFlow()

    private val _focusRemainingSeconds = MutableStateFlow(0)
    val focusRemainingSeconds: StateFlow<Int> = _focusRemainingSeconds.asStateFlow()

    private var focusTimerJob: Job? = null

    private val _installedApps = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val installedApps: StateFlow<List<Pair<String, String>>> = _installedApps.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                .map { it.packageName to it.loadLabel(pm).toString() }
                .sortedBy { it.second }
            _installedApps.value = apps
        }
    }

    fun startFocus(durationMinutes: Int) {
        val endTime = System.currentTimeMillis() + durationMinutes * 60 * 1000L
        _focusEndTimeMs.value = endTime
        _isFocusActive.value = true
        _focusRemainingSeconds.value = durationMinutes * 60

        focusTimerJob?.cancel()
        focusTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val remaining = ((endTime - System.currentTimeMillis()) / 1000).toInt()
                if (remaining <= 0) {
                    _focusRemainingSeconds.value = 0
                    stopFocus()
                    break
                }
                _focusRemainingSeconds.value = remaining
            }
        }
    }

    fun stopFocus() {
        focusTimerJob?.cancel()
        focusTimerJob = null
        _isFocusActive.value = false
        _focusEndTimeMs.value = null
        _focusRemainingSeconds.value = 0
    }

    fun addBlockedApp(packageName: String, label: String) {
        viewModelScope.launch {
            repository.insertBlockedApp(BlockedAppEntity(packageName, label))
        }
    }

    fun removeBlockedApp(packageName: String) {
        viewModelScope.launch {
            repository.deleteBlockedApp(packageName)
        }
    }

    fun toggleBlockedApp(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            val current = blockedApps.value.find { it.packageName == packageName } ?: return@launch
            repository.updateBlockedApp(
                BlockedAppEntity(current.packageName, current.label, enabled)
            )
        }
    }

    fun addSchedule(schedule: BlockSchedule) {
        viewModelScope.launch {
            repository.insertSchedule(
                BlockScheduleEntity(
                    id = schedule.id,
                    name = schedule.name,
                    startHour = schedule.startHour,
                    startMinute = schedule.startMinute,
                    endHour = schedule.endHour,
                    endMinute = schedule.endMinute,
                    activeDays = schedule.activeDays.joinToString(","),
                    isEnabled = schedule.isEnabled
                )
            )
        }
    }

    fun deleteSchedule(id: String) {
        viewModelScope.launch {
            repository.deleteSchedule(id)
        }
    }

    fun getInstalledApps(): List<Pair<String, String>> = _installedApps.value
}
