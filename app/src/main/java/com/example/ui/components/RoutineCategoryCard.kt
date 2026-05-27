package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoutineItem

@Composable
fun RoutineCategoryCard(
    title: String,
    category: String,
    icon: ImageVector,
    accentColor: Color,
    tasks: List<RoutineItem>,
    isAILoading: Boolean,
    onToggleComplete: (RoutineItem) -> Unit,
    onDelete: (Int) -> Unit,
    onAskAI: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Icon Frame
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "$title icon",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val completedCount = tasks.count { it.isCompleted }
                        val totalCount = tasks.size
                        Text(
                            text = if (totalCount == 0) "No tasks" else "$completedCount of $totalCount completed",
                            fontSize = 12.sp,
                            color = if (completedCount == totalCount && totalCount > 0) Color(0xFF34D399) else Color(0x99FFFFFF)
                        )
                    }
                }

                // AI Sparkle quick-planning trigger
                if (category != "water") {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isAILoading) Color(0x22FFFFFF)
                                else accentColor.copy(alpha = 0.15f)
                            )
                            .clickable {
                                onAskAI(category)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("ai_generate_button_$category"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isAILoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    strokeWidth = 1.5.dp,
                                    color = accentColor
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Action",
                                    tint = accentColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ask AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Expanding checklist items
            AnimatedVisibility(visible = isExpanded || tasks.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    tasks.forEach { task ->
                        RoutineTaskRow(
                            task = task,
                            accentColor = accentColor,
                            onToggleComplete = { onToggleComplete(task) },
                            onDelete = { onDelete(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoutineTaskRow(
    task: RoutineItem,
    accentColor: Color,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    // Checkbox animated fill color
    val checkboxBg by animateColorAsState(
        targetValue = if (task.isCompleted) accentColor else Color.Transparent,
        label = "checkbox_color"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x05FFFFFF))
            .padding(8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top
        ) {
            // Elegant micro check button
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(checkboxBg)
                    .border(
                        1.5.dp,
                        if (task.isCompleted) accentColor else Color(0x44FFFFFF),
                        CircleShape
                    )
                    .clickable { onToggleComplete() }
                    .testTag("checkbox_${task.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed mark",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.isCompleted) Color(0x66FFFFFF) else Color.White,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.scheduledTime.isNotBlank() && task.scheduledTime != "Anytime") {
                        Spacer(modifier = Modifier.width(8.dp))
                        // Time Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(accentColor.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.scheduledTime,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }

                if (task.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.notes,
                        fontSize = 12.sp,
                        color = Color(0xB3FFFFFF),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Delete button for custom ones or optional cleanup
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(24.dp)
                .testTag("delete_task_${task.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete routine item",
                tint = Color(0x44FFFFFF),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
