package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.RoutineRepository
import com.example.model.RoutineItem
import kotlinx.coroutines.flow.Flow
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

    data class CustomSong(
        val id: Int,
        val title: String,
        val artist: String = "Unknown",
        val uriStr: String? = null,
        val isLocked: Boolean = false,
        val isEmptySlot: Boolean = false
    )

    private val repository: RoutineRepository

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // Music Player state flows
    private val _songsList = MutableStateFlow<List<CustomSong>>(
        listOf(
            CustomSong(1, "Morning Radiance 🌅", "Stay Raxo Ambient"),
            CustomSong(2, "Deep Focus Flow 🧘", "Zen Lofi Beats"),
            CustomSong(3, "Stay Raxo Chill 🍹", "Cosmic Focus"),
            CustomSong(4, "Import Custom Audio 1", "Available Slot", isEmptySlot = true),
            CustomSong(5, "Import Custom Audio 2", "Available Slot", isEmptySlot = true),
            CustomSong(6, "Locked Premium Slot 6 🔒", "Premium Only", isLocked = true),
            CustomSong(7, "Locked Premium Slot 7 🔒", "Premium Only", isLocked = true),
            CustomSong(8, "Locked Premium Slot 8 🔒", "Premium Only", isLocked = true),
            CustomSong(9, "Locked Premium Slot 9 🔒", "Premium Only", isLocked = true),
            CustomSong(10, "Locked Premium Slot 10 🔒", "Premium Only", isLocked = true)
        )
    )
    val songsList: StateFlow<List<CustomSong>> = _songsList.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0L)
    val playbackProgress: StateFlow<Long> = _playbackProgress.asStateFlow()

    private val _playbackDuration = MutableStateFlow(160L) // Default 2:40
    val playbackDuration: StateFlow<Long> = _playbackDuration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSongIndex = MutableStateFlow(0)
    val currentSongIndex: StateFlow<Int> = _currentSongIndex.asStateFlow()

    private var mediaPlayer: android.media.MediaPlayer? = null
    private var progressJob: kotlinx.coroutines.Job? = null

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                kotlinx.coroutines.delay(1000)
                if (_isPlaying.value) {
                    val currentProg = _playbackProgress.value
                    val duration = _playbackDuration.value
                    if (currentProg < duration) {
                        _playbackProgress.value = currentProg + 1
                        mediaPlayer?.let {
                            if (it.isPlaying) {
                                _playbackProgress.value = (it.currentPosition / 1000L).coerceAtMost(duration)
                            }
                        }
                    } else {
                        nextSong()
                    }
                }
            }
        }
    }

    fun previousSong() {
        val songs = _songsList.value
        var newIndex = _currentSongIndex.value - 1
        if (newIndex < 0) {
            newIndex = 4 // wrap to last of the 5 allowed slots (0..4)
        }
        while (newIndex >= 0 && songs[newIndex].isLocked) {
            newIndex--
        }
        if (newIndex < 0) newIndex = 0
        selectSong(newIndex)
    }

    fun nextSong() {
        val songs = _songsList.value
        var newIndex = _currentSongIndex.value + 1
        if (newIndex > 4 || songs[newIndex].isLocked) {
            newIndex = 0
        }
        selectSong(newIndex)
    }

    fun selectSong(index: Int) {
        if (index < 0 || index >= _songsList.value.size) return
        val song = _songsList.value[index]
        if (song.isLocked) return // blocked in premium

        _currentSongIndex.value = index
        _playbackProgress.value = 0L

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (song.uriStr != null) {
            try {
                mediaPlayer = android.media.MediaPlayer().apply {
                    setDataSource(getApplication(), android.net.Uri.parse(song.uriStr))
                    prepare()
                    _playbackDuration.value = (duration / 1000L).coerceAtLeast(30L)
                    if (_isPlaying.value) {
                        start()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // simulation fallback if format is unsupportable
                _playbackDuration.value = 180L
            }
        } else {
            _playbackDuration.value = when (index) {
                0 -> 160L
                1 -> 210L
                2 -> 190L
                else -> 180L
            }
        }

        if (_isPlaying.value) {
            if (song.uriStr != null) {
                try {
                    mediaPlayer?.start()
                } catch (e: Exception) {}
            }
            startProgressTicker()
        }
    }

    fun togglePlayPause() {
        val currentIsPlaying = _isPlaying.value
        _isPlaying.value = !currentIsPlaying

        if (_isPlaying.value) {
            val song = _songsList.value[_currentSongIndex.value]
            if (song.uriStr != null) {
                try {
                    if (mediaPlayer == null) {
                        selectSong(_currentSongIndex.value)
                    }
                    mediaPlayer?.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            startProgressTicker()
        } else {
            mediaPlayer?.pause()
            progressJob?.cancel()
        }
    }

    fun updateSongInSlot(index: Int, title: String, uriStr: String) {
        val currentSongs = _songsList.value.toMutableList()
        if (index in 0 until currentSongs.size && !currentSongs[index].isLocked) {
            currentSongs[index] = CustomSong(
                id = index + 1,
                title = title,
                artist = "Local Import 🎵",
                uriStr = uriStr,
                isEmptySlot = false
            )
            _songsList.value = currentSongs
            selectSong(index)
            if (!_isPlaying.value) {
                togglePlayPause()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {}
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

    fun getRoutinesForDateFlow(date: String): Flow<List<RoutineItem>> {
        return repository.getRoutinesForDate(date)
    }

    val allRoutines: StateFlow<List<RoutineItem>> = repository.getAllRoutines()
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

    fun checkAndResetToToday() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (_currentDate.value != today) {
            _currentDate.value = today
            viewModelScope.launch {
                repository.prepopulateIfEmpty(today)
            }
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
