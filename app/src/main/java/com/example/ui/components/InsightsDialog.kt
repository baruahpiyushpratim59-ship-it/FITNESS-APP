package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.RoutineItem
import java.time.LocalDate
import java.time.YearMonth
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import java.time.format.DateTimeFormatter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import kotlin.math.roundToInt
import java.util.Calendar
import java.util.Locale

// Helper to compute monthly average percentages (Jan..Dec) from actual routine items in Room.
// Returns 0% if no data is recorded for the month to prevent fake baseline values.
fun getMonthlyPercentages(allRoutines: List<RoutineItem>): List<Float> {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    return (1..12).map { month ->
        val prefix = String.format(Locale.getDefault(), "%04d-%02d", currentYear, month)
        val monthRoutines = allRoutines.filter {
            it.dateString.startsWith(prefix) && it.category != "water"
        }
        if (monthRoutines.isNotEmpty()) {
            val completed = monthRoutines.count { it.isCompleted }
            (completed.toFloat() / monthRoutines.size) * 100f
        } else {
            0f
        }
    }
}

// Helper to compute weekly average percentages (Mon..Sun of current week) from actual routine items.
fun getWeeklyPercentages(allRoutines: List<RoutineItem>): Pair<List<String>, List<Float>> {
    val today = LocalDate.now()
    val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val values = (0..6).map { i ->
        val dateStr = monday.plusDays(i.toLong()).toString()
        val dayRoutines = allRoutines.filter { it.dateString == dateStr && it.category != "water" }
        if (dayRoutines.isNotEmpty()) {
            val completed = dayRoutines.count { it.isCompleted }
            (completed.toFloat() / dayRoutines.size) * 100f
        } else {
            0f
        }
    }
    return Pair(labels, values)
}

@Composable
fun InsightsDialog(
    onDismiss: () -> Unit,
    getRoutinesForDateFlow: (String) -> Flow<List<RoutineItem>>,
    allRoutinesFlow: StateFlow<List<RoutineItem>>
) {
    var selectedTab by remember { mutableStateOf(0) }
    var visualMode by remember { mutableStateOf("graph") } // default: "graph" (the sleek black image curve)
    
    val allRoutinesList by allRoutinesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Dialog(
        onDismissRequest = onDismiss, 
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Insights & Performance", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Weekly", fontWeight = FontWeight.Bold) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Monthly", fontWeight = FontWeight.Bold) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Calendar", fontWeight = FontWeight.Bold) })
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Visual option toggle
                Text(
                    text = "VISUALIZATION ENGINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isBar = visualMode == "bar"
                    val isGraph = visualMode == "graph"
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isGraph) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { visualMode = "graph" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "📈 Glowing Curve",
                            color = if (isGraph) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isBar) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { visualMode = "bar" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "📊 Teal Bar Diagram",
                            color = if (isBar) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                when (selectedTab) {
                    0 -> WeeklyInsightView(visualMode, allRoutinesList)
                    1 -> MonthlyInsightView(visualMode, allRoutinesList)
                    2 -> CalendarInsightView(getRoutinesForDateFlow, visualMode, allRoutinesList)
                }
            }
        }
    }
}

@Composable
fun TealBarDiagram(
    values: List<Float>,
    labels: List<String>,
    highlightedIndex: Int,
    xAxisTitle: String = "← Timeline →",
    modifier: Modifier = Modifier,
    onIndexSelected: ((Int) -> Unit)? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    
    val labelStyle = TextStyle(
        color = onSurfaceColor.copy(alpha = 0.7f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
    )
    val axisTitleStyle = TextStyle(
        color = onSurfaceColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(surfaceColor)
            .padding(4.dp)
            .pointerInput(values, highlightedIndex) {
                detectTapGestures { offset ->
                    val w = size.width.toFloat()
                    val leftPad = with(density) { 54.dp.toPx() }
                    val rightPad = with(density) { 16.dp.toPx() }
                    val chartWidth = w - leftPad - rightPad
                    val barCount = values.size
                    if (barCount > 0) {
                        val barSpacingWidth = chartWidth / barCount
                        if (offset.x >= leftPad && offset.x <= w - rightPad) {
                            val tappedIndex = ((offset.x - leftPad) / barSpacingWidth).toInt().coerceIn(0, barCount - 1)
                            onIndexSelected?.invoke(tappedIndex)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Padding offsets
            val leftPad = with(density) { 54.dp.toPx() }
            val rightPad = with(density) { 16.dp.toPx() }
            val topPad = with(density) { 20.dp.toPx() }
            val bottomPad = with(density) { 46.dp.toPx() }

            val chartWidth = w - leftPad - rightPad
            val chartHeight = h - topPad - bottomPad
            val bottomY = h - bottomPad

            // 1. Draw Axis Lines
            // Y Axis
            drawLine(
                color = onSurfaceColor.copy(alpha = 0.8f),
                start = Offset(leftPad, topPad),
                end = Offset(leftPad, bottomY),
                strokeWidth = 1.5.dp.toPx()
            )
            // Y Axis Arrow (Pointing Up)
            drawLine(
                color = onSurfaceColor.copy(alpha = 0.8f),
                start = Offset(leftPad, topPad),
                end = Offset(leftPad - 6.dp.toPx(), topPad + 10.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = onSurfaceColor.copy(alpha = 0.8f),
                start = Offset(leftPad, topPad),
                end = Offset(leftPad + 6.dp.toPx(), topPad + 10.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )

            // X Axis
            drawLine(
                color = onSurfaceColor.copy(alpha = 0.8f),
                start = Offset(leftPad, bottomY),
                end = Offset(w - rightPad, bottomY),
                strokeWidth = 1.5.dp.toPx()
            )

            // 2. Draw Y-Axis Labels & Tick marks (0% to 100%, step 20%)
            val maxY = 100f
            for (value in 0..100 step 20) {
                val ratio = value / maxY
                val yCoord = bottomY - (ratio * chartHeight)

                // Tick
                drawLine(
                    color = onSurfaceColor.copy(alpha = 0.3f),
                    start = Offset(leftPad - 4.dp.toPx(), yCoord),
                    end = Offset(leftPad, yCoord),
                    strokeWidth = 1.dp.toPx()
                )

                // Label
                val textLayoutResult = textMeasurer.measure(
                    text = "$value%",
                    style = labelStyle
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = leftPad - textLayoutResult.size.width - 8.dp.toPx(),
                        y = yCoord - textLayoutResult.size.height / 2f
                    )
                )
            }

            // 3. Draw Vertical Y-Axis Title rotated by -90 deg
            rotate(degrees = -90f, pivot = Offset(leftPad * 0.35f, h / 2)) {
                val titleLayout = textMeasurer.measure(
                    text = "Routine Completion Score (%)",
                    style = axisTitleStyle
                )
                drawText(
                    textLayoutResult = titleLayout,
                    topLeft = Offset(
                        x = (leftPad * 0.35f) - (titleLayout.size.width / 2f),
                        y = (h / 2f) - (titleLayout.size.height / 2f)
                    )
                )
            }

            // 4. Draw Columns (Teal bars) and X-Axis Labels
            val barCount = values.size
            val barSpacingWidth = chartWidth / barCount
            val barWidth = with(density) { (if (barCount > 8) 10.dp else 16.dp).toPx() } // dynamic bar thickness

            for (i in 0 until barCount) {
                val value = values[i]
                val ratio = value / maxY
                val barHeight = ratio * chartHeight
                val xCenter = leftPad + (i + 0.5f) * barSpacingWidth
                val xLeft = xCenter - barWidth / 2f

                // Highlight active bar with different color
                val barColor = if (i == highlightedIndex) Color(0xFF14B8A6) else Color(0xFF3B9EAC)

                // Draw Bar Rectangle
                drawRect(
                    color = barColor,
                    topLeft = Offset(xLeft, bottomY - barHeight),
                    size = Size(barWidth, barHeight)
                )

                // Tick mark on X-axis
                drawLine(
                    color = onSurfaceColor.copy(alpha = 0.5f),
                    start = Offset(xCenter, bottomY),
                    end = Offset(xCenter, bottomY + 4.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )

                // Draw Label Name
                val labelLayout = textMeasurer.measure(
                    text = labels[i],
                    style = labelStyle.copy(
                        fontWeight = if (i == highlightedIndex) FontWeight.Bold else FontWeight.Medium,
                        color = if (i == highlightedIndex) primaryColor else onSurfaceColor.copy(alpha = 0.7f)
                    )
                )
                drawText(
                    textLayoutResult = labelLayout,
                    topLeft = Offset(
                        x = xCenter - labelLayout.size.width / 2f,
                        y = bottomY + 6.dp.toPx()
                    )
                )
            }

            // 5. Draw horizontal bottom axis label
            val monthsLabelLayout = textMeasurer.measure(
                text = xAxisTitle,
                style = axisTitleStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            )
            drawText(
                textLayoutResult = monthsLabelLayout,
                topLeft = Offset(
                    x = leftPad + (chartWidth / 2f) - (monthsLabelLayout.size.width / 2f),
                    y = h - 20.dp.toPx()
                )
            )
        }
    }
}

@Composable
fun GlowingSmoothWaveGraph(
    values: List<Float>,
    labels: List<String>,
    highlightedIndex: Int,
    watermarkTitle: String = "DAILY HABIT PERSISTENCE STREAK",
    modifier: Modifier = Modifier,
    onIndexSelected: ((Int) -> Unit)? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val surfaceColor = Color(0xFF000000) // Sleek absolute black background from image
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(surfaceColor, RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF222222), RoundedCornerShape(16.dp))
            .padding(12.dp)
            .pointerInput(values, highlightedIndex) {
                detectTapGestures { offset ->
                    val w = size.width.toFloat()
                    val leftPad = with(density) { 46.dp.toPx() }
                    val rightPad = with(density) { 20.dp.toPx() }
                    val chartWidth = w - leftPad - rightPad
                    val count = values.size
                    val maxIndex = if (count > 1) count - 1 else 1
                    if (count > 0) {
                        val stepWidth = chartWidth / maxIndex
                        if (offset.x >= leftPad && offset.x <= w - rightPad) {
                            val tappedIndex = ((offset.x - leftPad) / stepWidth).roundToInt().coerceIn(0, count - 1)
                            onIndexSelected?.invoke(tappedIndex)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Padding offsets
            val leftPad = with(density) { 46.dp.toPx() }
            val rightPad = with(density) { 20.dp.toPx() }
            val topPad = with(density) { 24.dp.toPx() }
            val bottomPad = with(density) { 36.dp.toPx() }

            val chartWidth = w - leftPad - rightPad
            val chartHeight = h - topPad - bottomPad
            val bottomY = h - bottomPad

            val maxVal = 100f
            val count = values.size
            val maxIndex = if (count > 1) count - 1 else 1
            
            val points = (0 until count).map { i ->
                val x = leftPad + (i / maxIndex.toFloat()) * chartWidth
                val percentage = values[i]
                val y = bottomY - (percentage / maxVal) * chartHeight
                Offset(x, y)
            }

            // Grid lines and labels (0%, 20%, 40%, 60%, 80%, 100%)
            for (value in 0..100 step 20) {
                val ratio = value / maxVal
                val yCoord = bottomY - (ratio * chartHeight)

                // Grid line
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(leftPad, yCoord),
                    end = Offset(w - rightPad, yCoord),
                    strokeWidth = 1.dp.toPx()
                )

                // Axis Y Label
                val labelResult = textMeasurer.measure(
                    text = "$value%",
                    style = TextStyle(
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(
                        x = leftPad - labelResult.size.width - 6.dp.toPx(),
                        y = yCoord - labelResult.size.height / 2f
                    )
                )
            }

            // Draw Labels at the bottom
            for (i in 0 until count) {
                val isHighlighted = i == highlightedIndex
                val labelResult = textMeasurer.measure(
                    text = labels[i],
                    style = TextStyle(
                        color = Color.White.copy(alpha = if (isHighlighted) 0.9f else 0.5f),
                        fontSize = 9.sp,
                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal
                    )
                )
                val x = leftPad + (i / maxIndex.toFloat()) * chartWidth
                drawText(
                    textLayoutResult = labelResult,
                    topLeft = Offset(
                        x = x - labelResult.size.width / 2f,
                        y = bottomY + 6.dp.toPx()
                    )
                )
            }

            // Build smooth Bezier path joining all points
            if (points.isNotEmpty()) {
                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 0 until count - 1) {
                        val pStart = points[i]
                        val pEnd = points[i + 1]
                        
                        val cx1 = pStart.x + (pEnd.x - pStart.x) / 2f
                        val cy1 = pStart.y
                        val cx2 = pStart.x + (pEnd.x - pStart.x) / 2f
                        val cy2 = pEnd.y
                        
                        cubicTo(cx1, cy1, cx2, cy2, pEnd.x, pEnd.y)
                    }
                }

                // 1. Draw Filled gradient background under the path
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(points[count - 1].x, bottomY)
                    lineTo(points[0].x, bottomY)
                    close()
                }

                val fillBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF8B5CF6).copy(alpha = 0.45f), // Rich purple/violet
                        Color(0xFFEC4899).copy(alpha = 0.15f), // Pink magenta
                        Color.Transparent
                    ),
                    startY = topPad,
                    endY = bottomY
                )
                drawPath(path = fillPath, brush = fillBrush)

                // 2. Draw glowing outline stroke multiple times for neon effect
                val strokeBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF472B6), // beautiful soft pink
                        Color(0xFF8B5CF6), // dynamic purple
                        Color(0xFFFFFFFF)  // pure white highlight
                    ),
                    start = points[0],
                    end = points[count - 1]
                )

                // Outer wide halo glow
                drawPath(
                    path = path,
                    brush = strokeBrush,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                    alpha = 0.15f
                )
                // Core stroke
                drawPath(
                    path = path,
                    brush = strokeBrush,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // 3. Draw Peak Glowing circle on active index if valid
                if (highlightedIndex in 0 until count) {
                    val highlightedPoint = points[highlightedIndex]

                    // Outer halo glow
                    drawCircle(
                        color = Color(0xFFC084FC),
                        radius = 12.dp.toPx(),
                        center = highlightedPoint,
                        alpha = 0.4f
                    )
                    // Ring glow
                    drawCircle(
                        color = Color.White,
                        radius = 8.dp.toPx(),
                        center = highlightedPoint,
                        alpha = 0.8f
                    )
                    // Direct solid core white
                    drawCircle(
                        color = Color.White,
                        radius = 4.5.dp.toPx(),
                        center = highlightedPoint
                    )
                }
            }

            // Draw a sleek watermark / title
            val subtitleResult = textMeasurer.measure(
                text = watermarkTitle,
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textLayoutResult = subtitleResult,
                topLeft = Offset(leftPad, 0f)
            )
        }
    }
}

@Composable
fun WeeklyInsightView(
    visualMode: String,
    allRoutines: List<RoutineItem>
) {
    val weeklyPair = remember(allRoutines) {
        getWeeklyPercentages(allRoutines)
    }
    val currentDayIndex = remember {
        (LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
    }
    var selectedDayIndex by remember { mutableStateOf(currentDayIndex) }

    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Habit Analytics",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    val selLabel = weeklyPair.first.getOrNull(selectedDayIndex) ?: ""
                    val selVal = weeklyPair.second.getOrNull(selectedDayIndex) ?: 0f
                    Text(
                        text = "Selected: $selLabel (${selVal.toInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { if (selectedDayIndex > 0) selectedDayIndex-- },
                        enabled = selectedDayIndex > 0
                    ) {
                        Text("← Prev Day", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    
                    Text(
                        text = "Tap graph to select day",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    
                    TextButton(
                        onClick = { if (selectedDayIndex < 6) selectedDayIndex++ },
                        enabled = selectedDayIndex < 6
                    ) {
                        Text("Next Day →", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (visualMode == "graph") {
                    GlowingSmoothWaveGraph(
                        values = weeklyPair.second,
                        labels = weeklyPair.first,
                        highlightedIndex = selectedDayIndex,
                        watermarkTitle = "WEEKLY HABIT COMPLIANCE RATE",
                        onIndexSelected = { selectedDayIndex = it }
                    )
                } else {
                    TealBarDiagram(
                        values = weeklyPair.second,
                        labels = weeklyPair.first,
                        highlightedIndex = selectedDayIndex,
                        xAxisTitle = "← Days of the Current Week →",
                        onIndexSelected = { selectedDayIndex = it }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Calculate week summary stats
        val totalDaysActive = weeklyPair.second.count { it > 0 }
        val averageCompletion = if (weeklyPair.second.isNotEmpty()) weeklyPair.second.average() else 0.0
        
        Text("Weekly Performance Summary", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Days Logged", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$totalDaysActive / 7 Days",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Days with tracked habits",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Average Score", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${averageCompletion.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Average completion rate",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyInsightView(
    visualMode: String,
    allRoutines: List<RoutineItem>
) {
    val monthValues = remember(allRoutines) {
        getMonthlyPercentages(allRoutines)
    }
    val monthLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val currentMonthIndex = remember { 
        (LocalDate.now().monthValue - 1).coerceIn(0, 11)
    }
    var selectedMonthIndex by remember { mutableStateOf(currentMonthIndex) }

    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Yearly Trend Dynamics",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    val selLabel = monthLabels.getOrNull(selectedMonthIndex) ?: ""
                    val selVal = monthValues.getOrNull(selectedMonthIndex) ?: 0f
                    Text(
                        text = "Selected: $selLabel (${selVal.toInt()}%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { if (selectedMonthIndex > 0) selectedMonthIndex-- },
                        enabled = selectedMonthIndex > 0
                    ) {
                        Text("← Prev Month", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    
                    Text(
                        text = "Tap graph to select month",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    
                    TextButton(
                        onClick = { if (selectedMonthIndex < 11) selectedMonthIndex++ },
                        enabled = selectedMonthIndex < 11
                    ) {
                        Text("Next Month →", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (visualMode == "graph") {
                    GlowingSmoothWaveGraph(
                        values = monthValues,
                        labels = monthLabels,
                        highlightedIndex = selectedMonthIndex,
                        watermarkTitle = "MONTHLY HABIT INCEPTION TRENDS",
                        onIndexSelected = { selectedMonthIndex = it }
                    )
                } else {
                    TealBarDiagram(
                        values = monthValues,
                        labels = monthLabels,
                        highlightedIndex = selectedMonthIndex,
                        xAxisTitle = "← Months of the Year →",
                        onIndexSelected = { selectedMonthIndex = it }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Month stats summaries
        val monthsTracked = monthValues.count { it > 0 }
        val maxMonthScore = monthValues.maxOrNull() ?: 0f
        
        Text("Yearly Progress Insights", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Months Active", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$monthsTracked / 12 Months",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Months with completed routine data",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Peak Completion", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${maxMonthScore.toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Highest monthly rate so far",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarInsightView(
    getRoutinesForDateFlow: (String) -> Flow<List<RoutineItem>>,
    visualMode: String,
    allRoutines: List<RoutineItem>
) {
    val currentMonth = YearMonth.now()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    val selectedDateRoutines by getRoutinesForDateFlow(selectedDate.format(df)).collectAsStateWithLifecycle(initialValue = emptyList())
    
    // Calculate selected date fraction
    val selectedCompleted = selectedDateRoutines.count { it.isCompleted }
    val selectedTotal = selectedDateRoutines.count { it.category != "water" }
    
    Column {
        Text(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7), 
            modifier = Modifier.heightIn(max = 210.dp)
        ) {
            items(currentMonth.lengthOfMonth()) { day ->
                val date = currentMonth.atDay(day + 1)
                val isSelected = date == selectedDate
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .padding(2.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, 
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedDate = date },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (day + 1).toString(),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Stats for ${selectedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))}", 
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Completed Habits", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text("$selectedCompleted / $selectedTotal Task(s)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                CircularProgressIndicator(
                    progress = { if (selectedTotal > 0) selectedCompleted.toFloat() / selectedTotal else 0f },
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.5.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        
        if (visualMode == "graph") {
            GlowingSmoothWaveGraph(
                values = getMonthlyPercentages(allRoutines),
                labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                highlightedIndex = LocalDate.now().monthValue - 1,
                watermarkTitle = "MONTHLY HABIT INCEPTION TRENDS"
            )
        } else {
            TealBarDiagram(
                values = getMonthlyPercentages(allRoutines),
                labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
                highlightedIndex = LocalDate.now().monthValue - 1,
                xAxisTitle = "← Months of the Year →"
            )
        }
    }
}
