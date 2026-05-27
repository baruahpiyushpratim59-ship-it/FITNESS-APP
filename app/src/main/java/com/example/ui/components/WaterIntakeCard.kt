package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.model.RoutineItem
import java.util.Locale

@Composable
fun WaterGlassAnimation(
    completedCount: Int,
    targetCount: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (targetCount > 0) completedCount.toFloat() / targetCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow),
        label = "water_fill"
    )

    // Waves phase animation for flowing realism
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Box(
        modifier = modifier
            .size(width = 110.dp, height = 150.dp)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Dimension definitions for our beautiful tumbler glass
            val topWidth = width * 0.82f
            val bottomWidth = width * 0.65f
            val glassHeight = height * 0.86f
            val cornerRadius = 14.dp.toPx()
            val borderThickness = 4.5.dp.toPx()

            val leftX = (width - topWidth) / 2
            val rightX = leftX + topWidth
            val bottomLeftX = (width - bottomWidth) / 2
            val bottomRightX = bottomLeftX + bottomWidth
            val topY = (height - glassHeight) / 2
            val bottomY = topY + glassHeight

            // Inside glass definitions
            val insideLeftX = leftX + borderThickness
            val insideRightX = rightX - borderThickness
            val insideBottomLeftX = bottomLeftX + borderThickness
            val insideBottomRightX = bottomRightX - borderThickness
            val insideTopY = topY
            val insideBottomY = bottomY - borderThickness

            // 1. Draw Glass base shadow
            drawOval(
                color = Color(0xFF030712).copy(alpha = 0.45f),
                topLeft = Offset(bottomLeftX - 12.dp.toPx(), bottomY - 3.dp.toPx()),
                size = Size(bottomWidth + 24.dp.toPx(), 8.dp.toPx())
            )

            // Glass Inside Path for clipping water & tinted empty glass volume
            val glassInsidePath = Path().apply {
                moveTo(insideLeftX, insideTopY)
                lineTo(insideBottomLeftX, insideBottomY - cornerRadius)
                quadraticBezierTo(insideBottomLeftX, insideBottomY, insideBottomLeftX + cornerRadius, insideBottomY)
                lineTo(insideBottomRightX - cornerRadius, insideBottomY)
                quadraticBezierTo(insideBottomRightX, insideBottomY, insideBottomRightX, insideBottomY - cornerRadius)
                lineTo(insideRightX, insideTopY)
                close()
            }

            // Draw empty glass interior background tint (transparent/dark premium)
            drawPath(
                path = glassInsidePath,
                color = Color(0xFF1E293B).copy(alpha = 0.4f)
            )

            // 2. Draw water liquid clipped to inside glass path
            if (animatedProgress > 0.005f) {
                clipPath(glassInsidePath) {
                    val waterSurfaceY = insideBottomY - (insideBottomY - insideTopY) * animatedProgress
                    val waveHeight = 5.dp.toPx()
                    val waveLength = width

                    // Background wave (translucent light water blue)
                    val bgWaterPath = Path().apply {
                        moveTo(-20f, height + 20f)
                        lineTo(-20f, waterSurfaceY)
                        for (x in -20..width.toInt() + 20 step 4) {
                            val relativeX = x.toFloat()
                            val angle = (relativeX / waveLength) * 2f * Math.PI.toFloat() + wavePhase
                            val y = waterSurfaceY + Math.sin(angle.toDouble()).toFloat() * waveHeight
                            lineTo(relativeX, y)
                        }
                        lineTo(width + 20f, height + 20f)
                        close()
                    }
                    drawPath(
                        path = bgWaterPath,
                        color = Color(0xFF38BDF8).copy(alpha = 0.45f)
                    )

                    // Foreground wave (solid premium cyan-water blue)
                    val fgWaterPath = Path().apply {
                        moveTo(-20f, height + 20f)
                        lineTo(-20f, waterSurfaceY)
                        for (x in -20..width.toInt() + 20 step 4) {
                            val relativeX = x.toFloat()
                            val angle = (relativeX / waveLength) * 2f * Math.PI.toFloat() - wavePhase + Math.PI.toFloat()
                            val y = waterSurfaceY + Math.sin(angle.toDouble()).toFloat() * waveHeight
                            lineTo(relativeX, y)
                        }
                        lineTo(width + 20f, height + 20f)
                        close()
                    }
                    drawPath(
                        path = fgWaterPath,
                        color = Color(0xFF0284C7)
                    )

                    // Draw subtle bubble/particle accents inside the water
                    val bubbleOffset1 = Offset(insideLeftX + (insideRightX - insideLeftX) * 0.3f, insideBottomY - (insideBottomY - insideTopY) * animatedProgress * 0.35f)
                    val bubbleOffset2 = Offset(insideLeftX + (insideRightX - insideLeftX) * 0.7f, insideBottomY - (insideBottomY - insideTopY) * animatedProgress * 0.65f)
                    drawCircle(Color.White.copy(alpha = 0.28f), radius = 2.5f.dp.toPx(), center = bubbleOffset1)
                    drawCircle(Color.White.copy(alpha = 0.20f), radius = 1.8f.dp.toPx(), center = bubbleOffset2)
                }
            }

            // 3. Draw outer glass frame shape (beaker/tumbler glassware border)
            val glassOuterPath = Path().apply {
                moveTo(leftX, topY)
                lineTo(bottomLeftX, bottomY - cornerRadius)
                quadraticBezierTo(bottomLeftX, bottomY, bottomLeftX + cornerRadius, bottomY)
                lineTo(bottomRightX - cornerRadius, bottomY)
                quadraticBezierTo(bottomRightX, bottomY, bottomRightX, bottomY - cornerRadius)
                lineTo(rightX, topY)
            }
            drawPath(
                path = glassOuterPath,
                color = Color.White.copy(alpha = 0.32f),
                style = Stroke(width = borderThickness)
            )

            // 4. Glossy highlights/reflections
            drawLine(
                color = Color.White.copy(alpha = 0.18f),
                start = Offset(leftX + 8.dp.toPx(), topY + 6.dp.toPx()),
                end = Offset(bottomLeftX + 5.dp.toPx(), bottomY - 12.dp.toPx()),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun WaterTargetChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF2563EB).copy(alpha = 0.2f) else Color.Transparent
    val borderColor = if (isSelected) Color(0xFF0EA5E9) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    val textColor = if (isSelected) Color(0xFF38BDF8) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun WaterIntakeCard(
    waterItem: RoutineItem?,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onUpdateTarget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = waterItem?.completedCount ?: 0
    val target = waterItem?.targetCount ?: 8

    // Liters equivalents: 1 click = 0.25 L
    val completedLiters = completed * 0.25f
    val targetLiters = target * 0.25f

    // Standard Preset targets (in Liters) mapping to actual click-counts in integers
    val targetsList = listOf(
        Pair("1.5 L", 6),
        Pair("2.0 L", 8),
        Pair("2.5 L", 10),
        Pair("3.0 L", 12),
        Pair("3.5 L", 14),
        Pair("4.0 L", 16)
    )

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main card layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left hand side: Custom Animated Water Glass
                WaterGlassAnimation(
                    completedCount = completed,
                    targetCount = target,
                    modifier = Modifier.size(110.dp, 130.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Right hand side: Description, Data tracker & control buttons
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "HYDRATION ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0EA5E9),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Water Intake Tracker",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    // Liters Display
                    Text(
                        text = String.format(Locale.US, "%.2fL / %.2fL", completedLiters, targetLiters),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8)
                    )
                    Text(
                        text = "$completed of $target glasses (250ml each)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plus and Minus controllers for intake adjustments
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Minus Button
                        IconButton(
                            onClick = onDecrement,
                            enabled = completed > 0,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                .testTag("water_decrement_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Reduce water by standard glass",
                                tint = if (completed > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Plus Button
                        IconButton(
                            onClick = onIncrement,
                            enabled = completed < target,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7))
                                .testTag("water_increment_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add a 250ml glass of water",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Choose Daily Target Section
            Text(
                text = "Adjust Daily Target Plan:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Horizontal Scrollable targets chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(targetsList) { pair ->
                    val (label, targetCountValue) = pair
                    val isSelected = target == targetCountValue
                    WaterTargetChip(
                        label = label,
                        isSelected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                onUpdateTarget(targetCountValue)
                            }
                        }
                    )
                }
            }

            // Encouragement Banner
            if (completed >= target) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "🌟 Superb! Daily hydration target completed!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF34D399),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun WaterIntakeScreen(
    waterItem: RoutineItem?,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onUpdateTarget: (Int) -> Unit,
    onBack: () -> Unit
) {
    val completed = waterItem?.completedCount ?: 0
    val target = waterItem?.targetCount ?: 8

    val completedLiters = completed * 0.25f
    val targetLiters = target * 0.25f

    val targetsList = listOf(
        Pair("1.5 L", 6),
        Pair("2.0 L", 8),
        Pair("2.5 L", 10),
        Pair("3.0 L", 12),
        Pair("3.5 L", 14),
        Pair("4.0 L", 16)
    )

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
                        .testTag("water_back_button")
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
                        text = "HYDRATION GOAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Water Intake Tracker",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Main Glass Canvas Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Larger water glass with realistic wave ripple animation
                    WaterGlassAnimation(
                        completedCount = completed,
                        targetCount = target,
                        modifier = Modifier
                            .size(170.dp, 230.dp)
                            .padding(vertical = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Liters Display with elegant neon styling
                    Text(
                        text = String.format(Locale.US, "%.2f L / %.2f L", completedLiters, targetLiters),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$completed of $target glasses completed (250ml each)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Large, modern touch buttons to adjust amount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // MINUS
                        IconButton(
                            onClick = onDecrement,
                            enabled = completed > 0,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (completed > 0) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
                                )
                                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), CircleShape)
                                .testTag("water_screen_decrement")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease water intake count",
                                tint = if (completed > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(32.dp))

                        // PLUS
                        IconButton(
                            onClick = onIncrement,
                            enabled = completed < target,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7))
                                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f), CircleShape)
                                .testTag("water_screen_increment")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase water intake count",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Target Adjustment Section
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Customize Daily Target Plan:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(targetsList) { pair ->
                            val (label, targetCountValue) = pair
                            val isSelected = target == targetCountValue
                            WaterTargetChip(
                                label = label,
                                isSelected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        onUpdateTarget(targetCountValue)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Informative/Inspirational box
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Infotip logo",
                        tint = Color(0xFF0EA5E9),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Did you know? Regular water intake improves energy levels, enhances brain function, and helps maintain peak performance.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

