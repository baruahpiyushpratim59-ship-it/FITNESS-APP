package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.RoutineItem
import com.example.ui.components.AddCustomTaskDialog
import com.example.ui.components.MusicSettingsDialog
import com.example.ui.components.FloatingMusicPlayerWidget
import com.example.ui.components.GlassCard
import com.example.ui.components.QuoteSection
import com.example.ui.components.RoutineCategoryCard
import com.example.ui.components.WaterIntakeCard
import com.example.ui.components.WaterIntakeScreen
import com.example.ui.components.SquareCategoryButton
import com.example.ui.components.CategoryTasksDialog
import com.example.ui.components.ClockScreen
import com.example.ui.components.MorningRoutineScreen
import com.example.ui.components.ExerciseScreen
import com.example.ui.components.StudyScreen
import com.example.ui.components.SleepScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.RoutineViewModel
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: RoutineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.checkAndResetToToday()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkTheme) {
                RoutineDashboardScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RoutineDashboardScreen(
    viewModel: RoutineViewModel,
    modifier: Modifier = Modifier
) {
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val quote by viewModel.motivationalQuote.collectAsStateWithLifecycle()
    val isAILoading by viewModel.isAILoading.collectAsStateWithLifecycle()
    val aiSuggestion by viewModel.aiSuggestion.collectAsStateWithLifecycle()
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showInsightsDialog by remember { mutableStateOf(false) }
    var showMusicDialog by remember { mutableStateOf(false) }
    var selectedCategoryForAI by remember { mutableStateOf<String?>(null) }
    var activeCategoryDialog by remember { mutableStateOf<String?>(null) }
    var activeScreen by remember { mutableStateOf("dashboard") }

    BackHandler(enabled = activeScreen != "dashboard") {
        activeScreen = "dashboard"
    }

    val currentSelectedDate by viewModel.currentDate.collectAsStateWithLifecycle()
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val yesterdayStr = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    // Format display date dynamically based on selection
    val displayDate = remember(currentSelectedDate) {
        try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(currentSelectedDate)
            if (date != null) {
                SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(date)
            } else {
                SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())
            }
        } catch (e: Exception) {
            SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())
        }
    }

    // Progress calculations (excluding water from count to prevent bias)
    val taskRoutines = routines.filter { it.category != "water" }
    val completedCount = taskRoutines.count { it.isCompleted }
    val totalCount = taskRoutines.size
    val progressFraction = if (totalCount == 0) 1f else completedCount.toFloat() / totalCount

    // Animated background gradient colors for incredibly smooth theme transition
    val gradStart by animateColorAsState(
        targetValue = if (isDark) Color(0xFF040209) else Color(0xFFF0F9FF), // Sky 50 (Very light blue)
        animationSpec = tween(durationMillis = 600),
        label = "gradStart"
    )
    val gradMid by animateColorAsState(
        targetValue = if (isDark) Color(0xFF0D0922) else Color(0xFFFFFFFF), // White
        animationSpec = tween(durationMillis = 600),
        label = "gradMid"
    )
    val gradEnd by animateColorAsState(
        targetValue = if (isDark) Color(0xFF111438) else Color(0xFFE0F2FE), // Sky 100 (Beautiful soft blue)
        animationSpec = tween(durationMillis = 600),
        label = "gradEnd"
    )

    val blobPurple by animateColorAsState(
        targetValue = if (isDark) Color(0x188B5CF6) else Color(0x1538BDF8), // Light blue tint
        animationSpec = tween(durationMillis = 600),
        label = "blobPurple"
    )
    val blobCyan by animateColorAsState(
        targetValue = if (isDark) Color(0x1100E5FF) else Color(0x100284C7), // Light blue tint
        animationSpec = tween(durationMillis = 600),
        label = "blobCyan"
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(260.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MENU", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                    
                    NavigationDrawerItem(
                        label = { Text("Insights") },
                        selected = false,
                        icon = { Icon(Icons.Default.Insights, contentDescription = null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            showInsightsDialog = true
                        }
                    )
                    
                    NavigationDrawerItem(
                        label = { Text(if (isDark) "Light Mode" else "Dark Mode") },
                        selected = false,
                        icon = { Icon(if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = null) },
                        onClick = { viewModel.toggleTheme() }
                    )
                    
                    NavigationDrawerItem(
                        label = { Text("Music Hub") },
                        selected = false,
                        icon = { Icon(Icons.Default.MusicNote, contentDescription = null) },
                        onClick = {
                            scope.launch { drawerState.close() }
                            showMusicDialog = true
                        }
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(gradStart, gradMid, gradEnd)
                    )
                )
                .testTag("dashboard_root")
        ) {
            // Ambient soft-glowing design blobs
            Box(
                modifier = Modifier
                    .offset(x = (-90).dp, y = (-100).dp)
                    .size(350.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(blobPurple, Color.Transparent)))
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 120.dp, y = 140.dp)
                    .size(420.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(blobCyan, Color.Transparent)))
            )

            AnimatedContent(
                targetState = activeScreen,
                transitionSpec = {
                    if (targetState != "dashboard") {
                        (scaleIn(animationSpec = tween(350), initialScale = 0.8f) + fadeIn(animationSpec = tween(350)))
                            .togetherWith(fadeOut(animationSpec = tween(350)))
                    } else {
                        (fadeIn(animationSpec = tween(350)))
                            .togetherWith(scaleOut(animationSpec = tween(350), targetScale = 0.8f) + fadeOut(animationSpec = tween(350)))
                    }
                },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    "wakeup" -> {
                        ClockScreen(onBack = { activeScreen = "dashboard" })
                    }
                    "study" -> {
                        StudyScreen(onBack = { activeScreen = "dashboard" })
                    }
                    "sleep" -> {
                        SleepScreen(onBack = { activeScreen = "dashboard" })
                    }
                    "morning" -> {
                        val morningTasks = routines.filter { it.category == "morning" }
                        MorningRoutineScreen(
                            tasks = morningTasks,
                            isAILoading = isAILoading && selectedCategoryForAI == "morning",
                            onToggleComplete = { viewModel.toggleComplete(it) },
                            onDelete = { viewModel.deleteRoutine(it) },
                            onAskAI = {
                                selectedCategoryForAI = "morning"
                                viewModel.fetchAISuggestion("morning")
                            },
                            onAddTask = { showAddTaskDialog = true },
                            onBack = { activeScreen = "dashboard" }
                        )
                    }
                    "exercise" -> {
                        val exerciseTasks = routines.filter { it.category == "exercise" }
                        ExerciseScreen(
                            tasks = exerciseTasks,
                            isAILoading = isAILoading && selectedCategoryForAI == "exercise",
                            onToggleComplete = { viewModel.toggleComplete(it) },
                            onDelete = { viewModel.deleteRoutine(it) },
                            onAskAI = {
                                selectedCategoryForAI = "exercise"
                                viewModel.fetchAISuggestion("exercise")
                            },
                            onAddTask = { showAddTaskDialog = true },
                            onBack = { activeScreen = "dashboard" }
                        )
                    }
                    "water" -> {
                        val waterItem = routines.find { it.category == "water" }
                        WaterIntakeScreen(
                            waterItem = waterItem,
                            onIncrement = { waterItem?.let { viewModel.incrementWater(it) } },
                            onDecrement = { waterItem?.let { viewModel.decrementWater(it) } },
                            onUpdateTarget = { newTarget -> waterItem?.let { viewModel.updateWaterTarget(it, newTarget) } },
                            onBack = { activeScreen = "dashboard" }
                        )
                    }
                    else -> {
                Scaffold(
                    containerColor = Color.Transparent,
                    // Fab removed as requested
                ) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Main Header Block
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                        .size(48.dp) // Ensure 48dp touch target
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open Menu",
                                        tint = MaterialTheme.colorScheme.primary // Use primary color for visibility
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "AURA ROUTINE AI",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 2.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Your Day at Glance",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = displayDate,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val isTodaySelected = currentSelectedDate == todayStr
                                val isYesterdaySelected = currentSelectedDate == yesterdayStr
                                
                                // Yesterday Button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = if (isYesterdaySelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.setDate(yesterdayStr) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Yesterday",
                                            color = if (isYesterdaySelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isYesterdaySelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                // Today Button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            color = if (isTodaySelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.setDate(todayStr) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Today",
                                            color = if (isTodaySelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isTodaySelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Completion Progress Card
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "DAILY MOMENTUM",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFA78BFA),
                                                letterSpacing = 1.2.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (totalCount == 0) "Zero tasks planned" else "$completedCount of $totalCount completed",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Text(
                                            text = "${(progressFraction * 100).toInt()}%",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                    )
                                }
                            }
                        }

                        // AI Quotes Subsection
                        item {
                            QuoteSection(
                                quote = quote,
                                isAILoading = isAILoading,
                                onRefreshQuote = { viewModel.refreshQuote() }
                            )
                        }

                        // Core Daily Actions (Square Buttons Grid)
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = "CORE DAILY ACTIONS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Row 1: Snooze & Morning Routine
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val snoozeTasks = routines.filter { it.category == "wakeup" }
                                    SquareCategoryButton(
                                        title = "Snooze",
                                        icon = Icons.Default.Alarm,
                                        accentColor = Color(0xFF38BDF8),
                                        completedCount = snoozeTasks.count { it.isCompleted },
                                        totalCount = snoozeTasks.size,
                                        onClick = { activeScreen = "wakeup" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    val morningTasks = routines.filter { it.category == "morning" }
                                    SquareCategoryButton(
                                        title = "Morning Routine",
                                        icon = Icons.Default.WbSunny,
                                        accentColor = Color(0xFFFDE047),
                                        completedCount = morningTasks.count { it.isCompleted },
                                        totalCount = morningTasks.size,
                                        onClick = { activeScreen = "morning" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Row 2: Study & Sleep
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    SquareCategoryButton(
                                        title = "Study",
                                        icon = Icons.Default.School,
                                        accentColor = Color(0xFF22C55E),
                                        completedCount = 0,
                                        totalCount = 0,
                                        onClick = { activeScreen = "study" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    SquareCategoryButton(
                                        title = "Sleep",
                                        icon = Icons.Default.Bedtime,
                                        accentColor = Color(0xFF818CF8),
                                        completedCount = 0,
                                        totalCount = 0,
                                        onClick = { activeScreen = "sleep" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                
                                // Row 3: Exercise & Water Intake
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    val exerciseTasks = routines.filter { it.category == "exercise" }
                                    SquareCategoryButton(
                                        title = "Exercise",
                                        icon = Icons.Default.FitnessCenter,
                                        accentColor = Color(0xFFF87171),
                                        completedCount = exerciseTasks.count { it.isCompleted },
                                        totalCount = exerciseTasks.size,
                                        onClick = { activeScreen = "exercise" },
                                        modifier = Modifier.weight(1f)
                                    )
                                    val waterItem = routines.find { it.category == "water" }
                                    SquareCategoryButton(
                                        title = "Water Intake",
                                        icon = Icons.Default.LocalDrink,
                                        accentColor = Color(0xFF0EA5E9),
                                        completedCount = waterItem?.completedCount ?: 0,
                                        totalCount = waterItem?.targetCount ?: 8,
                                        onClick = { activeScreen = "water" },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Routine Category Panels
                        val categories = listOf(
                            Triple("morning", "Morning Routine", Icons.Default.WbSunny),
                            Triple("study", "Study", Icons.Default.School),
                            Triple("sleep", "Sleep", Icons.Default.Bedtime),
                            Triple("exercise", "Exercise", Icons.Default.FitnessCenter),
                            Triple("water", "Water Intake", Icons.Default.LocalDrink)
                        )

                        items(categories.size) { index ->
                            val (key, title, icon) = categories[index]
                            val catTasks = routines.filter { it.category == key }

                            // Define distinctive primary colors for each card outline
                            val accentColor = when (key) {
                                "breakfast" -> Color(0xFFFB923C)// Orange
                                "study" -> Color(0xFF22C55E)    // Green
                                "sleep" -> Color(0xFF818CF8)    // Indigo
                                else -> Color(0xFFC084FC)       // Purple
                            }

                            RoutineCategoryCard(
                                title = title,
                                category = key,
                                icon = icon,
                                accentColor = accentColor,
                                tasks = catTasks,
                                isAILoading = isAILoading && selectedCategoryForAI == key,
                                onToggleComplete = { viewModel.toggleComplete(it) },
                                onDelete = { viewModel.deleteRoutine(it) },
                                onAskAI = { cat ->
                                    selectedCategoryForAI = cat
                                    viewModel.fetchAISuggestion(cat)
                                }
                            )
                        }

                        // Small decorative bottom footer
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Crafted for a healthy & mindful day.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
                }
            }
            if (activeScreen == "dashboard") {
                FloatingMusicPlayerWidget(
                    viewModel = viewModel,
                    onOpenHub = { showMusicDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
    
    // Dialogs
    if (showMusicDialog) {
        MusicSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showMusicDialog = false }
        )
    }

    if (showAddTaskDialog) {
        AddCustomTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onSave = { title, category, scheduledTime, notes ->
                viewModel.addCustomRoutine(title, category, scheduledTime, notes)
            }
        )
    }

    if (showInsightsDialog) {
        com.example.ui.components.InsightsDialog(
            onDismiss = { showInsightsDialog = false },
            getRoutinesForDateFlow = remember { { date -> viewModel.getRoutinesForDateFlow(date) } },
            allRoutinesFlow = viewModel.allRoutines
        )
    }

    aiSuggestion?.let { suggestionString: String ->
        AISuggestionsDialog(
            suggestionText = suggestionString,
            category = selectedCategoryForAI ?: "custom",
            onDismiss = {
                viewModel.clearAISuggestion()
                selectedCategoryForAI = null
            },
            onApply = { desc: String ->
                val matchingTask = routines.firstOrNull { it.category == selectedCategoryForAI }
                if (matchingTask != null) {
                    viewModel.toggleComplete(matchingTask)
                    viewModel.addCustomRoutine(
                        title = "AI Recommended " + (selectedCategoryForAI?.replaceFirstChar { it.uppercase() } ?: "Goal"),
                        category = selectedCategoryForAI ?: "custom",
                        scheduledTime = matchingTask.scheduledTime,
                        notes = desc
                    )
                } else {
                    viewModel.addCustomRoutine(
                        title = "AI Idea",
                        category = selectedCategoryForAI ?: "custom",
                        scheduledTime = "Anytime",
                        notes = desc
                    )
                }
                viewModel.clearAISuggestion()
                selectedCategoryForAI = null
            }
        )
    }

    activeCategoryDialog?.let { currentCat ->
        val dialogTitle = when (currentCat) {
            "wakeup" -> "Snooze"
            "morning" -> "Morning Routine"
            "exercise" -> "Exercise"
            else -> currentCat.replaceFirstChar { it.uppercase() }
        }
        val accentColor = when (currentCat) {
            "wakeup" -> Color(0xFF38BDF8)
            "morning" -> Color(0xFFFDE047)
            "exercise" -> Color(0xFFF87171)
            else -> Color(0xFFC084FC)
        }
        val catTasks = routines.filter { it.category == currentCat }

        CategoryTasksDialog(
            category = currentCat,
            title = dialogTitle,
            accentColor = accentColor,
            tasks = catTasks,
            isAILoading = isAILoading && selectedCategoryForAI == currentCat,
            onToggleComplete = { viewModel.toggleComplete(it) },
            onDelete = { viewModel.deleteRoutine(it) },
            onAskAI = { cat ->
                selectedCategoryForAI = cat
                viewModel.fetchAISuggestion(cat)
            },
            onAddTask = { showAddTaskDialog = true },
            onDismiss = { activeCategoryDialog = null }
        )
    }
}


