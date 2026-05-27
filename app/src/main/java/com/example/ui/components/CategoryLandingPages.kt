package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoutineItem
import kotlinx.coroutines.delay
import java.util.Locale

// ==========================================
// 1. CLOCK SCREEN (SNOOZE / TIMER / STOPWATCH)
// ==========================================
@Composable
fun ClockScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("stopwatch") } // "stopwatch" or "timer"

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        .testTag("clock_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to dashboard",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "SNOOZE CLOCK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Alarms, Timer & Stopwatch",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Selectors
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    .padding(6.dp)
            ) {
                // Stopwatch tab
                val isStopwatch = selectedTab == "stopwatch"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isStopwatch) Color(0xFF38BDF8) else Color.Transparent)
                        .clickable { selectedTab = "stopwatch" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Stopwatch tab",
                            tint = if (isStopwatch) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stopwatch",
                            fontWeight = FontWeight.Bold,
                            color = if (isStopwatch) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Timer tab
                val isTimer = selectedTab == "timer"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isTimer) Color(0xFF38BDF8) else Color.Transparent)
                        .clickable { selectedTab = "timer" }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Timer tab",
                            tint = if (isTimer) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Timer",
                            fontWeight = FontWeight.Bold,
                            color = if (isTimer) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == "stopwatch") {
                StopwatchWidget()
            } else {
                TimerWidget()
            }
        }
    }
}

@Composable
fun StopwatchWidget() {
    var timeInMs by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var lapsByGroup by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            var lastUpdate = System.currentTimeMillis()
            while (isRunning) {
                delay(80)
                val now = System.currentTimeMillis()
                timeInMs += (now - lastUpdate)
                lastUpdate = now
            }
        }
    }

    // Calculations
    val min = (timeInMs / 60000) % 60
    val sec = (timeInMs / 1000) % 60
    val ms = (timeInMs / 10) % 100
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d.%02d", min, sec, ms)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Grand dial counter
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(Color(0xFF38BDF8).copy(alpha = 0.04f))
                .border(2.5.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), CircleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Neon rotating dial highlight if running
            if (isRunning) {
                CircularProgressIndicator(
                    progress = { (sec.toFloat() / 60f) },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF38BDF8),
                    strokeWidth = 4.dp,
                    trackColor = Color.Transparent,
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SW CHECKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Main Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lap / Reset button
            Button(
                onClick = {
                    if (isRunning) {
                        lapsByGroup = listOf(timeFormatted) + lapsByGroup
                    } else {
                        // Reset
                        timeInMs = 0L
                        lapsByGroup = emptyList()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("stopwatch_lap_reset_button")
            ) {
                Text(
                    text = if (isRunning) "Lap" else "Reset",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Play/Pause button
            Button(
                onClick = { isRunning = !isRunning },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFFF87171) else Color(0xFF38BDF8),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(54.dp)
                    .border(2.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .testTag("stopwatch_play_pause_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle stopwatch",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "Pause" else "Start",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lap list
        if (lapsByGroup.isNotEmpty()) {
            Text(
                text = "LAP RECORDS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(lapsByGroup.take(50).withIndex().toList()) { (index, lapTime) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lap ${lapsByGroup.size - index}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = lapTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimerWidget() {
    var inputSliderSeconds by remember { mutableStateOf(300f) }
    var selectedSeconds by remember { mutableStateOf(300L) } // 5 mins in seconds default
    var initialMaxSeconds by remember { mutableStateOf(300L) }
    var secondsLeft by remember { mutableLongStateOf(300L) }
    var timerRunning by remember { mutableStateOf(false) }
    var timerFinishedAlert by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerRunning && secondsLeft > 0) {
                delay(1000)
                if (timerRunning) {
                    secondsLeft -= 1
                    if (secondsLeft == 0L) {
                        timerRunning = false
                        timerFinishedAlert = true
                    }
                }
            }
        }
    }

    // Synchronize seconds when slider is picked and timer is idle
    if (!timerRunning && secondsLeft == selectedSeconds) {
        // synced
    }

    val min = secondsLeft / 60
    val sec = secondsLeft % 60
    val progressOfTimer = if (initialMaxSeconds == 0L) 1f else secondsLeft.toFloat() / initialMaxSeconds.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dial countdown
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(Color(0xFF38BDF8).copy(alpha = 0.04f))
                .border(2.5.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), CircleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Neon progress indicator
            CircularProgressIndicator(
                progress = { progressOfTimer },
                modifier = Modifier.fillMaxSize(),
                color = if (timerFinishedAlert) Color(0xFF34D399) else Color(0xFF38BDF8),
                strokeWidth = 6.dp,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val clockFormat = String.format(Locale.getDefault(), "%02d:%02d", min, sec)
                Text(
                    text = clockFormat,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = if (timerFinishedAlert) Color(0xFF34D399) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (timerFinishedAlert) "FINISHED!" else "REMAINING",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (timerFinishedAlert) Color(0xFF34D399) else Color(0xFF38BDF8),
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Presets selector
        if (!timerRunning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val presets = listOf(
                    Pair("1 min", 60L),
                    Pair("5 min", 300L),
                    Pair("10 min", 600L),
                    Pair("15 min", 900L)
                )
                presets.forEach { (label, duration) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (selectedSeconds == duration) Color(0xFF38BDF8).copy(alpha = 0.16f)
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                            )
                            .border(
                                1.dp,
                                if (selectedSeconds == duration) Color(0xFF38BDF8)
                                else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedSeconds = duration
                                initialMaxSeconds = duration
                                secondsLeft = duration
                                timerRunning = false
                                timerFinishedAlert = false
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedSeconds == duration) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom slider for timer duration
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Customize timer length:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${selectedSeconds / 60}m ${selectedSeconds % 60}s",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8)
                    )
                }

                Slider(
                    value = selectedSeconds.toFloat(),
                    onValueChange = { newVal ->
                        val length = newVal.toLong()
                        selectedSeconds = length
                        initialMaxSeconds = length
                        secondsLeft = length
                        timerFinishedAlert = false
                    },
                    valueRange = 10f..1800f, // 10s up to 30 mins
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color(0xFF38BDF8),
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        thumbColor = Color(0xFF38BDF8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Counter Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Secondary Reset
            IconButton(
                onClick = {
                    timerRunning = false
                    secondsLeft = selectedSeconds
                    timerFinishedAlert = false
                },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset countdown",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(24.dp))

            // Main Trigger
            Button(
                onClick = {
                    if (timerFinishedAlert) {
                        timerFinishedAlert = false
                        secondsLeft = selectedSeconds
                    } else {
                        timerRunning = !timerRunning
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerRunning) Color(0xFFF87171) else Color(0xFF38BDF8),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(180.dp)
                    .height(54.dp)
                    .testTag("timer_trigger_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Trigger countdown clock",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (timerRunning) "Pause" else "Start Timer",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}


// ==========================================
// 2. MORNING ROUTINE SCREEN
// ==========================================
@Composable
fun MorningRoutineScreen(
    tasks: List<RoutineItem>,
    isAILoading: Boolean,
    onToggleComplete: (RoutineItem) -> Unit,
    onDelete: (Int) -> Unit,
    onAskAI: () -> Unit,
    onAddTask: () -> Unit,
    onBack: () -> Unit
) {
    var breathingActive by remember { mutableStateOf(false) }
    var breathePhase by remember { mutableStateOf("Ready") } // "Ready", "Inhale", "Hold", "Exhale"
    var breatheTimer by remember { mutableStateOf(0) }

    LaunchedEffect(breathingActive) {
        if (breathingActive) {
            breathePhase = "Ready"
            breatheTimer = 2
            while (breathingActive) {
                delay(1000)
                if (breatheTimer > 0) {
                    breatheTimer -= 1
                } else {
                    when (breathePhase) {
                        "Ready", "Exhale" -> {
                            breathePhase = "Inhale"
                            breatheTimer = 4
                        }
                        "Inhale" -> {
                            breathePhase = "Hold"
                            breatheTimer = 4
                        }
                        "Hold" -> {
                            breathePhase = "Exhale"
                            breatheTimer = 4
                        }
                        else -> {
                            breathePhase = "Inhale"
                            breatheTimer = 4
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        .testTag("morning_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to dashboard",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "MORNING ROUTINES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFDE047),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Rise and Shine Mindfully",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task completions card
            item {
                val completedCount = tasks.count { it.isCompleted }
                val totalCount = tasks.size
                val ratio = if (totalCount == 0) 1f else completedCount.toFloat() / totalCount

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${(ratio * 100).toInt()}% Done",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFDE047)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (totalCount == 0) "No tasks" else "$completedCount of $totalCount blocks finished",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        CircularProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier.size(54.dp),
                            color = Color(0xFFFDE047),
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                            strokeWidth = 5.dp
                        )
                    }
                }
            }

            // Interactive Breathing Tool
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (breathingActive) Color(0xFF1E293B) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                    ),
                    border = if (breathingActive) BorderStroke(1.5.dp, Color(0xFF22D3EE)) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌤️ MIND MINUTE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF22D3EE),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Breathe & Focus",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (breathingActive) {
                            val circleScale by animateFloatAsState(
                                targetValue = when (breathePhase) {
                                    "Inhale" -> 1.4f
                                    "Hold" -> 1.4f
                                    "Exhale" -> 0.8f
                                    else -> 1f
                                },
                                animationSpec = tween(durationMillis = 4000, easing = LinearEasing),
                                label = "pulse"
                            )

                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(circleScale)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(Color(0xFF22D3EE).copy(alpha = 0.5f), Color.Transparent)
                                        )
                                    )
                                    .border(2.dp, Color(0xFF22D3EE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$breatheTimer",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = when (breathePhase) {
                                    "Inhale" -> "💨 Breathe IN slowly..."
                                    "Hold" -> "✋ Hold your breath..."
                                    "Exhale" -> "💨 Breathe OUT completely..."
                                    else -> "Starting breathing loop..."
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    breathingActive = false
                                    breathePhase = "Ready"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF87171)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("End Mind Session", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(
                                text = "Start your day with a rapid 1-minute conscious diaphragm breathing workout to increase arterial oxygen and mood.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    breathingActive = true
                                    breathePhase = "Ready"
                                    breatheTimer = 2
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22D3EE)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("morning_breathe_button")
                            ) {
                                Text("Deep Breathe Guidance", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // Checklist lists heading
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MORNING CHECKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFDE047),
                        letterSpacing = 1.sp
                    )

                    Row {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFDE047).copy(alpha = 0.15f))
                                .clickable(onClick = onAskAI)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isAILoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = Color(0xFFFDE047))
                                } else {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFDE047), modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Ideas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .clickable(onClick = onAddTask)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add block", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Morning tasks items list
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No morning tasks listed. Tap \"AI Ideas\" or \"Add block\" to design routines!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(tasks) { task ->
                    RoutineTaskRow(
                        task = task,
                        accentColor = Color(0xFFFDE047),
                        onToggleComplete = { onToggleComplete(task) },
                        onDelete = { onDelete(task.id) }
                    )
                }
            }
        }
    }
}


// ==========================================
// 3. EXERCISE SCREEN (WORKOUTS / COMPLETED TIMERS)
// ==========================================
@Composable
fun ExerciseScreen(
    tasks: List<RoutineItem>,
    isAILoading: Boolean,
    onToggleComplete: (RoutineItem) -> Unit,
    onDelete: (Int) -> Unit,
    onAskAI: () -> Unit,
    onAddTask: () -> Unit,
    onBack: () -> Unit
) {
    var calsBurned by remember { mutableStateOf(0) }
    var exerciseTimerActive by remember { mutableStateOf(false) }
    var secondsRemaining by remember { mutableStateOf(30) }
    var activeExerciseName by remember { mutableStateOf("Planks Set") }

    LaunchedEffect(exerciseTimerActive) {
        if (exerciseTimerActive) {
            while (exerciseTimerActive && secondsRemaining > 0) {
                delay(1000)
                if (exerciseTimerActive) {
                    secondsRemaining -= 1
                    if (secondsRemaining == 0) {
                        exerciseTimerActive = false
                        calsBurned += 15 // complete a timer = +15 kcal!
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        .testTag("exercise_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to dashboard",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "HEALTH & EXERCISE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF87171),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Daily Body & Mind Training",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calorie simulator and completions stats in one
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF87171).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Burned fat indicator",
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "$calsBurned KCAL",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFF87171)
                                )
                                Text(
                                    text = "Estimated burned calories",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Completion indicator ratios
                        val totalTasks = tasks.size
                        val doneTasks = tasks.count { it.isCompleted }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$doneTasks/$totalTasks Done",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                    text = "Fitness tasks",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Interactive Workout Set Trainer
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (exerciseTimerActive) Color(0xFF2C2525) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                    ),
                    border = if (exerciseTimerActive) BorderStroke(2.dp, Color(0xFFF87171)) else null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⏱️ ACTIVE FITNESS INTERVAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFF87171),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (exerciseTimerActive) "Active Drill" else "Standby",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (exerciseTimerActive) {
                            Text(
                                text = activeExerciseName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF87171).copy(alpha = 0.08f))
                                    .border(2.5.dp, Color(0xFFF87171), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${secondsRemaining}s",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Train with intensive full-force! Once the timer ticks out you earn +15 kcal.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { exerciseTimerActive = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Abort Interval", color = Color.White)
                            }
                        } else {
                            Text(
                                text = "Select physical routine drill below to launch customized 30s interval countdown. Complete to gain burn points!",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 2 Column quick presets launcher
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val drills = listOf("Jumping Jacks", "High Knees", "Push-ups", "Air Squats")
                                drills.forEach { drill ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF87171).copy(alpha = 0.15f))
                                            .clickable {
                                                activeExerciseName = drill
                                                secondsRemaining = 30
                                                exerciseTimerActive = true
                                            }
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Go $drill",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Exercises checklist title row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ROUTINE FITNESS CLOCKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFF87171),
                        letterSpacing = 1.sp
                    )

                    Row {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF87171).copy(alpha = 0.15f))
                                .clickable(onClick = onAskAI)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isAILoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = Color(0xFFF87171))
                                } else {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("AI Ideas", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .clickable(onClick = onAddTask)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add block", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // Exercise tasks list items
            if (tasks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No custom exercises listed yet. Tap \"AI Ideas\" or create items representing your goals!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(tasks) { task ->
                    RoutineTaskRow(
                        task = task,
                        accentColor = Color(0xFFF87171),
                        onToggleComplete = { onToggleComplete(task) },
                        onDelete = { onDelete(task.id) }
                    )
                }
            }
        }
    }
}
