package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.components.GlassCard
import com.example.ui.components.QuoteSection
import com.example.ui.components.RoutineCategoryCard
import com.example.ui.components.WaterIntakeCard
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.RoutineViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: RoutineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
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

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedCategoryForAI by remember { mutableStateOf<String?>(null) }

    // Format display date
    val displayDate = SimpleDateFormat("EEEE, d MMMM YYYY", Locale.getDefault()).format(Date())

    // Progress calculations (excluding water from count to prevent bias)
    val taskRoutines = routines.filter { it.category != "water" }
    val completedCount = taskRoutines.count { it.isCompleted }
    val totalCount = taskRoutines.size
    val progressFraction = if (totalCount == 0) 1f else completedCount.toFloat() / totalCount

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF040209),
                        Color(0xFF0D0922),
                        Color(0xFF111438)
                    )
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
                .background(Brush.radialGradient(listOf(Color(0x188B5CF6), Color.Transparent)))
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 120.dp, y = 140.dp)
                .size(420.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0x1100E5FF), Color.Transparent)))
        )

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddTaskDialog = true },
                    containerColor = Color(0xFF8B5CF6),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .testTag("add_custom_task_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom daily routine item",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
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
                    Text(
                        text = "AURA ROUTINE AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 2.sp,
                        modifier = Modifier.statusBarsPadding()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your Day at Glance",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = displayDate,
                        fontSize = 14.sp,
                        color = Color(0x99FFFFFF)
                    )
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
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "${(progressFraction * 100).toInt()}%",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF8B5CF6)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { progressFraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF8B5CF6),
                                trackColor = Color(0x1AFFFFFF)
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

                // Hydration Panel
                item {
                    val waterItem = routines.find { it.category == "water" }
                    WaterIntakeCard(
                        waterItem = waterItem,
                        onIncrement = { waterItem?.let { viewModel.incrementWater(it) } },
                        onDecrement = { waterItem?.let { viewModel.decrementWater(it) } }
                    )
                }

                // Routine Category Panels
                val categories = listOf(
                    Triple("wakeup", "Snooze & Wake Reminders", Icons.Default.Alarm),
                    Triple("morning", "Morning Routines", Icons.Default.WbSunny),
                    Triple("exercise", "5-Min Quick Exercises", Icons.Default.FitnessCenter),
                    Triple("breakfast", "Healthy Breakfast Suggestions", Icons.Default.Egg),
                    Triple("study", "Study Routine Planner", Icons.Default.School),
                    Triple("sleep", "Evening Wind-downs", Icons.Default.Bedtime),
                    Triple("custom", "Additional Habits", Icons.Default.Star)
                )

                items(categories.size) { index ->
                    val (key, title, icon) = categories[index]
                    val catTasks = routines.filter { it.category == key }

                    // Define distinctive primary colors for each card outline
                    val accentColor = when (key) {
                        "wakeup" -> Color(0xFF38BDF8)   // Sky
                        "morning" -> Color(0xFFFDE047)  // Yellow
                        "exercise" -> Color(0xFFF87171) // Coral
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
                        color = Color(0x66FFFFFF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Add Custom Block Dialog Overlay
        if (showAddTaskDialog) {
            AddCustomTaskDialog(
                onDismiss = { showAddTaskDialog = false },
                onSave = { title, category, scheduledTime, notes ->
                    viewModel.addCustomRoutine(title, category, scheduledTime, notes)
                }
            )
        }

        // Suggestions Sheet Dialog
        aiSuggestion?.let { suggestionString ->
            AISuggestionsDialog(
                suggestionText = suggestionString,
                category = selectedCategoryForAI ?: "custom",
                onDismiss = {
                    viewModel.clearAISuggestion()
                    selectedCategoryForAI = null
                },
                onApply = { desc ->
                    // Apply suggestion to the first empty notes task of that category if available
                    val matchingTask = routines.firstOrNull { it.category == selectedCategoryForAI }
                    if (matchingTask != null) {
                        viewModel.toggleComplete(matchingTask) // Toggles complete for satisfaction
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
    }
}

@Composable
fun AISuggestionsDialog(
    suggestionText: String,
    category: String,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0C1B)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Action",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Personalizer",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close description",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Suggested recommendation:",
                    fontSize = 12.sp,
                    color = Color(0xFFA78BFA),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x0AFFFFFF))
                        .padding(12.dp)
                ) {
                    Text(
                        text = suggestionText,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    ) {
                        Text("Dismiss", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = { onApply(suggestionText) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                    ) {
                        Text("Add to Tracker", color = Color.White)
                    }
                }
            }
        }
    }
}
