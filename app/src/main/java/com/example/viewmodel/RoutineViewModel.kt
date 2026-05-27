package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RoutineRepository
import com.example.model.RoutineItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class RoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoutineRepository

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    private val _currentDate = MutableStateFlow("")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _motivationalQuote = MutableStateFlow("Loading your morning inspiration...")
    val motivationalQuote: StateFlow<String> = _motivationalQuote.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    private val _aiSuggestion = MutableStateFlow<String?>(null)
    val aiSuggestion: StateFlow<String?> = _aiSuggestion.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = RoutineRepository(database.routineDao())

        // Set current date to today formatted as yyyy-MM-dd
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        _currentDate.value = today

        viewModelScope.launch {
            repository.prepopulateIfEmpty(today)
            refreshQuote()
        }
    }

    val routines: StateFlow<List<RoutineItem>> = _currentDate
        .flatMapLatest { date ->
            repository.getRoutinesForDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setDate(date: String) {
        _currentDate.value = date
        viewModelScope.launch {
            repository.prepopulateIfEmpty(date)
        }
    }

    fun toggleComplete(item: RoutineItem) {
        viewModelScope.launch {
            repository.update(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun incrementWater(item: RoutineItem) {
        if (item.completedCount < item.targetCount) {
            viewModelScope.launch {
                repository.update(item.copy(
                    completedCount = item.completedCount + 1,
                    isCompleted = (item.completedCount + 1 >= item.targetCount)
                ))
            }
        }
    }

    fun decrementWater(item: RoutineItem) {
        if (item.completedCount > 0) {
            viewModelScope.launch {
                repository.update(item.copy(
                    completedCount = item.completedCount - 1,
                    isCompleted = (item.completedCount - 1 >= item.targetCount)
                ))
            }
        }
    }

    fun updateWaterTarget(item: RoutineItem, targetCount: Int) {
        viewModelScope.launch {
            val adjustedCompleted = if (item.completedCount > targetCount) targetCount else item.completedCount
            repository.update(item.copy(
                targetCount = targetCount,
                completedCount = adjustedCompleted,
                isCompleted = (adjustedCompleted >= targetCount)
            ))
        }
    }

    fun refreshQuote() {
        viewModelScope.launch {
            _isAILoading.value = true
            val quote = repository.fetchAIMotivationalQuote()
            _motivationalQuote.value = quote
            _isAILoading.value = false
        }
    }

    fun fetchAISuggestion(category: String) {
        viewModelScope.launch {
            _isAILoading.value = true
            val suggestion = repository.fetchAISuggestion(category)
            _aiSuggestion.value = suggestion
            _isAILoading.value = false
        }
    }

    fun clearAISuggestion() {
        _aiSuggestion.value = null
    }

    fun addCustomRoutine(title: String, category: String, scheduledTime: String, notes: String) {
        viewModelScope.launch {
            val item = RoutineItem(
                title = title,
                category = category,
                scheduledTime = if (scheduledTime.isBlank()) "Anytime" else scheduledTime,
                notes = if (notes.isBlank()) "Stay consistent today." else notes,
                dateString = _currentDate.value
            )
            repository.insert(item)
        }
    }

    fun deleteRoutine(id: Int) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }
}
