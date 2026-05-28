package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.time.format.DateTimeFormatter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow


@Composable
fun InsightsDialog(
    onDismiss: () -> Unit,
    getRoutinesForDateFlow: (String) -> Flow<List<RoutineItem>>
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(0.9f).heightIn(max = 600.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Insights", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Day") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Calendar") })
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (selectedTab == 0) {
                    DayInsightView(getRoutinesForDateFlow)
                } else {
                    CalendarInsightView(getRoutinesForDateFlow)
                }
            }
        }
    }
}

@Composable
fun DayInsightView(getRoutinesForDateFlow: (String) -> Flow<List<RoutineItem>>) {
    val df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now().format(df)
    val yesterday = LocalDate.now().minusDays(1).format(df)
    
    val todayRoutines by getRoutinesForDateFlow(today).collectAsStateWithLifecycle(initialValue = emptyList())
    val yestRoutines by getRoutinesForDateFlow(yesterday).collectAsStateWithLifecycle(initialValue = emptyList())
    
    Column {
        Text("Today", fontWeight = FontWeight.Bold)
        RoutineBarGraph(todayRoutines)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Yesterday", fontWeight = FontWeight.Bold)
        RoutineBarGraph(yestRoutines)
    }
}

@Composable
fun RoutineBarGraph(routines: List<RoutineItem>) {
    val categories = routines.map { it.category }.distinct()
    
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        categories.forEach { category ->
            val catRoutines = routines.filter { it.category == category }
            val completed = catRoutines.count { it.isCompleted }
            val total = catRoutines.size
            val fraction = if (total == 0) 0f else completed.toFloat() / total
            
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(text = category.replaceFirstChar { it.uppercase() }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun CalendarInsightView(getRoutinesForDateFlow: (String) -> Flow<List<RoutineItem>>) {
    val currentMonth = YearMonth.now()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    val selectedDateRoutines by getRoutinesForDateFlow(selectedDate.format(df)).collectAsStateWithLifecycle(initialValue = emptyList())
    
    Column {
        Text(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), fontWeight = FontWeight.Bold)
        
        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.heightIn(max = 200.dp)) {
            items(currentMonth.lengthOfMonth()) { day ->
                val date = currentMonth.atDay(day + 1)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                        .background(if (date == selectedDate) MaterialTheme.colorScheme.primary.copy(alpha=0.5f) else Color.Transparent, RoundedCornerShape(4.dp))
                        .clickable { selectedDate = date },
                    contentAlignment = Alignment.Center
                ) {
                    Text((day + 1).toString())
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Stats for ${selectedDate.format(df)}", fontWeight = FontWeight.Bold)
        RoutineBarGraph(selectedDateRoutines)
    }
}
