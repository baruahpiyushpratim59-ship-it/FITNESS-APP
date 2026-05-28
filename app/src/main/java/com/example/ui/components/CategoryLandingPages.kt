package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.app.NotificationManager
import android.graphics.Color as AndroidColor
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoutineItem
import kotlinx.coroutines.delay
import java.util.Locale

// ==========================================
// 1.5. STUDY & SLEEP SCREENS
// ==========================================
@Composable
fun StudyScreen(onBack: () -> Unit) {
    var timerRunning by remember { mutableStateOf(false) }
    var hours by remember { mutableLongStateOf(0L) }
    var minutes by remember { mutableLongStateOf(25L) }
    var seconds by remember { mutableLongStateOf(0L) }
    var totalSecondsRemaining by remember { mutableLongStateOf(25L * 60) }
    var taskText by remember { mutableStateOf("Reading Science") }
    var isTaskSet by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (timerRunning && totalSecondsRemaining > 0) {
                delay(1000)
                totalSecondsRemaining--
                if (totalSecondsRemaining <= 0) timerRunning = false
            }
        }
    }

    val displayH = totalSecondsRemaining / 3600
    val displayM = (totalSecondsRemaining % 3600) / 60
    val displayS = totalSecondsRemaining % 60

    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (isDark) listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                    else listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = contentColor)
                }
                Text(
                    "STUDY SESSION",
                    style = MaterialTheme.typography.headlineMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Timer Display
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFF38BDF8), CircleShape)
                    .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.getDefault(), "%02d:%02d:%02d", displayH, displayM, displayS),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light,
                        color = contentColor,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Text(
                        text = if (timerRunning) "FOCUSING..." else "READY",
                        fontSize = 14.sp,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (!timerRunning) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Set Study Duration", color = Color.Gray, fontSize = 14.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NumberPicker(value = hours, onValueChange = { hours = it; totalSecondsRemaining = it * 3600 + minutes * 60 + seconds }, label = "H")
                        Text(":", color = contentColor, fontSize = 24.sp, modifier = Modifier.padding(horizontal = 8.dp))
                        NumberPicker(value = minutes, onValueChange = { minutes = it; totalSecondsRemaining = hours * 3600 + it * 60 + seconds }, label = "M")
                        Text(":", color = contentColor, fontSize = 24.sp, modifier = Modifier.padding(horizontal = 8.dp))
                        NumberPicker(value = seconds, onValueChange = { seconds = it; totalSecondsRemaining = hours * 3600 + minutes * 60 + it }, label = "S")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = taskText,
                        onValueChange = { taskText = it },
                        label = { Text("What are you studying?") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = contentColor,
                            focusedTextColor = contentColor,
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color(0xFF38BDF8)
                        )
                    )
                }
            } else {
                Text(
                    text = "Working on: $taskText",
                    color = contentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { timerRunning = !timerRunning },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerRunning) Color(0xFFEF4444) else Color(0xFF38BDF8),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (timerRunning) "STOP" else "START FOCUS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun NumberPicker(value: Long, onValueChange: (Long) -> Unit, label: String) {
    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF0F172A)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { onValueChange(value + 1) }) {
            Icon(Icons.Default.Add, contentDescription = null, tint = contentColor)
        }
        Text(text = String.format(Locale.getDefault(), "%02d", value), color = contentColor, fontSize = 24.sp)
        IconButton(onClick = { if (value > 0) onValueChange(value - 1) }) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = contentColor)
        }
        Text(text = label, color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun SleepScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedSleepMode by remember { mutableStateOf<String?>(null) }
    var sleepTimerRunning by remember { mutableStateOf(false) }
    var alarmTime by remember { mutableStateOf("07:00") }
    var sleepScore by remember { mutableStateOf(100) }
    var phoneUsageDetected by remember { mutableStateOf(false) }

    // DND Management
    fun setDND(enabled: Boolean) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notificationManager?.isNotificationPolicyAccessGranted == true) {
            notificationManager.setInterruptionFilter(if (enabled) {
                NotificationManager.INTERRUPTION_FILTER_NONE
            } else {
                NotificationManager.INTERRUPTION_FILTER_ALL
            })
        }
    }

    // Phone movement tracking
    DisposableEffect(sleepTimerRunning) {
        if (sleepTimerRunning) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val accel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val listener = object : SensorEventListener {
                var lastX = 0f
                var lastY = 0f
                var lastZ = 0f
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    if (lastX != 0f) {
                        val delta = Math.sqrt(((x - lastX) * (x - lastX) + (y - lastY) * (y - lastY) + (z - lastZ) * (z - lastZ)).toDouble())
                        if (delta > 0.8) {
                            phoneUsageDetected = true
                            // Score logic
                            if (sleepScore > 0) sleepScore -= 5
                        }
                    }
                    lastX = x; lastY = y; lastZ = z
                }
                override fun onAccuracyChanged(s: Sensor?, a: Int) {}
            }
            sensorManager?.registerListener(listener, accel, SensorManager.SENSOR_DELAY_NORMAL)
            onDispose {
                sensorManager?.unregisterListener(listener)
            }
        } else {
            onDispose {}
        }
    }

    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (isDark) listOf(Color(0xFF020617), Color(0xFF1E1B4B))
                    else listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (selectedSleepMode != null && !sleepTimerRunning) selectedSleepMode = null else onBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = contentColor)
                }
                Text(
                    "SLEEP TRACKER",
                    style = MaterialTheme.typography.headlineMedium,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (selectedSleepMode == null) {
                Text("Select Sleep Quality", color = Color.Gray, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SleepModeCard(
                        title = "Light Sleep",
                        icon = Icons.Default.Bedtime,
                        color = Color(0xFF60A5FA),
                        onClick = { selectedSleepMode = "Light" },
                        modifier = Modifier.weight(1f)
                    )
                    SleepModeCard(
                        title = "Deep Sleep",
                        icon = Icons.Default.AutoAwesome,
                        color = Color(0xFFA78BFA),
                        onClick = { selectedSleepMode = "Deep" },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // Active Sleep Tracker
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedSleepMode == "Deep") Icons.Default.AutoAwesome else Icons.Default.Bedtime,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = if (selectedSleepMode == "Deep") Color(0xFFA78BFA) else Color(0xFF60A5FA)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "$selectedSleepMode Mode Active",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    if (selectedSleepMode == "Deep") {
                        Text("DND & Silent Mode ON", color = Color(0xFFF87171), fontSize = 12.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    Text("Sleep Score", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        text = "$sleepScore",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (sleepScore > 80) Color(0xFF34D399) else if (sleepScore > 50) Color(0xFFFBBF24) else Color(0xFFF87171)
                    )
                    
                    if (phoneUsageDetected) {
                        Text("Movement Detected! Score Decreased.", color = Color(0xFFF87171), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    
                    OutlinedTextField(
                        value = alarmTime,
                        onValueChange = { alarmTime = it },
                        label = { Text("Alarm Time (HH:mm)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = contentColor,
                            focusedTextColor = contentColor,
                            focusedBorderColor = Color(0xFFA78BFA),
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Button(
                        onClick = {
                            if (!sleepTimerRunning) {
                                sleepTimerRunning = true
                                phoneUsageDetected = false
                                if (selectedSleepMode == "Deep") setDND(true)
                            } else {
                                sleepTimerRunning = false
                                if (selectedSleepMode == "Deep") setDND(false)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (sleepTimerRunning) Color(0xFFEF4444) else Color(0xFF34D399),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (sleepTimerRunning) "WAKE UP" else "START SLEEP", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SleepModeCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier) {
    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = if (isDark) Color.White else Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}


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
// 2. MORNING ROUTINE SCREEN (WITH INTERACTIVE PORTALS)
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
    // Sub-navigation state within Morning Routine:
    // "main" = Morning dashboard, "exercise" = list of exercises, "running_workout" = running tools panel, "work_studio" = productivity blocker, "breathing" = deep breath session, "walking_workout" = walking tracker, "yoga_routine" = yoga & stretching, "breakfast_station" = healthy breakfast
    var morningSection by remember { mutableStateOf("main") }

    // Completed recipes for the healthy breakfast station
    var completedBreakfastRecipes by remember { mutableStateOf(setOf<String>()) }

    // High-fidelity animations using InfiniteTransition
    val infiniteTransition = rememberInfiniteTransition(label = "category_animations")

    val runBounceY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "runBounce"
    )
    val runRotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "runRotation"
    )

    val walkSwayX by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "walkSway"
    )
    val walkRotation by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "walkRotation"
    )

    val jumpBounceY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jumpBounce"
    )
    val jumpSquishY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jumpSquish"
    )

    val stretchScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stretchScale"
    )

    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    val context = LocalContext.current

    // GPS real-time status details
    var currentLatitude by remember { mutableStateOf<Double?>(null) }
    var currentLongitude by remember { mutableStateOf<Double?>(null) }
    var gpsAccuracy by remember { mutableStateOf<Float?>(null) }
    var gpsStatusMessage by remember { mutableStateOf("GPS Status: Idle (Tap Start)") }
    var isLocationPermissionGranted by remember {
        mutableStateOf(
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher for Location and Sensors
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    // Automatic permission checker on state transitions
    LaunchedEffect(morningSection) {
        if (morningSection == "running_workout" || morningSection == "walking_workout") {
            if (!isLocationPermissionGranted) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    )
                )
            }
        }
    }

    // Running actual metrics (No fake increments!)
    var runKcal by remember { mutableStateOf(0f) }
    var runSteps by remember { mutableStateOf(0) }
    var runKm by remember { mutableStateOf(0.00f) }

    // Running Customizable Timer states
    var runTimerDurationMinutes by remember { mutableStateOf(10) }
    var runTimerRemainingMs by remember { mutableStateOf(10 * 60 * 1000L) }
    var runTimerRunning by remember { mutableStateOf(false) }

    // Running Stopwatch states
    var stopwatchMs by remember { mutableStateOf(0L) }
    var stopwatchRunning by remember { mutableStateOf(false) }

    // Walking actual metrics (No fake increments!)
    var walkKcal by remember { mutableStateOf(0f) }
    var walkSteps by remember { mutableStateOf(0) }
    var walkKm by remember { mutableStateOf(0.00f) }

    // Walking Customizable Timer states
    var walkTimerDurationMinutes by remember { mutableStateOf(15) }
    var walkTimerRemainingMs by remember { mutableStateOf(15 * 60 * 1000L) }
    var walkTimerRunning by remember { mutableStateOf(false) }

    // Walking Stopwatch states
    var walkStopwatchMs by remember { mutableStateOf(0L) }
    var walkStopwatchRunning by remember { mutableStateOf(false) }

    // Yoga timer states
    var yogaSelectedType by remember { mutableStateOf("Pranayan") } // "Pranayan", "Stretching", "Jumping"
    var yogaSelectedMinutes by remember { mutableStateOf(5) } // 5 or 10 min
    var yogaTimerRemainingMs by remember { mutableStateOf(5 * 60 * 1000L) }
    var yogaTimerRunning by remember { mutableStateOf(false) }

    // Breakfast states
    var breakfastSelectedRecipe by remember { mutableStateOf<String?>(null) }
    var breakfastMealPrepTimerMs by remember { mutableStateOf(0L) }
    var breakfastMealPrepTimerActive by remember { mutableStateOf(false) }

    // BackHandler to handle system back buttons in sub-screens smoothly
    BackHandler(enabled = morningSection != "main") {
        when (morningSection) {
            "running_workout", "walking_workout", "yoga_routine" -> morningSection = "exercise"
            "exercise", "work_studio", "breathing", "breakfast_station" -> morningSection = "main"
        }
    }

    // Motion steps tracking using accelerometer or direct step sensor
    val isTrackingActive = runTimerRunning || stopwatchRunning || walkTimerRunning || walkStopwatchRunning

    DisposableEffect(isTrackingActive) {
        if (isTrackingActive) {
            var lastStepTime = 0L
            var lastAccelerationMagnitude = 9.81f

            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val stepDetector = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

            val sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event == null) return

                    // Step detector hardware optimization
                    if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                        if (runTimerRunning || stopwatchRunning) {
                            runSteps += 1
                            runKcal += 0.045f
                        } else if (walkTimerRunning || walkStopwatchRunning) {
                            walkSteps += 1
                            walkKcal += 0.032f
                        }
                        return
                    }

                    // Accelerometer peaks threshold algorithm fallback (Highly responsive & simulator compatible)
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                        
                        val diff = magnitude - lastAccelerationMagnitude
                        val curSecs = System.currentTimeMillis()

                        if (magnitude > 11.8f && diff > 1.2f && (curSecs - lastStepTime > 360L)) {
                            if (runTimerRunning || stopwatchRunning) {
                                runSteps += 1
                                runKcal += 0.045f
                            } else if (walkTimerRunning || walkStopwatchRunning) {
                                walkSteps += 1
                                walkKcal += 0.032f
                            }
                            lastStepTime = curSecs
                        }
                        lastAccelerationMagnitude = magnitude
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            if (accelSensor != null) {
                sensorManager.registerListener(sensorEventListener, accelSensor, SensorManager.SENSOR_DELAY_UI)
            }
            if (stepDetector != null) {
                sensorManager.registerListener(sensorEventListener, stepDetector, SensorManager.SENSOR_DELAY_FASTEST)
            }

            onDispose {
                sensorManager?.unregisterListener(sensorEventListener)
            }
        } else {
            onDispose {}
        }
    }

    // Precise GPS tracking for exact metrics
    var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
    DisposableEffect(isTrackingActive, isLocationPermissionGranted) {
        if (isTrackingActive && isLocationPermissionGranted) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            gpsStatusMessage = "GPS: Contacting satellites..."

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    gpsAccuracy = location.accuracy
                    gpsStatusMessage = "GPS: Connected (Acc: ${String.format(Locale.US, "%.1f", location.accuracy)}m)"

                    val prev = lastKnownLocation
                    if (prev != null) {
                        val dist = prev.distanceTo(location) // Dist in meters
                        // Filter: Good accuracy (< 20m), significant distance (> 3m), and must have speed > 0.5 m/s (approx 1.8 km/h)
                        val isMoving = location.hasSpeed() && location.speed > 0.5f
                        if (location.accuracy < 20f && dist > 3.0f && (isMoving || location.speed > 0.5f)) {
                            if (runTimerRunning || stopwatchRunning) {
                                runKm += (dist / 1000f)
                            } else if (walkTimerRunning || walkStopwatchRunning) {
                                walkKm += (dist / 1000f)
                            }
                        }
                    }
                    lastKnownLocation = location
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {
                    gpsStatusMessage = "GPS: Enabled"
                }
                override fun onProviderDisabled(provider: String) {
                    gpsStatusMessage = "GPS: Location services disabled!"
                }
            }

            try {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000L,
                        0.5f,
                        locationListener
                    )
                }
                if (locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1500L,
                        0.5f,
                        locationListener
                    )
                }
            } catch (e: SecurityException) {
                gpsStatusMessage = "GPS: Permissions failed!"
            }

            onDispose {
                try {
                    locationManager?.removeUpdates(locationListener)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                gpsStatusMessage = "GPS: Closed"
                lastKnownLocation = null
            }
        } else {
            onDispose {}
        }
    }

    // Running customizable countdown timer loop
    LaunchedEffect(runTimerRunning) {
        if (runTimerRunning) {
            var lastTime = System.currentTimeMillis()
            while (runTimerRunning && runTimerRemainingMs > 0) {
                delay(100)
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now
                runTimerRemainingMs = (runTimerRemainingMs - delta).coerceAtLeast(0L)
                if (runTimerRemainingMs <= 0L) {
                    runTimerRunning = false
                }
            }
        }
    }

    // Running live stopwatch loop
    LaunchedEffect(stopwatchRunning) {
        if (stopwatchRunning) {
            var lastTime = System.currentTimeMillis()
            while (stopwatchRunning) {
                delay(30)
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now
                stopwatchMs += delta
            }
        }
    }

    // Walking customizable countdown timer loop
    LaunchedEffect(walkTimerRunning) {
        if (walkTimerRunning) {
            var lastTime = System.currentTimeMillis()
            while (walkTimerRunning && walkTimerRemainingMs > 0) {
                delay(100)
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now
                walkTimerRemainingMs = (walkTimerRemainingMs - delta).coerceAtLeast(0L)
                if (walkTimerRemainingMs <= 0L) {
                    walkTimerRunning = false
                }
            }
        }
    }

    // Walking live stopwatch loop
    LaunchedEffect(walkStopwatchRunning) {
        if (walkStopwatchRunning) {
            var lastTime = System.currentTimeMillis()
            while (walkStopwatchRunning) {
                delay(30)
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now
                walkStopwatchMs += delta
            }
        }
    }

    // Yoga timer countdown loop
    LaunchedEffect(yogaTimerRunning) {
        if (yogaTimerRunning) {
            var lastTime = System.currentTimeMillis()
            while (yogaTimerRunning && yogaTimerRemainingMs > 0) {
                delay(100)
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now
                yogaTimerRemainingMs = (yogaTimerRemainingMs - delta).coerceAtLeast(0L)
                if (yogaTimerRemainingMs <= 0L) {
                    yogaTimerRunning = false
                }
            }
        }
    }

    // Breakfast Meal Prep countdown timer
    LaunchedEffect(breakfastMealPrepTimerActive) {
        if (breakfastMealPrepTimerActive) {
            var lastTime = System.currentTimeMillis()
            while (breakfastMealPrepTimerActive && breakfastMealPrepTimerMs > 0) {
                delay(100)
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now
                breakfastMealPrepTimerMs = (breakfastMealPrepTimerMs - delta).coerceAtLeast(0L)
                if (breakfastMealPrepTimerMs <= 0L) {
                    breakfastMealPrepTimerActive = false
                }
            }
        }
    }

    // Work focus Pomodoro countdown loop
    var workSelectedMins by remember { mutableStateOf(25) }
    var workTimerRemainingMs by remember { mutableStateOf(25 * 60 * 1000L) }
    var workTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(workTimerRunning) {
        if (workTimerRunning) {
            var lastTime = System.currentTimeMillis()
            while (workTimerRunning && workTimerRemainingMs > 0) {
                delay(100)
                val now = System.currentTimeMillis()
                val delta = now - lastTime
                lastTime = now
                workTimerRemainingMs = (workTimerRemainingMs - delta).coerceAtLeast(0L)
                if (workTimerRemainingMs <= 0L) {
                    workTimerRunning = false
                }
            }
        }
    }

    // Dynamic work checklist states inside work studio
    var workDeliverables by remember {
        mutableStateOf(
            listOf(
                Pair("Write morning standup bulletin", true),
                Pair("Check visual UI metrics compilation", false),
                Pair("Formulate critical user journeys checklist", false)
            )
        )
    }
    var newWorkItemText by remember { mutableStateOf("") }

    // Guided Deep Breathing states (5 sec inhale, 5 sec hold, 5 sec exhale)
    var selectBreathMins by remember { mutableStateOf(5) } // 5 or 10 min
    var breathActive by remember { mutableStateOf(false) }
    var breathSessionRemainingSecs by remember { mutableStateOf(300) }
    var breathCyclePhase by remember { mutableStateOf("Inhale") } // "Inhale", "Hold", "Exhale"
    var breathPhaseSecondsRemaining by remember { mutableStateOf(5) }

    LaunchedEffect(breathActive) {
        if (breathActive) {
            breathSessionRemainingSecs = selectBreathMins * 60
            breathCyclePhase = "Inhale"
            breathPhaseSecondsRemaining = 5
            while (breathActive && breathSessionRemainingSecs > 0) {
                delay(1000)
                breathSessionRemainingSecs -= 1
                breathPhaseSecondsRemaining -= 1
                if (breathPhaseSecondsRemaining <= 0) {
                    // Cycle phases
                    when (breathCyclePhase) {
                        "Inhale" -> {
                            breathCyclePhase = "Hold"
                            breathPhaseSecondsRemaining = 5
                        }
                        "Hold" -> {
                            breathCyclePhase = "Exhale"
                            breathPhaseSecondsRemaining = 5
                        }
                        "Exhale" -> {
                            breathCyclePhase = "Inhale"
                            breathPhaseSecondsRemaining = 5
                        }
                    }
                }
            }
            breathActive = false
        }
    }

    // Render based on morningSection
    when (morningSection) {
        "main" -> {
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
                    // Task completions progress card
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

                    // 1. Interactive Routine Channels Card (List View style)
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "🌤️ ACTIVE ROUTINE CHANNELS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF22D3EE),
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            // Portal Item A: Morning Cardio & Exercise Hub
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                    .clickable { morningSection = "exercise" }
                                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF87171).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = "Exercise",
                                                tint = Color(0xFFF87171),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Morning Exercise Hub 🏃",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Running timer & active fitness stats",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "→",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF87171)
                                    )
                                }
                            }

                            // Portal Item B: Work Focus Studio
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                    .clickable { morningSection = "work_studio" }
                                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Timer,
                                                contentDescription = "Work Presets",
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Work Focus Studio 💼",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Pomodoro intervals & work deliverables",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "→",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }

                            // Portal Item C: Guided Deep Breathing
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                    .clickable { morningSection = "breathing" }
                                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF22D3EE).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.WbSunny,
                                                contentDescription = "Breathing",
                                                tint = Color(0xFF22D3EE),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Guided Deep Breathing 🌬️",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Pranayama box breath (5M / 10M)",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "→",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF22D3EE)
                                    )
                                }
                            }

                            // Portal Item D: Healthy Breakfast Station
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                    .clickable { morningSection = "breakfast_station" }
                                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFFB923C).copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Egg,
                                                contentDescription = "Breakfast Station",
                                                tint = Color(0xFFFB923C),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Healthy Breakfast Station 🍳",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "High-protein recipes & meal prep guidelines",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "→",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFB923C)
                                    )
                                }
                            }
                        }
                    }

                    // Checklist section (Morning Checks) below
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

        "exercise" -> {
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
                            onClick = { morningSection = "main" },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "EXERCISE LIST",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF87171),
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Morning Exercise Hub",
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
                    item {
                        Text(
                            text = "PRIMARY MORNING WORKOUTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Exercise 1: Running & Cardio (Main Workout containing Customizable Timer + Stopwatch)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { morningSection = "running_workout" },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF87171).copy(alpha = 0.15f))
                                                .graphicsLayer {
                                                    translationY = runBounceY
                                                    rotationZ = runRotation
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🏃", fontSize = 18.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Running Tracker",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF87171).copy(alpha = 0.15f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Primary", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                                    }
                                }
                            }
                        }
                    }

                    // Exercise 2: Yoga Stretch
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { morningSection = "yoga_routine" },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF34D399).copy(alpha = 0.15f))
                                                .graphicsLayer {
                                                    scaleX = stretchScale
                                                    scaleY = stretchScale
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🧘", fontSize = 18.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Yoga & Morning Stretching",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF34D399).copy(alpha = 0.15f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Yoga", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                                    }
                                }
                            }
                        }
                    }

                    // Exercise 2.5: Walking Workout
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { morningSection = "walking_workout" },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
                                                .graphicsLayer {
                                                    translationX = walkSwayX
                                                    rotationZ = walkRotation
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("🚶", fontSize = 18.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Walking Workout",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 17.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("Cardio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                    }
                                }
                            }
                        }
                    }

                    // Exercise 3: Calisthenics Routine
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFDE047).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("💪", fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Calisthenics HIIT Core",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Active intervals for pushups, squats, and star jumps. Quick 4-minute routines to ramp up morning thermal activation levels.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        "running_workout" -> {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { morningSection = "exercise" },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "PRIMARY TRACKER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF87171),
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Running cardio Suite",
                                fontSize = 19.sp,
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
                    // LIVE SIMULATING STATISTICS PANEL
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚡ LIVE RUNNING METRICS",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFF87171),
                                        letterSpacing = 1.sp
                                    )
                                    if (runTimerRunning || stopwatchRunning) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.Red)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("ACTIVE", fontSize = 10.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "IDLE (Not tracking)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Three Column statistical row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // Distance (km)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("📍 km", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = String.format(Locale.US, "%.3f", runKm),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text("Distance", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                    }

                                    // Calories (kcal)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🔥 kcal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${runKcal.toInt()}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFF97316)
                                        )
                                        Text("Cal. Burned", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                    }

                                    // Steps count
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("👣 steps", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "$runSteps",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF38BDF8)
                                        )
                                        Text("Steps taken", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                    }
                                }

                                if (runKm > 0 || runSteps > 0) {
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            runKm = 0.00f
                                            runSteps = 0
                                            runKcal = 0f
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset Stats", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurface)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Reset Running Stats", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // MODULE A: INTERACTIVE CUSTOMIZABLE TIMER
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "🎯 CUSTOMIZABLE RUN TIMER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFDE047),
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Timer display: mm : ss
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!runTimerRunning) {
                                        // Minus Button to adjust duration
                                        IconButton(
                                            onClick = {
                                                if (runTimerDurationMinutes > 1) {
                                                    runTimerDurationMinutes -= 1
                                                    runTimerRemainingMs = runTimerDurationMinutes * 60 * 1000L
                                                }
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                        ) {
                                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }

                                        Spacer(modifier = Modifier.width(20.dp))
                                    }

                                    // Large Digit countdown or duration selector
                                    val minutes = runTimerRemainingMs / 60000
                                    val seconds = (runTimerRemainingMs % 60000) / 1000
                                    Text(
                                        text = String.format(Locale.US, "%02d:%02d", minutes, seconds),
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (!runTimerRunning) {
                                        Spacer(modifier = Modifier.width(20.dp))

                                        // Plus Button to adjust duration
                                        IconButton(
                                            onClick = {
                                                if (runTimerDurationMinutes < 60) {
                                                    runTimerDurationMinutes += 1
                                                    runTimerRemainingMs = runTimerDurationMinutes * 60 * 1000L
                                                }
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                        ) {
                                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }

                                if (!runTimerRunning) {
                                    Text(
                                        text = "Setup mode: Use -/+ to customize runtime minutes count.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Controls Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // Start / Pause
                                    Button(
                                        onClick = {
                                            runTimerRunning = !runTimerRunning
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (runTimerRunning) Color(0xFFEF4444) else Color(0xFF10B981)
                                        ),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (runTimerRunning) "Pause Run" else "Start Countdown",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Reset Button
                                    Button(
                                        onClick = {
                                            runTimerRunning = false
                                            runTimerRemainingMs = runTimerDurationMinutes * 60 * 1000L
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                        ),
                                        modifier = Modifier.weight(0.5f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Reset", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // MODULE B: LIVE RUN STOPWATCH
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "⏱️ LIVE RUN STOPWATCH",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF22D3EE),
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Stopwatch time representation (minutes, seconds, centiseconds)
                                val swMinutes = stopwatchMs / 60000
                                val swSeconds = (stopwatchMs % 60000) / 1000
                                val swCentiseconds = (stopwatchMs % 1000) / 10

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%02d:%02d.%02d", swMinutes, swSeconds, swCentiseconds),
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF22D3EE)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Stopwatch Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = {
                                            stopwatchRunning = !stopwatchRunning
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (stopwatchRunning) Color(0xFFF59E0B) else Color(0xFF06B6D4)
                                        ),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (stopwatchRunning) "Pause Stopwatch" else "Start Stopwatch",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Button(
                                        onClick = {
                                            stopwatchRunning = false
                                            stopwatchMs = 0L
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                        ),
                                        modifier = Modifier.weight(0.5f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Stop", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        "work_studio" -> {
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
                            onClick = { morningSection = "main" },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "WORK MANAGEMENT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Deep Work Focus Studio",
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
                    // WORK POMODORO BLOCK CARD
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "⏲️ DEEP WORK BLOCKS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF38BDF8),
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Slider / preset selector for Pomodoro time blocks
                                if (!workTimerRunning) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        listOf(15, 25, 50).forEach { mins ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        if (workSelectedMins == mins) Color(0xFF38BDF8).copy(alpha = 0.2f)
                                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                                                    )
                                                    .clickable {
                                                        workSelectedMins = mins
                                                        workTimerRemainingMs = mins * 60 * 1000L
                                                    }
                                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = "$mins Min",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = if (workSelectedMins == mins) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Show active countdown timer
                                val workMins = workTimerRemainingMs / 60000
                                val workSecs = (workTimerRemainingMs % 60000) / 1000
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = String.format(Locale.US, "%02d:%02d", workMins, workSecs),
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF38BDF8)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "FOCUS BLOCK SESSION COUNTDOWN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = {
                                            workTimerRunning = !workTimerRunning
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (workTimerRunning) Color(0xFFEF4444) else Color(0xFF0284C7)
                                        ),
                                        modifier = Modifier.weight(1.5f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = if (workTimerRunning) "Stop focus" else "Start Deep Work",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Button(
                                        onClick = {
                                            workTimerRunning = false
                                            workTimerRemainingMs = workSelectedMins * 60 * 1000L
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                        ),
                                        modifier = Modifier.weight(0.7f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Reset", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // MORNING WORK DELIVERABLES LIST
                    item {
                        Column {
                            Text(
                                text = "📝 MORNING WORK DELIVERABLES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Add Task Text field inline with button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = newWorkItemText,
                                        onValueChange = { newWorkItemText = it },
                                        placeholder = { Text("Add critical focus task...", fontSize = 13.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF38BDF8),
                                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (newWorkItemText.isNotBlank()) {
                                            workDeliverables = workDeliverables + Pair(newWorkItemText.trim(), false)
                                            newWorkItemText = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(54.dp)
                                ) {
                                    Text("Add", fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Deliverables List rows
                            if (workDeliverables.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                ) {
                                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                                        Text(text = "Deliverables complete! Add focus tasks above.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 13.sp)
                                    }
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    workDeliverables.forEachIndexed { idx, pair ->
                                        val (text, isChecked) = pair
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Checkbox tap
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (isChecked) Color(0xFF38BDF8)
                                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                                    )
                                                    .clickable {
                                                        workDeliverables = workDeliverables.toMutableList().also { list ->
                                                            list[idx] = Pair(text, !isChecked)
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isChecked) {
                                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Checked", tint = Color.Black, modifier = Modifier.size(16.dp))
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Text(
                                                text = text,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isChecked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )

                                            // Delete Button
                                            IconButton(
                                                onClick = {
                                                    workDeliverables = workDeliverables.filterIndexed { index, _ -> index != idx }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Item", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        "breathing" -> {
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
                            onClick = {
                                breathActive = false
                                morningSection = "main"
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "WELLNESS OPTION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF22D3EE),
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Guided Deep Breathing",
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (!breathActive) {
                        // BREATHING TECHNIQUE INFOCARD - SETUP SCREEN
                        Spacer(modifier = Modifier.height(10.dp))

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "🧘 PRANAYAMA DEEP BREATH TECHNIQUE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF22D3EE),
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "This structured exercise establishes heart rate regulation and mental clarity. It is composed of highly specific, even intervals:",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(
                                        "💨 INHALE (5 seconds) - Take in fresh morning oxygen.",
                                        "✋ HOLD (5 seconds) - Retain and normalize blood pressure.",
                                        "💨 EXHALE (5 seconds) - Slow diaphragm release of toxins."
                                    ).forEach { item ->
                                        Text(text = item, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }

                        // TIME CHANNELS (VO COUSTAMZABLE NEHI HE - 5M or 10M SELECT CHIPS)
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = "⏱️ SELECT STABLE SESSION DURATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFDE047),
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // 5 Min Chip
                                    Button(
                                        onClick = { selectBreathMins = 5 },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectBreathMins == 5) Color(0xFF22D3EE) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                                        ),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "5 Min Session",
                                            color = if (selectBreathMins == 5) Color.Black else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    // 10 Min Chip
                                    Button(
                                        onClick = { selectBreathMins = 10 },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectBreathMins == 10) Color(0xFF22D3EE) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                                        ),
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "10 Min Session",
                                            color = if (selectBreathMins == 10) Color.Black else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Note: Focus intervals are non-customizable for pristine respiratory training results.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // LARGE START GUIDED BUTTON
                        Button(
                            onClick = {
                                breathActive = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22D3EE)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                text = "Start Breath Guide",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    } else {
                        // PRECISE LIVE GUIDED SESSION RUNNING
                        Spacer(modifier = Modifier.height(20.dp))

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // TOTAL COUNTER TIMER OVERVIEW
                                val remMins = breathSessionRemainingSecs / 60
                                val remSecs = breathSessionRemainingSecs % 60
                                Text(
                                    text = String.format(Locale.US, "REMAINING SESSION TIME - %02d:%02d", remMins, remSecs),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF22D3EE),
                                    letterSpacing = 1.5.sp
                                )

                                Spacer(modifier = Modifier.height(30.dp))

                                // GLOWING DYNAMIC BREATH CIRCLE BUBBLE
                                val circleScale by animateFloatAsState(
                                    targetValue = when (breathCyclePhase) {
                                        "Inhale" -> 1.5f - (breathPhaseSecondsRemaining.toFloat() / 5f) * 0.7f
                                        "Hold" -> 1.5f
                                        "Exhale" -> 0.8f + (breathPhaseSecondsRemaining.toFloat() / 5f) * 0.7f
                                        else -> 1.0f
                                    },
                                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
                                    label = "breath_circle_scale"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .scale(circleScale)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    when (breathCyclePhase) {
                                                        "Inhale" -> Color(0xFF10B981).copy(alpha = 0.5f)
                                                        "Hold" -> Color(0xFFF59E0B).copy(alpha = 0.5f)
                                                        else -> Color(0xFF0EA5E9).copy(alpha = 0.5f)
                                                    },
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                        .border(
                                            3.dp,
                                            when (breathCyclePhase) {
                                                "Inhale" -> Color(0xFF10B981)
                                                "Hold" -> Color(0xFFF59E0B)
                                                else -> Color(0xFF0EA5E9)
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Internal Second Tick
                                    Text(
                                        text = "$breathPhaseSecondsRemaining",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(40.dp))

                                // PHASE LABEL SPECIFICS
                                Text(
                                    text = when (breathCyclePhase) {
                                        "Inhale" -> "💨 BREATHE IN"
                                        "Hold" -> "✋ HOLD STEADY"
                                        "Exhale" -> "💨 BREATHE OUT"
                                        else -> "GET READY..."
                                    },
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when (breathCyclePhase) {
                                        "Inhale" -> Color(0xFF10B981)
                                        "Hold" -> Color(0xFFF59E0B)
                                        else -> Color(0xFF0EA5E9)
                                    }
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = when (breathCyclePhase) {
                                        "Inhale" -> "Slowly expand diaphragm to pull in life-force"
                                        "Hold" -> "Normalize blood parameters, retain quiet stillness"
                                        else -> "Completely flush physical and mental fatigue"
                                    },
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(40.dp))

                                Button(
                                    onClick = {
                                        breathActive = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Abort Session", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        "walking_workout" -> {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { morningSection = "exercise" },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to exercises",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "WALKING WORKOUT TRACKER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Live Cardio Walkroom",
                                fontSize = 18.sp,
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
                    // Live metrics Row
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(
                                    text = "LIVE MOTION METRICS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("👣 Steps", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Text("$walkSteps", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🔥 Calories", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Text(String.format(Locale.US, "%.1f kcal", walkKcal), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("📍 Distance", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Text(String.format(Locale.US, "%.3f km", walkKm), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("True GPS dist", fontSize = 9.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // GPS Satellite coordinates marker block (Satisfies the requirement for actual satellite/google map coordinate display)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "GPS Signal",
                                        tint = if (isLocationPermissionGranted) Color(0xFF34D399) else Color(0xFFF87171),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = gpsStatusMessage,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                if (currentLatitude != null && currentLongitude != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "LAT: ${String.format(Locale.US, "%.6f", currentLatitude)}",
                                            fontSize = 11.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = "LNG: ${String.format(Locale.US, "%.6f", currentLongitude)}",
                                            fontSize = 11.sp,
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Walk around or hold phone in your hand to start detecting steps with live sensors & direct GPS coordinates.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    // Tool selection: Customizable countdown timer or raw stopwatch!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "⏱️ WALK TRACKING SESSION CONTROLS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                // Tab triggers inside Card
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { walkTimerRunning = !walkTimerRunning; walkStopwatchRunning = false },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (walkTimerRunning) Color(0xFF38BDF8) else Color.Transparent,
                                            contentColor = if (walkTimerRunning) Color.Black else MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (walkTimerRunning) "Pause Timer" else "Start Timer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { walkStopwatchRunning = !walkStopwatchRunning; walkTimerRunning = false },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (walkStopwatchRunning) Color(0xFF38BDF8) else Color.Transparent,
                                            contentColor = if (walkStopwatchRunning) Color.Black else MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(if (walkStopwatchRunning) "Pause Stopw." else "Start Stopw.", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Render Walk Timer UI
                                if (!walkStopwatchRunning) {
                                    val minutesLeft = (walkTimerRemainingMs / 60000).toInt()
                                    val secondsLeft = ((walkTimerRemainingMs % 60000) / 1000).toInt()
                                    Text(
                                        text = String.format(Locale.US, "%02d:%02d", minutesLeft, secondsLeft),
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        listOf(5, 10, 15, 30).forEach { mins ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (walkTimerDurationMinutes == mins) Color(0xFF38BDF8).copy(alpha = 0.15f) else Color.Transparent)
                                                    .clickable {
                                                        walkTimerDurationMinutes = mins
                                                        walkTimerRemainingMs = mins * 60000L
                                                        walkTimerRunning = false
                                                    }
                                                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text("${mins}M", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                } else {
                                    // Stopwatch UI
                                    val elapsedMins = (walkStopwatchMs / 60000).toInt()
                                    val elapsedSecs = ((walkStopwatchMs % 60000) / 1000).toInt()
                                    val elapsedHunds = ((walkStopwatchMs % 1000) / 10).toInt()
                                    Text(
                                        text = String.format(Locale.US, "%02d:%02d:%02d", elapsedMins, elapsedSecs, elapsedHunds),
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { walkStopwatchMs = 0L; walkStopwatchRunning = false },
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f), contentColor = Color(0xFFEF4444)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Reset Stopwatch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Reset fitness logging metrics
                    item {
                        Button(
                            onClick = {
                                walkSteps = 0
                                walkKcal = 0f
                                walkKm = 0.00f
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reset Walking Session Metrics", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        "yoga_routine" -> {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { morningSection = "exercise"; yogaTimerRunning = false },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to exercises",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "YOGA & CELLULAR STRETCHING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF34D399),
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Daily Mindful Flow",
                                fontSize = 18.sp,
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


                    // Display active exercise panel if running
                    if (yogaTimerRunning) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                                border = BorderStroke(2.dp, Color(0xFF34D399))
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "ACTIVE SESSION: ${yogaSelectedType.uppercase()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF34D399),
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Graphic pulse wave animation
                                    val infiniteTransition = rememberInfiniteTransition(label = "yoga_pulse")
                                    val pulseScale by infiniteTransition.animateFloat(
                                        initialValue = 0.92f,
                                        targetValue = 1.08f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1200, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "scale"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .scale(pulseScale)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399).copy(alpha = 0.1f))
                                            .border(4.dp, Color(0xFF34D399), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            val mins = (yogaTimerRemainingMs / 60000).toInt()
                                            val secs = ((yogaTimerRemainingMs % 60000) / 1000).toInt()
                                            Text(
                                                text = String.format(Locale.US, "%02d:%02d", mins, secs),
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = when(yogaSelectedType) {
                                            "Pranayan" -> "Sit cross-legged. Focus purely on deep inhale expansion, holding, and slow abdominal release."
                                            "Stretching" -> "Stretch the neck, chest, and lower spine safely. Mobilize deep limbs and lengthen core fibers."
                                            else -> "Perform soft jumping blocks in vertical rhythm. Stay elastic and bounce lightly on toes."
                                        },
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Button(
                                        onClick = { yogaTimerRunning = false },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("End Active Routine", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // The three selectable preset yoga activities
                    val yogaPres = listOf(
                        Triple("Pranayan", "Pranayama deep breath and lung expansion. Calms neurological networks instantly.", "🧘‍♀️"),
                        Triple("Stretching", "Gentle full-body mobility and deep, restorative muscle stretches.", "🙆‍♂️"),
                        Triple("Jumping", "Rhythmic vertical jumps to warm up muscle units and boost thermo logs.", "🤸")
                    )

                    items(yogaPres.size) { index ->
                        val (title, info, emoji) = yogaPres[index]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when (title) {
                                                        "Pranayan" -> Color(0xFF34D399).copy(alpha = 0.15f)
                                                        "Stretching" -> Color(0xFFFB923C).copy(alpha = 0.15f)
                                                        else -> Color(0xFFF87171).copy(alpha = 0.15f)
                                                    }
                                                )
                                                .graphicsLayer {
                                                    when (title) {
                                                        "Pranayan" -> {
                                                            scaleX = breathingScale
                                                            scaleY = breathingScale
                                                        }
                                                        "Stretching" -> {
                                                            scaleX = stretchScale
                                                            scaleY = stretchScale
                                                        }
                                                        "Jumping" -> {
                                                            translationY = jumpBounceY
                                                            scaleY = jumpSquishY
                                                        }
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(emoji, fontSize = 18.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = info,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.61f),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Standard 5M and 10M trigger buttons (Strict unmodifiable times)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            yogaSelectedType = title
                                            yogaSelectedMinutes = 5
                                            yogaTimerRemainingMs = 5 * 60 * 1000L
                                            yogaTimerRunning = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399).copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.onSurface),
                                        border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.3f)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("5 Min Workout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            yogaSelectedType = title
                                            yogaSelectedMinutes = 10
                                            yogaTimerRemainingMs = 10 * 60 * 1000L
                                            yogaTimerRunning = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34D399).copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.onSurface),
                                        border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.3f)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("10 Min Workout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        "breakfast_station" -> {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { morningSection = "main"; breakfastMealPrepTimerActive = false },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "NUTRITIOUS BREAKFAST STATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFB923C),
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Morning Fuel Station",
                                fontSize = 18.sp,
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
                    // Prep guidelines intro card
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Why Protein breakfast?",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Eating nutritious clean proteins increases morning neurotransmitter focus, regulates daily glucose levels and cuts unhealthy snacking cravings.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    // Curated recipes with tick/untick capability
                    item {
                        Text(
                            text = "NUTRIMENT MEAL OPTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            letterSpacing = 1.sp
                        )
                    }

                    val recipes = listOf(
                        Triple("Avocado Omelet Toast Ensembles", "2 whole eggs cooked with fresh chopped baby spinach, served on warm toasted multigrain bread topped with mashed ripe avocado. Rich in healthy proteins and dietary fibers.\n🔥 Curated Macros: 28g Protein, 12g Fiber, 420 Kcal.", "🥑"),
                        Triple("Almond-Banana Greek Bowl", "Plain non-fat Greek yogurt base overlaid with natural almond halves, half a sliced banana, chia seeds, and a golden drizzle of raw honey.\n🔥 Curated Macros: 32g Protein, 6g Fiber, 340 Kcal.", "🥣"),
                        Triple("Superseed Oatmeal Concoction", "Quick porridge cooked with protein soy/oat milk, blended with milled flaxseeds, raw hemp seeds, walnuts, and seasonal raspberries.\n🔥 Curated Macros: 22g Protein, 11g Fiber, 380 Kcal.", "🌾")
                    )

                    items(recipes.size) { rIndex ->
                        val (title, body, emoji) = recipes[rIndex]
                        val isChecked = completedBreakfastRecipes.contains(title)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    completedBreakfastRecipes = if (isChecked) {
                                        completedBreakfastRecipes - title
                                    } else {
                                        completedBreakfastRecipes + title
                                    }
                                    
                                    // Synchronize completion status with the main task routine
                                    val hasMatchingBreakfastRoutine = tasks.firstOrNull { it.category == "breakfast" }
                                    if (hasMatchingBreakfastRoutine != null) {
                                        val breakfastCurrentlyShouldBeCompleted = completedBreakfastRecipes.isNotEmpty()
                                        if (hasMatchingBreakfastRoutine.isCompleted != breakfastCurrentlyShouldBeCompleted) {
                                            onToggleComplete(hasMatchingBreakfastRoutine)
                                        }
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)),
                            border = BorderStroke(1.dp, if (isChecked) Color(0xFF22C55E).copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Text(emoji, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    
                                    // Elegant green checkbox or circle indicator
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isChecked) Color(0xFF22C55E).copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isChecked) Color(0xFF22C55E)
                                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isChecked) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color(0xFF22C55E),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = body,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
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
    val context = LocalContext.current
    var selectedSport by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val allSports = listOf(
        "Running", "Walking", "Cycling", "Badminton", "Cricket", "Football", "Basketball", "Volleyball", "Swimming", "Tennis",
        "Table Tennis", "Gym Workout", "Yoga", "Skipping Rope", "Hiking", "Jogging", "Dance", "Zumba", "Boxing", "Kickboxing",
        "Martial Arts", "Karate", "Taekwondo", "Wrestling", "Kabaddi", "Baseball", "Softball", "Rugby", "Golf", "Archery",
        "Skating", "Roller Skating", "Ice Skating", "Horse Riding", "Surfing", "Skiing", "Snowboarding", "Climbing", "Rock Climbing", "Rowing",
        "Canoeing", "Kayaking", "Sprinting", "Long Jump", "High Jump", "Shot Put", "Discus Throw", "Javelin Throw", "Pole Vault", "Triathlon",
        "Bodybuilding", "CrossFit", "Pilates", "Stretching", "Meditation", "Aerobics", "Parkour", "Calisthenics", "Push-Up Training", "Pull-Up Training",
        "Plank Training", "Strength Training", "Cardio Workout", "HIIT Workout", "Functional Training", "Stair Climbing", "Treadmill Running", "Indoor Cycling", "Elliptical Training", "Battle Rope",
        "Bench Press", "Deadlift", "Squats", "Lunges", "Mountain Climber Exercise", "Burpees", "Jumping Jacks", "Handball", "Netball", "Futsal",
        "Chess", "Billiards", "Snooker", "Bowling", "Fishing", "Frisbee", "Ultimate Frisbee", "Shooting", "Air Rifle", "Darts",
        "eSports", "Adventure Racing", "Obstacle Course", "Trail Running", "Nature Walk", "Scooter Riding", "BMX", "Motocross", "Scuba Diving", "Free Diving",
        "Paragliding", "Bungee Jumping", "Skydiving", "Sailing", "Badminton Doubles", "Beach Volleyball", "Water Polo", "Sepak Takraw", "Kho Kho", "Gilli Danda"
    )

    val filteredSports = allSports.filter { it.contains(searchQuery, ignoreCase = true) }

    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (isDark) listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                    else listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                )
            )
            .statusBarsPadding()
    ) {
        if (selectedSport == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 12.dp)
                    .navigationBarsPadding()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = contentColor)
                    }
                    Text("SPORTS & FITNESS", color = contentColor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 100+ Sports...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = contentColor,
                        focusedTextColor = contentColor,
                        focusedBorderColor = Color(0xFFA78BFA),
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f),
                        unfocusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White,
                        focusedContainerColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredSports) { sport ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { selectedSport = sport },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFFA78BFA))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(sport, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAddTask,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA78BFA))
                ) {
                    Text("ADD CUSTOM BLOCK", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            SportsTrackingDashboard(sport = selectedSport!!, onBack = { selectedSport = null })
        }
    }
}

@Composable
fun SportsTrackingDashboard(sport: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var isRunning by remember { mutableStateOf(false) }
    var timeInMs by remember { mutableLongStateOf(0L) }
    var steps by remember { mutableStateOf(0) }
    var distanceKm by remember { mutableStateOf(0.0f) }
    var calories by remember { mutableStateOf(0) }
    var speed by remember { mutableStateOf(0.0f) }

    // Types of sports logic
    val isMovementSport = listOf("Running", "Walking", "Cycling", "Hiking", "Jogging", "Trail Running").contains(sport)
    val isMatchSport = listOf("Football", "Cricket", "Badminton").contains(sport)

    LaunchedEffect(isRunning) {
        if (isRunning) {
            var lastTime = System.currentTimeMillis()
            while (isRunning) {
                delay(100)
                val now = System.currentTimeMillis()
                timeInMs += (now - lastTime)
                lastTime = now
            }
        }
    }

    // GPS & Motion Sensors
    DisposableEffect(isRunning) {
        if (isRunning) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            
            val accel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val accelListener = object : SensorEventListener {
                var lastPeak = 0L
                override fun onSensorChanged(e: SensorEvent?) {
                    if (e == null) return
                    val mag = Math.sqrt((e.values[0]*e.values[0] + e.values[1]*e.values[1] + e.values[2]*e.values[2]).toDouble())
                    if (mag > 12.5 && System.currentTimeMillis() - lastPeak > 400) {
                        steps++
                        calories += if (isMovementSport) 5 else 2
                        lastPeak = System.currentTimeMillis()
                    }
                }
                override fun onAccuracyChanged(s: Sensor?, a: Int) {}
            }
            
            val locListener = object : LocationListener {
                var lastLoc: Location? = null
                override fun onLocationChanged(loc: Location) {
                    if (loc.accuracy < 20f) {
                        val prev = lastLoc
                        if (prev != null) {
                            val d = prev.distanceTo(loc)
                            // Strict movement filter
                            if (d > 3.0 && loc.speed > 0.5f) {
                                distanceKm += (d / 1000f)
                                speed = loc.speed * 3.6f // km/h
                            }
                        }
                        lastLoc = loc
                    }
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            sensorManager?.registerListener(accelListener, accel, SensorManager.SENSOR_DELAY_UI)
            try {
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, locListener)
            } catch (e: SecurityException) {}

            onDispose {
                sensorManager?.unregisterListener(accelListener)
                locationManager?.removeUpdates(locListener)
            }
        } else {
            onDispose {}
        }
    }

    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF0F172A)
    val displayTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", 
        (timeInMs / 3600000), (timeInMs / 60000) % 60, (timeInMs / 1000) % 60)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 12.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = contentColor)
            }
            Text(sport.uppercase(), color = contentColor, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Futuristic Timer Circle
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(260.dp)) {
            CircularProgressIndicator(
                progress = { (timeInMs % 60000).toFloat() / 60000f },
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF60A5FA),
                strokeWidth = 4.dp,
                trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
            )
            CircularProgressIndicator(
                progress = { (timeInMs % 3600000).toFloat() / 3600000f },
                modifier = Modifier.size(240.dp),
                color = Color(0xFFA78BFA),
                strokeWidth = 8.dp,
                trackColor = Color.Transparent
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(displayTime, color = contentColor, fontSize = 42.sp, fontWeight = FontWeight.Light, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                Text("LIVE DURATION", color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Stats Grid
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("Calories", "$calories kcal", Icons.Default.LocalFireDepartment, Color(0xFFF87171), modifier = Modifier.weight(1f))
                    if (isMovementSport || isMatchSport) {
                        StatCard("Distance", String.format("%.2f km", distanceKm), Icons.Default.LocationOn, Color(0xFF60A5FA), modifier = Modifier.weight(1f))
                    } else {
                        StatCard("Score", "${(timeInMs / 10000)} pts", Icons.Default.AutoAwesome, Color(0xFFFBBF24), modifier = Modifier.weight(1f))
                    }
                }
            }
            if (isMovementSport) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Steps", "$steps", Icons.Default.DirectionsWalk, Color(0xFFC084FC), modifier = Modifier.weight(1f))
                        StatCard("Speed", String.format("%.1f km/h", speed), Icons.Default.PlayArrow, Color(0xFF34D399), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color(0xFFEF4444) else Color(0xFF60A5FA)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (isRunning) "STOP SESSION" else "START TRACKING", fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    val isDark = MaterialTheme.colorScheme.onBackground == Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF0F172A)
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White
        ),
        border = BorderStroke(1.dp, if (isDark) color.copy(alpha = 0.2f) else color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, color = contentColor, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

