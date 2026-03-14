

package com.prafullkumar.orbit.home.habits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.orbit.home.habits.data.repository.HabitRepository
import com.prafullkumar.orbit.home.habits.model.EnvironmentTag
import com.prafullkumar.orbit.home.habits.model.FailureReason
import com.prafullkumar.orbit.home.habits.model.EnergyLevel
import com.prafullkumar.orbit.home.habits.model.Habit
import com.prafullkumar.orbit.home.habits.model.HabitDifficulty
import com.prafullkumar.orbit.home.habits.model.HabitLog
import com.prafullkumar.orbit.home.habits.model.HabitStats

import com.prafullkumar.orbit.home.habits.model.HabitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HabitDashboardState(
    val habits: List<Habit> = emptyList(),
    val todayCompletions: Set<Long> = emptySet(),
    val momentumScore: Int = 0,
    val stackingSuggestions: List<Pair<String, String>> = emptyList(),
    val isLoading: Boolean = true,
    val showOnboarding: Boolean = false
)

data class AddEditHabitState(
    val id: Long = 0,
    val name: String = "",
    val habitType: HabitType = HabitType.DAILY,
    val difficulty: HabitDifficulty = HabitDifficulty.MEDIUM,
    val targetValue: Int = 1,
    val targetDays: List<Int> = emptyList(),
    val unit: String = "",
    val environment: EnvironmentTag = EnvironmentTag.ANY,
    val note: String = "",
    val step: Int = 0,         // Multi-step form page index
    val isSaving: Boolean = false
)

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val today = LocalDate.now().toEpochDay()

    val habits: StateFlow<List<Habit>> = repository.getAllHabitsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dashboardState = MutableStateFlow(HabitDashboardState())
    val dashboardState: StateFlow<HabitDashboardState> = _dashboardState.asStateFlow()

    private val _addEditState = MutableStateFlow(AddEditHabitState())
    val addEditState: StateFlow<AddEditHabitState> = _addEditState.asStateFlow()

    private val _selectedHabitStats = MutableStateFlow<HabitStats?>(null)
    val selectedHabitStats: StateFlow<HabitStats?> = _selectedHabitStats.asStateFlow()

    init {
        observeHabits()
    }

    private fun observeHabits() {
        viewModelScope.launch {
            habits.collect { habitList ->
                val momentumScore = repository.getOverallMomentumScore(habitList)
                val stackingSuggestions = repository.buildStackingSuggestions(habitList)
                val showOnboarding = false
                _dashboardState.update {
                    it.copy(
                        habits = habitList,
                        momentumScore = momentumScore,
                        stackingSuggestions = stackingSuggestions,
                        isLoading = false,
                        showOnboarding = showOnboarding
                    )
                }
            }
        }
    }

    fun toggleHabitCompletion(
        habit: Habit,
        environment: EnvironmentTag = EnvironmentTag.ANY,
        onResult: (isNowCompleted: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.toggleHabitForToday(habit, today, environment)
            onResult(result)
        }
    }

    fun saveFailureAnalysis(
        habitId: Long,
        reason: FailureReason,
        energyLevel: EnergyLevel,
        notes: String
    ) {
        viewModelScope.launch {
            val log = HabitLog(
                habitId = habitId,
                epochDay = today,
                completed = false,
                failureReason = reason,
                energyLevel = energyLevel,
                notes = notes
            )
            repository.saveLog(log)
        }
    }

    fun loadHabitStats(habit: Habit) {
        viewModelScope.launch {
            _selectedHabitStats.value = repository.getHabitStats(habit)
        }
    }

    // ── AddEdit form ────────────────────────────────────────────────────────

    fun resetAddEditForm(habit: Habit? = null) {
        _addEditState.value = if (habit != null) {
            AddEditHabitState(
                id = habit.id, name = habit.name, habitType = habit.habitType,
                difficulty = habit.difficulty, targetValue = habit.targetValue,
                targetDays = habit.targetDays, unit = habit.unit,
                environment = habit.defaultEnvironment, note = habit.note
            )
        } else {
            AddEditHabitState()
        }
    }

    fun updateName(name: String) = _addEditState.update { it.copy(name = name) }
    fun updateHabitType(type: HabitType) = _addEditState.update { it.copy(habitType = type) }
    fun updateDifficulty(d: HabitDifficulty) = _addEditState.update { it.copy(difficulty = d) }
    fun updateTargetValue(v: Int) = _addEditState.update { it.copy(targetValue = v) }
    fun toggleTargetDay(day: Int) = _addEditState.update {
        val days = it.targetDays.toMutableList()
        if (day in days) days.remove(day) else days.add(day)
        it.copy(targetDays = days.sorted())
    }
    fun updateUnit(unit: String) = _addEditState.update { it.copy(unit = unit) }
    fun updateEnvironment(env: EnvironmentTag) = _addEditState.update { it.copy(environment = env) }
    fun updateNote(note: String) = _addEditState.update { it.copy(note = note) }
    fun nextStep() = _addEditState.update { it.copy(step = it.step + 1) }
    fun prevStep() = _addEditState.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }

    fun saveHabit(onDone: () -> Unit) {
        val s = _addEditState.value
        if (s.name.isBlank()) return
        _addEditState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val habit = Habit(
                id = s.id, name = s.name.trim(), habitType = s.habitType,
                difficulty = s.difficulty, targetValue = s.targetValue,
                targetDays = s.targetDays, unit = s.unit,
                defaultEnvironment = s.environment, note = s.note
            )
            if (s.id == 0L) repository.insertHabit(habit) else repository.updateHabit(habit)
            _addEditState.update { it.copy(isSaving = false) }
            onDone()
        }
    }

    fun deleteHabit(id: Long) {
        viewModelScope.launch { repository.deleteHabit(id) }
    }
}
