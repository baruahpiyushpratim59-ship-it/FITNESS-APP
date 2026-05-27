package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoutineItem

@Composable
fun WaterIntakeCard(
    waterItem: RoutineItem?,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = waterItem?.completedCount ?: 0
    val target = waterItem?.targetCount ?: 8

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HYDRATION GOAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Water Intake Tracker",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Decrement Button
                    IconButton(
                        onClick = onDecrement,
                        enabled = completed > 0,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x15FFFFFF))
                            .testTag("water_decrement_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove water cup",
                            tint = if (completed > 0) Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "$completed/$target",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF60A5FA)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Increment Button
                    IconButton(
                        onClick = onIncrement,
                        enabled = completed < target,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB))
                            .testTag("water_increment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add water cup",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Beautiful cup grid block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..target) {
                    val isHydrated = i <= completed
                    val cupTint by animateColorAsState(
                        targetValue = if (isHydrated) Color(0xFF38BDF8) else Color(0x33FFFFFF),
                        animationSpec = spring(dampingRatio = 0.6f),
                        label = "cup_glow"
                    )

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = if (isHydrated) "Hydrated cup" else "Empty cup",
                        tint = cupTint,
                        modifier = Modifier
                            .weight(1f)
                            .size(if (isHydrated) 28.dp else 24.dp)
                            .clickable {
                                if (isHydrated && i == completed) {
                                    onDecrement()
                                } else if (!isHydrated && i == completed + 1) {
                                    onIncrement()
                                }
                            }
                    )
                }
            }

            if (completed >= target) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "🌟 Fantastic! Daily hydration goal completed!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF34D399),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
